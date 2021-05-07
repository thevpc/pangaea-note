/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author vpc
 */
public class OtherUtils {

    public static String getFileExtension(String name) {
        name = name.replace('\\', '/');
        int x = name.lastIndexOf('/');
        if (x >= 0) {
            name = name.substring(x + 1);
        }
        x = name.lastIndexOf('.');
        String suffix = "";
        if (x >= 0) {
            suffix = name.substring(x + 1);
        }
        return suffix;
    }

    public static String trim(String in) {
        return in == null ? "" : in.trim();
    }

    public static boolean isBlank(String in) {
        return in == null || in.trim().isEmpty();
    }

    public static String toEscapedName(String in) {
        return toEscapedString(in, '`', false, "<no-name>");
    }

    public static String toEscapedValue(String in) {
        return toEscapedString(in, '\"', true, "null");
    }

    public static String toEscapedString(String in, char quoteType, boolean always, String nullValue) {
        if (in == null) {
            if (nullValue == null) {
                if (always && quoteType != '\0') {
                    return "null";
                }
                return "<null>";
            } else {
                return nullValue;
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean hasSpace = true;
        for (char c : in.toCharArray()) {
            switch (c) {
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                case ' ': {
                    if (quoteType == '\0') {
                        sb.append("\\ ");
                    } else {
                        sb.append(" ");
                    }
                    hasSpace = true;
                    break;
                }
                case '`':
                case '\'':
                case '\"': {
                    if (quoteType == c) {
                        sb.append('\\').append(c);
                    } else {
                        sb.append(c);
                    }
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        if (hasSpace) {
            always = true;
        }
        if (always && quoteType != '\0') {
            sb.insert(0, quoteType);
            sb.append(quoteType);
        }
        return sb.toString();
    }

    public static byte[] toByteArray(InputStream in) {

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            // read bytes from the input stream and store them in buffer
            while ((len = in.read(buffer)) != -1) {
                // write bytes from the buffer into output stream
                os.write(buffer, 0, len);
            }

            return os.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static <T> boolean switchListValues(List<T> values, int index1, int index2) {
        if (values != null) {
            if (index1 >= 0 && index1 < values.size()) {
                if (index2 >= 0 && index2 < values.size()) {
                    T a = values.get(index1);
                    values.set(index1, values.get(index2));
                    values.set(index2, a);
                }
            }
        }
        return false;
    }

    public static String escapeHtml(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static String toHex(int value, int pad) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(value));
        while (sb.length() < pad) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    private static Properties EXT_TO_MIMETYPE;

    public static Properties getExtensionToProperties() {
        if (EXT_TO_MIMETYPE != null) {
            return EXT_TO_MIMETYPE;
        }
        URL url = OtherUtils.class.getResource("/net/thevpc/pnote/extension-to-mimetype.properties");
        Properties p = new Properties();
        try (InputStream is = url.openStream()) {
            p.load(url.openStream());
        } catch (Exception ex) {
            //
        }
        Properties p2 = new Properties();
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            String[] ks = k.trim().split(",");
            for (String k1 : ks) {
                p2.put(k1, v);
            }
        }
        return EXT_TO_MIMETYPE = p2;
    }

    public static String probeContentTypeByName(String path) {
        String fe = getFileExtension(path).toLowerCase();
        String a = getExtensionToProperties().getProperty(fe);
        if (a == null) {
            return "application/octet-stream";
        }
        return a;
    }

    public static String probeContentType(String path) {
        String probedContentType = null;
        File asFile = asFile(path);
        if (asFile != null) {
            try {
                probedContentType = Files.probeContentType(asFile.toPath());
            } catch (IOException ex) {
                //
            }
        }
        if (probedContentType == null) {
            URL asUrl = asURL(path);
            if (asUrl != null) {
                try {
                    URLConnection hc = asUrl.openConnection();
                    hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                    probedContentType = hc.getContentType();
                } catch (IOException ex) {
                    //
                }
            }
        }
        if (probedContentType == null) {
            probedContentType = probeContentTypeByName(path);
        }
        if (probedContentType == null) {
            probedContentType = "application/octet-stream";
        }
        return probedContentType;
    }

    public static URL asURL(String url) {
        try {
            return new URL(url);
        } catch (Exception ex) {
        }
        //this is a file?
        File file1 = null;
        try {
            file1 = new File(url);
            return file1.toURI().toURL();
        } catch (Exception ex) {
        }
        return null;
    }

    public static File asFile(String url) {
        File file1 = null;
        try {
            if (url.startsWith("file:")) {
                if (url.contains(" ")) {//TODO: DIRTY fix me, 
                    url = url.replaceAll(" ", "%20");
                }
                URL u = new URL(url);
                URI uri = u.toURI();

                if (uri.getAuthority() != null && uri.getAuthority().length() > 0) {
                    // Hack for UNC Path
                    uri = (new URL("file://" + url.substring("file:".length()))).toURI();
                }
                File file = new File(uri);
                if (file.exists()) {
                    return file;
                }
            }
            file1 = new File(url);
            if (file1.exists()) {
                return file1;
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public static Integer parseInt(String s) {
        if (s != null) {
            try {
                return Integer.parseInt(s);
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }

    public static String getFileName(String s) {
        if (s == null) {
            return "";
        }
        int i = s.indexOf('?');
        if (i >= 0) {
            s = s.substring(0, i);
        }
        while (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0, s.length() - 1);
        }
        i = s.lastIndexOf('/');
        int i2 = s.lastIndexOf('\\');
        if (i2 > i) {
            i = i2;
        }
        if (i > 0) {
            return s.substring(i + 1);
        }
        return s;
    }
}
