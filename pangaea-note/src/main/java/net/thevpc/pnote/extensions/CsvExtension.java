/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.extensions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.thevpc.pnote.api.PangaeaNoteAppExtension;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteField;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class CsvExtension implements PangaeaNoteFileImporter, PangaeaNoteAppExtension {

    public CsvExtension() {
    }

    @Override
    public String getName() {
        return "CSV";
    }

    @Override
    public void onLoad(PangaeaNoteApp app) {
        app.service().installFileImporter(this);
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[]{"csv"};
    }

    private List<List<String>> loadCSV(InputStream stream, PangaeaNoteService service) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        TokenConf tokenConf = new TokenConf();
        List<List<String>> all = new ArrayList<>();
        while (true) {
            List<String> line = parseRow(br, service, tokenConf);
            if (line != null && line.size() > 0) {
                all.add(line);
            } else {
                break;
            }
        }
        return all;
    }

    private static class TokenConf {

        boolean sepComa = true;
        boolean sepSemiColon = true;
        boolean sepTab = true;
        boolean dblQte = true;
        boolean smplQte = true;

    }

    private static class Token {

        static int TEXT = 1;
        static int SEPARATOR = 2;
        static int NEWLINE = 3;
        int type;
        String sval;
        boolean trimmable;

        public Token(int type, String sval, boolean trimmable) {
            this.type = type;
            this.sval = sval;
            this.trimmable = trimmable;
        }

    }

    private Token parseQuotedStringToken(Reader line, char c0, PangaeaNoteService service, TokenConf cnf) throws IOException {
        boolean inLoop = true;
        char q = c0;
        StringBuilder sb = new StringBuilder();
        while (inLoop) {
            int i = line.read();
            if (i < 0 || i == q) {
                break;
            } else if (i == '\\') {
                int i2 = line.read();
                sb.append((char) i2);
            } else {
                sb.append((char) i);
            }
        }
        return new Token(Token.TEXT, sb.toString(), false);
    }

    private Token parseUnquotedStringToken(Reader line, StringBuilder sb, PangaeaNoteService service, TokenConf cnf) throws IOException {
        boolean inLoop = true;
        while (inLoop) {
            line.mark(1);
            int i = line.read();
            if (i < 0) {
                break;
            } else {
                char c = (char) i;
                switch (c) {
                    case ',':
                    case '\t':
                    case ';': {
                        if ((c == ',' && cnf.sepComa)
                                || (c == ';' && cnf.sepSemiColon)
                                || (c == '\t' && cnf.sepTab)) {
                            line.reset();
                            inLoop = false;
                        } else {
                            sb.append(c);
                        }
                        break;
                    }
                    default: {
                        sb.append(c);
                        break;
                    }
                }
            }
        }
        return new Token(Token.TEXT, sb.toString(), true);
    }

    private Token parseToken(Reader line, PangaeaNoteService service, TokenConf cnf) {
        try {
            int i = line.read();
            if (i < 0) {
                return null;
            }
            char c = (char) i;
            switch (c) {
                case ',':
                case '\t':
                case ';': {
                    if ((c == ',' && cnf.sepComa)
                            || (c == ';' && cnf.sepSemiColon)
                            || (c == '\t' && cnf.sepTab)) {
                        return new Token(Token.SEPARATOR, String.valueOf(c), false);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    return parseUnquotedStringToken(line, sb, service, cnf);
                }
                case '\r': {
                    line.mark(1);
                    i = line.read();
                    if (i == '\n') {
                        return new Token(Token.NEWLINE, "\r\n", false);
                    } else if (i == -1) {
                        return new Token(Token.NEWLINE, "\r", false);
                    } else {
                        line.reset();
                        return new Token(Token.NEWLINE, "\r", false);
                    }
                }
                case '\n': {
                    return new Token(Token.NEWLINE, "\n", false);
                }
                case '\"':
                case '\'': {
                    if ((c == '\"' && cnf.dblQte)
                            || (c == '\'' && cnf.smplQte)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(c);
                        return parseQuotedStringToken(line, c, service, cnf);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    return parseUnquotedStringToken(line, sb, service, cnf);
                }
                default: {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    return parseUnquotedStringToken(line, sb, service, cnf);
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private List<String> parseRow(Reader ll, PangaeaNoteService service, TokenConf cnf) {
        List<String> a = new ArrayList<>();
        Token t = null;
        boolean wasSep = true;
        while ((t = parseToken(ll, service, cnf)) != null) {
            if (t.type == Token.SEPARATOR) {
                if (wasSep) {
                    a.add("");
                } else {
                    wasSep = true;
                }
            } else if (t.type == Token.TEXT) {
                wasSep = false;
                String s = t.sval;
                if (t.trimmable) {
                    s = s.trim();
                }
                a.add(s);
            } else if (t.type == Token.NEWLINE) {
                return a;
            }
        }
        return a;
    }

    private PangaeaNote findGroup(PangaeaNote n, String group, PangaeaNoteService service) {
        if (group == null || group.length() == 0) {
            return n;
        }
        String[] gg = Arrays.stream(group.trim().split("/")).map(x -> x.trim()).filter(x -> x.length() > 0).toArray(String[]::new);
        if (gg.length == 0) {
            return n;
        }
        PangaeaNote p = n;
        for (int i = 0; i < gg.length; i++) {
            String c = gg[i];
            PangaeaNote next = null;
            for (PangaeaNote cc : p.getChildren()) {
                if (Objects.equals(cc.getName(), c)) {
                    next = cc;
                    break;
                }
            }
            if (next == null) {
                next = new PangaeaNote();
                next.setName(c);
                next.setContentType(PangaeaNotePlainTextService.PLAIN.toString());
                next.setContent(service.stringToElement(""));
                p.getChildren().add(next);
            }
            p = next;
        }
        return p;
    }

    @Override
    public PangaeaNote loadNote(InputStream stream, String preferredName, String fileExtension, PangaeaNoteService service) {
        if ("csv".equals(fileExtension)) {
            List<List<String>> lines = loadCSV(stream, service);
            PangaeaNote n = new PangaeaNote().setName(preferredName)
                    .setContentType(PangaeaNotePlainTextService.PLAIN.toString())
                    .setContent(service.stringToElement(""));
            if (!lines.isEmpty()) {
                List<String> header = lines.remove(0);
                PangaeaNoteFormsService s = (PangaeaNoteFormsService) service.getContentTypeService(PangaeaNoteFormsService.FORMS);
                PangaeaNoteObjectDescriptor descr = new PangaeaNoteObjectDescriptor();
                Map<Integer, String> indexToHeader = new HashMap<>();
                for (int hi = 0; hi < header.size(); hi++) {
                    String h = header.get(hi);
                    indexToHeader.put(hi, h);
                    PangaeaNoteFieldType tt = PangaeaNoteFieldType.TEXT;
                    String hlc = h.toLowerCase();
                    if (hlc.contains("password") || hlc.contains("pwd")) {
                        tt = PangaeaNoteFieldType.PASSWORD;
                    } else if (hlc.contains("password") || hlc.contains("pwd")) {
                        tt = PangaeaNoteFieldType.PASSWORD;
                    } else if (hlc.equalsIgnoreCase("note")
                            || hlc.equalsIgnoreCase("notes")
                            || hlc.equalsIgnoreCase("desc")
                            || hlc.equalsIgnoreCase("description")
                            || hlc.equalsIgnoreCase("descriptions")
                            || hlc.equalsIgnoreCase("obs")
                            || hlc.equalsIgnoreCase("observation")
                            || hlc.equalsIgnoreCase("observations")
                            || hlc.equalsIgnoreCase("extra")) {
                        tt = PangaeaNoteFieldType.TEXTAREA;
                    } else if (hlc.equals("icon")) {
                        continue;
//                    } else if (hlc.equals("group")) {
//                        continue;
                    }
                    descr.addField(new PangaeaNoteFieldDescriptor().setName(h).setType(tt));
                }
                int entryIndex = 0;
                for (List<String> line : lines) {
                    descr = descr.copy();
                    PangaeaNoteObjectDocument doc = new PangaeaNoteObjectDocument();
                    doc.setDescriptor(descr);
                    entryIndex++;
                    String groupString = "";
                    String nameString = "";
                    String iconString = "";
                    PangaeaNoteObject o = new PangaeaNoteObject();
                    int hsize = descr.getFields().size();
                    for (int i = 0; i < line.size(); i++) {
                        if (i >= hsize) {
                            for (int j = hsize; j < i; j++) {
                                String newColName = "COL-" + (j + 1);
                                indexToHeader.put(j, newColName);
                                descr.addField(new PangaeaNoteFieldDescriptor().setName(newColName).setType(PangaeaNoteFieldType.TEXT));
                            }
                        }
                        String colVal = line.get(i);
                        String colName = indexToHeader.get(i);
                        if ("group".equalsIgnoreCase(colName)) {
                            groupString = colVal;
                            o.addField(new PangaeaNoteField(colName, colVal));
                        } else if ("name".equalsIgnoreCase(colName) || "title".equalsIgnoreCase(colName)) {
                            nameString = colVal;
                            o.addField(new PangaeaNoteField(colName, colVal));
                        } else if ("icon".equalsIgnoreCase(colName)) {
                            iconString = colVal;
                        } else if ("totp".equalsIgnoreCase(colName)) {
                            if (colVal.length() > 0) {
                                o.addField(new PangaeaNoteField(colName, colVal));
                            } else {
                                doc.removeField(colName);
                            }
                        } else {
                            o.addField(new PangaeaNoteField(colName, colVal));
                        }
                    }
                    doc.addObject(o);
                    PangaeaNote q = new PangaeaNote().setName(nameString.isEmpty() ? ("entry " + entryIndex) : nameString);
                    if (iconString.isEmpty()) {
                        iconString = null;
                    } else {
                        Integer iconIndex = OtherUtils.parseInt(iconString);
                        if (iconIndex == null) {
                            iconIndex = iconString.hashCode();
                        }
                        String[] icons = service.getAllIcons().toArray(new String[0]);
                        iconString = icons[Math.abs(iconIndex) % icons.length];
                    }
                    q.setContentType(PangaeaNoteFormsService.FORMS.toString());
                    q.setContent(s.getContentAsElement(doc));
                    q.setIcon(iconString);
                    PangaeaNote gg = findGroup(n, groupString, service);
                    gg.getChildren().add(q);
                }
                return n;

            }
        }
        throw new IllegalArgumentException("unsupported type " + fileExtension);
    }
}
