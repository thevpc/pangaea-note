package net.thevpc.pnote.core.types.embedded;

public class PangaeaNoteDocumentInfo {
    private String path;
    private String locale;
    private String iconSet;
    private int iconSize;

    public String getPath() {
        return path;
    }

    public int getIconSize() {
        return iconSize;
    }

    public PangaeaNoteDocumentInfo setIconSize(int iconSize) {
        this.iconSize = iconSize;
        return this;
    }

    public PangaeaNoteDocumentInfo setPath(String path) {
        this.path = path;
        return this;
    }

    public String getLocale() {
        return locale;
    }

    public PangaeaNoteDocumentInfo setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getIconSet() {
        return iconSet;
    }

    public PangaeaNoteDocumentInfo setIconSet(String iconSet) {
        this.iconSet = iconSet;
        return this;
    }
}
