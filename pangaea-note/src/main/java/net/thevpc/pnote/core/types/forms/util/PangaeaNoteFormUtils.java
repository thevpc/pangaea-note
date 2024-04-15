package net.thevpc.pnote.core.types.forms.util;

import net.thevpc.echo.Color;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.pnote.core.frame.util.PangaeaNoteLabelHelper;
import net.thevpc.pnote.core.types.forms.model.*;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PangaeaNoteFormUtils {
    public static boolean isBlank(PangaeaNoteField field) {
        if (field != null) {
            if (!NBlankable.isBlank(field.getValue())) {
                return false;
            }
            if (field.getOptions() != null) {
                if (!NBlankable.isBlank(field.getOptions().getContentType())) {
                    return false;
                }
                if (!NBlankable.isBlank(field.getOptions().getValuePattern())) {
                    return false;
                }
                if (!NBlankable.isBlank(field.getOptions().getLabelName())) {
                    return false;
                }
                if (!NBlankable.isBlank(field.getOptions().getLabelBackgroundColor())) {
                    return false;
                }
                if (!NBlankable.isBlank(field.getOptions().getLabelForegroundColor())) {
                    return false;
                }
                if (!NBlankable.isBlank(field.getOptions().getLabelIcon())) {
                    return false;
                }
                if (!NBlankable.isBlank(field.getOptions().getLabelName())) {
                    return false;
                }
                if (field.getOptions().getHidden() != null) {
                    return false;
                }
                if (field.getOptions().getContentReadOnly() != null) {
                    return false;
                }
                if (field.getOptions().getLabelBold() != null) {
                    return false;
                }
                if (field.getOptions().getLabelItalic() != null) {
                    return false;
                }
                if (field.getOptions().getLabelStriked() != null) {
                    return false;
                }
                if (field.getOptions().getLabelUnderlined() != null) {
                    return false;
                }
            }
        }
        if (field != null && !field.equals(new PangaeaNoteField())) {
            //last fall through!
            return false;
        }
        return true;
    }

    public static String getFieldName(PangaeaNoteFieldDescriptor fdesc) {
        String fn1 = NStringUtils.trim(fdesc.getName());
        String fn2 = NStringUtils.trim(fdesc.getOptions().getLabelName());
        if (fn2.length() > 0) {
            fn1 = fn2;
        }
        return fn1;
    }

    public static void changeFieldType(PangaeaNoteField field, PangaeaNoteFieldType fromType, PangaeaNoteFieldType toField, List<String> fieldValues) {
        if (fromType == toField) {
            return;
        }
        if (fromType.isFreeTextType()) {
            if (toField.isSelectOne()) {
                LinkedHashSet<String> allValues = new LinkedHashSet<>(fieldValues);
                String s = field.getValue();
                if (s == null) {
                    s = "";
                    field.setValue(s);
                }
                allValues.add(s);
                if (!new ArrayList<>(allValues).equals(fieldValues)) {
                    fieldValues.clear();
                    fieldValues.addAll(allValues);
                }
            } else if (toField.isSelectMulti()) {
                LinkedHashSet<String> allValues = new LinkedHashSet<>(fieldValues);
                String s = field.getValue();
                if (s == null) {
                    s = "";
                    field.setValue(s);
                }
                Set<String> selected = new LinkedHashSet<>();
                for (String nv : s.split("[\n,;]")) {
                    nv = nv.trim();
                    selected.add(nv);
                }
                field.setValue(String.join("\n", selected));
                allValues.addAll(selected);
                if (!new ArrayList<>(allValues).equals(fieldValues)) {
                    fieldValues.clear();
                    fieldValues.addAll(allValues);
                }
            }
        } else if (fromType.isSelectOne()) {
            //do nothing, can be transformed to any other...
        } else if (fromType.isSelectMulti()) {
            if (toField.isFreeTextType()) {
                if (toField.isFreeTextTypeAcceptingNewLine()) {
                    //do nothing
                } else {
                    //replace newline with ';'
                    String s = field.getValue();
                    if (s == null) {
                        s = "";
                    }
                    Set<String> selected = new LinkedHashSet<>();
                    for (String nv : s.split("\n")) {
                        nv = nv.trim();
                        selected.add(nv);
                    }
                    field.setValue(String.join(";", selected));
                }
            } else if (toField.isSelectOne()) {
                //expand multiple selections and select only the first one... (non bijective)!!
                LinkedHashSet<String> allValues = new LinkedHashSet<>(fieldValues);
                String s = field.getValue();
                if (s == null) {
                    s = "";
                }
                Set<String> selected = new LinkedHashSet<>();
                for (String nv : s.split("\n")) {
                    nv = nv.trim();
                    selected.add(nv);
                }
                //only first is selected!
                if (selected.size() > 0) {
                    field.setValue((String) selected.toArray()[0]);
                } else {
                    field.setValue("");
                }
                allValues.addAll(selected);
                if (!new ArrayList<>(allValues).equals(fieldValues)) {
                    fieldValues.clear();
                    fieldValues.addAll(allValues);
                }
            }
        }
    }

    public static void copyObject(PangaeaNoteObject from, PangaeaNoteObject to){
        from=from.copy();
        to.getFields().clear();
        to.getFields().addAll(from.getFields());
    }

    public static PangaeaNoteFieldOptions fieldOptions(PangaeaNoteField field,PangaeaNoteFieldDescriptor descr){
        PangaeaNoteFieldOptions f = field.getOptions().copy();
        PangaeaNoteFieldOptions d = descr.getOptions().copy();
        d.setLabelName(descr.getName());
        return PangaeaNoteFieldOptions.merge(f,d);
    }

    public static PangaeaNoteLabelHelper.LabelFormat formatOf(PangaeaNoteFieldOptions options){
        return new PangaeaNoteLabelHelper.LabelFormat()
                .setBold(PNoteUtils.nonNullAndTrue(options.getLabelBold()))
                .setItalic(PNoteUtils.nonNullAndTrue(options.getLabelItalic()))
                .setStriked(PNoteUtils.nonNullAndTrue(options.getLabelStriked()))
                .setUnderlined(PNoteUtils.nonNullAndTrue(options.getLabelUnderlined()))
                .setText(options.getLabelName())
                .setBackgroundColor(options.getLabelBackgroundColor())
                .setForegroundColor(options.getLabelForegroundColor())
                ;
    }
    public static void applyOptions(PangaeaNoteFieldOptions options, AppComponent component){
        component.backgroundColor().set(Color.ofDefault(options.getEditorBackgroundColor(),component.app()));
        component.foregroundColor().set(Color.ofDefault(options.getEditorForegroundColor(),component.app()));
    }

}
