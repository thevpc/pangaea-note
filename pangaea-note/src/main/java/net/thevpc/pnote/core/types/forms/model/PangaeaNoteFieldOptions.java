package net.thevpc.pnote.core.types.forms.model;

import net.thevpc.pnote.util.PNoteUtils;

public class PangaeaNoteFieldOptions implements Cloneable {
    private String editorBackgroundColor;
    private String editorForegroundColor;
    private String labelIcon;
    private String labelName;
    private String labelForegroundColor;
    private String labelBackgroundColor;
    private Boolean labelBold;
    private Boolean labelItalic;
    private Boolean labelUnderlined;
    private Boolean labelStriked;
    private Boolean hidden;
    private Boolean contentReadOnly;
    private String valuePattern;
    private String contentType;


    public PangaeaNoteFieldOptions() {
    }

    public static PangaeaNoteFieldOptions nullifyDefaults(PangaeaNoteFieldOptions child, PangaeaNoteFieldOptions parent) {
        PangaeaNoteFieldOptions ret = new PangaeaNoteFieldOptions();

        if (!PNoteUtils.isLenientEquals(child.getEditorBackgroundColor(), parent.getEditorBackgroundColor())) {
            ret.setEditorBackgroundColor(child.getEditorBackgroundColor());
        }
        if (!PNoteUtils.isLenientEquals(child.getEditorForegroundColor(), parent.getEditorForegroundColor())) {
            ret.setEditorForegroundColor(child.getEditorForegroundColor());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelIcon(), parent.getLabelIcon())) {
            ret.setLabelIcon(child.getLabelIcon());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelName(), parent.getLabelName())) {
            ret.setLabelName(child.getLabelName());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelForegroundColor(), parent.getLabelForegroundColor())) {
            ret.setLabelForegroundColor(child.getLabelForegroundColor());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelBackgroundColor(), parent.getLabelBackgroundColor())) {
            ret.setLabelBackgroundColor(child.getLabelBackgroundColor());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelBold(), parent.getLabelBold())) {
            ret.setLabelBold(child.getLabelBold());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelItalic(), parent.getLabelItalic())) {
            ret.setLabelItalic(child.getLabelItalic());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelUnderlined(), parent.getLabelUnderlined())) {
            ret.setLabelUnderlined(child.getLabelUnderlined());
        }
        if (!PNoteUtils.isLenientEquals(child.getLabelStriked(), parent.getLabelStriked())) {
            ret.setLabelStriked(child.getLabelStriked());
        }
        if (!PNoteUtils.isLenientEquals(child.getHidden(), parent.getHidden())) {
            ret.setHidden(child.getHidden());
        }
        if (!PNoteUtils.isLenientEquals(child.getContentReadOnly(), parent.getContentReadOnly())) {
            ret.setContentReadOnly(child.getContentReadOnly());
        }
        if (!PNoteUtils.isLenientEquals(child.getValuePattern(), parent.getValuePattern())) {
            ret.setValuePattern(child.getValuePattern());
        }
        if (!PNoteUtils.isLenientEquals(child.getContentType(), parent.getContentType())) {
            ret.setContentType(child.getContentType());
        }
        return ret;
    }

    public static PangaeaNoteFieldOptions merge(PangaeaNoteFieldOptions... options) {
        PangaeaNoteFieldOptions ret = new PangaeaNoteFieldOptions();
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getLabelName();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setLabelName(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getEditorBackgroundColor();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setEditorBackgroundColor(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getEditorForegroundColor();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setEditorForegroundColor(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getLabelIcon();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setLabelIcon(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getLabelBackgroundColor();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setLabelBackgroundColor(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getLabelForegroundColor();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setLabelForegroundColor(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getValuePattern();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setValuePattern(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                String vv = option.getContentType();
                if (vv != null && !vv.trim().isEmpty()) {
                    ret.setContentType(vv.trim());
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                Boolean vv = option.getLabelBold();
                if (vv != null) {
                    ret.setLabelBold(vv);
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                Boolean vv = option.getLabelItalic();
                if (vv != null) {
                    ret.setLabelItalic(vv);
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                Boolean vv = option.getLabelUnderlined();
                if (vv != null) {
                    ret.setLabelUnderlined(vv);
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                Boolean vv = option.getLabelStriked();
                if (vv != null) {
                    ret.setLabelStriked(vv);
                    break;
                }
            }
        }
        for (PangaeaNoteFieldOptions option : options) {
            if (option != null) {
                Boolean vv = option.getContentReadOnly();
                if (vv != null) {
                    ret.setContentReadOnly(vv);
                    break;
                }
            }
        }
        return ret;
    }

    public Boolean getContentReadOnly() {
        return contentReadOnly;
    }

    public PangaeaNoteFieldOptions setContentReadOnly(Boolean contentReadOnly) {
        this.contentReadOnly = contentReadOnly;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public PangaeaNoteFieldOptions setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getValuePattern() {
        return valuePattern;
    }

    public PangaeaNoteFieldOptions setValuePattern(String valuePattern) {
        this.valuePattern = valuePattern;
        return this;
    }

    public Boolean getLabelUnderlined() {
        return labelUnderlined;
    }

    public PangaeaNoteFieldOptions setLabelUnderlined(Boolean labelUnderlined) {
        this.labelUnderlined = labelUnderlined;
        return this;
    }

    public Boolean getLabelStriked() {
        return labelStriked;
    }

    public PangaeaNoteFieldOptions setLabelStriked(Boolean labelStriked) {
        this.labelStriked = labelStriked;
        return this;
    }

    public String getEditorForegroundColor() {
        return editorForegroundColor;
    }

    public PangaeaNoteFieldOptions setEditorForegroundColor(String editorForegroundColor) {
        this.editorForegroundColor = editorForegroundColor;
        return this;
    }

    public String getLabelName() {
        return labelName;
    }

    public PangaeaNoteFieldOptions setLabelName(String labelName) {
        this.labelName = labelName;
        return this;
    }

    public String getLabelForegroundColor() {
        return labelForegroundColor;
    }

    public PangaeaNoteFieldOptions setLabelForegroundColor(String labelForegroundColor) {
        this.labelForegroundColor = labelForegroundColor;
        return this;
    }

    public String getLabelBackgroundColor() {
        return labelBackgroundColor;
    }

    public PangaeaNoteFieldOptions setLabelBackgroundColor(String labelBackgroundColor) {
        this.labelBackgroundColor = labelBackgroundColor;
        return this;
    }

    public Boolean getLabelBold() {
        return labelBold;
    }

    public PangaeaNoteFieldOptions setLabelBold(Boolean labelBold) {
        this.labelBold = labelBold;
        return this;
    }

    public Boolean getLabelItalic() {
        return labelItalic;
    }

    public PangaeaNoteFieldOptions setLabelItalic(Boolean labelItalic) {
        this.labelItalic = labelItalic;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public PangaeaNoteFieldOptions setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public String getLabelIcon() {
        return labelIcon;
    }

    public PangaeaNoteFieldOptions setLabelIcon(String labelIcon) {
        this.labelIcon = labelIcon;
        return this;
    }


    public String getEditorBackgroundColor() {
        return editorBackgroundColor;
    }

    public PangaeaNoteFieldOptions setEditorBackgroundColor(String editorBackgroundColor) {
        this.editorBackgroundColor = editorBackgroundColor;
        return this;
    }

    public PangaeaNoteFieldOptions copy() {
        try {
            return (PangaeaNoteFieldOptions) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
