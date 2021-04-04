/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.thevpc.pnote.gui.util.PangaeaNoteError;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class PangaeaNoteExt {

    private PangaeaNoteExt parent;
    private String version;
    private Instant creationTime;
    private Instant lastModified;
    private String name;
    private String icon;
    private String contentType;
    private String editorType;
    private String content;
    private boolean readOnly;
    private transient boolean loaded;
    private Set<String> tags = new HashSet<String>();
    private Map<String, String> properties = new LinkedHashMap<String, String>();
    private List<PangaeaNoteExt> children = new ArrayList<>();
    private ObservableNoteListener listener;
    private CypherInfo cypherInfo;
    private boolean titleBold;
    private boolean titleItalic;
    private boolean titleUnderlined;
    private boolean titleStriked;
    private String titleForeground;
    private String titleBackground;
    public transient PangaeaNoteError error;

    public static PangaeaNoteExt newDocument() {
        return of(PangaeaNote.newDocument());
    }

    public static PangaeaNoteExt of(PangaeaNote n) {
        return new PangaeaNoteExt().copyFrom(n);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public ObservableNoteListener getListener() {
        return listener;
    }

    public void setListener(ObservableNoteListener listener) {
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        if (!Objects.equals(this.name, value)) {
            String old = this.name;
            this.name = value;
            fireChangeEvent(this, "name", old, value);
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String value) {
        if (!Objects.equals(this.icon, value)) {
            String old = this.icon;
            this.icon = value;
            fireChangeEvent(this, "icon", old, value);
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean value) {
        if (!Objects.equals(this.readOnly, value)) {
            boolean old = this.readOnly;
            this.readOnly = value;
            fireChangeEvent(this, "readOnly", old, value);
        }
    }


    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean value) {
        if (!Objects.equals(this.loaded, value)) {
            boolean old = this.loaded;
            this.loaded = value;
            fireChangeEvent(this, "loaded", old, value);
        }
    }

    public List<PangaeaNoteExt> getChildren() {
        return children;
    }

    public void removeAllChildren() {
        while (!children.isEmpty()) {
            removeChild(0);
        }
    }

    public void setChildren(List<PangaeaNoteExt> children) {
        List<PangaeaNoteExt> value = new ArrayList<>(children);
        removeAllChildren();
        for (PangaeaNoteExt n : value) {
            addChild(n);
        }
    }

    public void addChild(int pos, PangaeaNoteExt child) {
        List<PangaeaNoteExt> old = new ArrayList<>(this.children);
        child.setParent(this);
        this.children.add(pos, child);
        fireChangeEvent(this, "children", old, this.children);
        fireAddEvent(child, this);
    }

    public void addChild(PangaeaNoteExt child) {
        List<PangaeaNoteExt> old = new ArrayList<>(this.children);
        child.setParent(this);
        this.children.add(child);
        fireChangeEvent(this, "children", old, this.children);
        fireAddEvent(child, this);
    }

    public void removeChild(int pos) {
        List<PangaeaNoteExt> old = new ArrayList<>(this.children);
        PangaeaNoteExt n = this.children.remove(pos);
        if (n != null) {
            n.setParent(null);
            fireChangeEvent(this, "children", old, this.children);
            fireRemoveEvent(n, this);
        }
    }

    public PangaeaNoteExt getParent() {
        return parent;
    }

    public void setParent(PangaeaNoteExt value) {
        if (this.parent != value) {
            PangaeaNoteExt old = this.parent;
            this.parent = value;
            fireChangeEvent(this, "parent", old, value);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String value) {
        if (!Objects.equals(this.contentType, value)) {
            String old = this.contentType;
            this.contentType = value;
            fireChangeEvent(this, "contentType", old, value);
        }
    }

    public String getEditorType() {
        return editorType;
    }

    public void setEditorType(String value) {
        if (!Objects.equals(this.editorType, value)) {
            String old = this.editorType;
            this.editorType = value;
            fireChangeEvent(this, "editorType", old, value);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String value) {
        if (!Objects.equals(this.content, value)) {
            String old = this.content;
            this.content = value;
            fireChangeEvent(this, "content", old, value);
        }
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> value) {
        if (value == null) {
            value = new HashSet<>();
        }
        if (!this.tags.equals(value)) {
            Set<String> old = this.tags;
            this.tags = value;
            fireChangeEvent(this, "tags", old, value);
        }
    }

    protected void fireChangeEvent(PangaeaNoteExt note, String prop, Object oval, Object nval) {
        if (listener != null) {
            this.listener.onChanged(note, prop, oval, nval);
        } else if (this.parent != null) {
            this.parent.fireChangeEvent(note, prop, oval, nval);
        }
    }

    protected void fireAddEvent(PangaeaNoteExt child, PangaeaNoteExt parent) {
        if (listener != null) {
            this.listener.onAdded(child, parent);
        } else if (this.parent != null) {
            this.parent.fireAddEvent(child, parent);
        }
    }

    protected void fireRemoveEvent(PangaeaNoteExt child, PangaeaNoteExt parent) {
        if (this.listener != null) {
            this.listener.onRemoved(child, parent);
        } else if (this.parent != null) {
            this.parent.fireRemoveEvent(child, parent);
        }
    }

    public PangaeaNoteExt copyFrom(PangaeaNote other) {
        setName(other.getName());
        setIcon(other.getIcon());
        setContentType(other.getContentType());
        setTags(other.getTags());
        setContent(other.getContent());

        setCreationTime(other.getCreationTime());
        setLastModified(other.getLastModified());
        setVersion(other.getVersion());
        setCypherInfo(other.getCypherInfo());
        setProperties(other.getProperties() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(other.getProperties()));
        setReadOnly(other.isReadOnly());
        setLoaded(other.isLoaded());

        setTitleBackground(other.getTitleBackground());
        setTitleForeground(other.getTitleForeground());
        setTitleBold(other.isTitleBold());
        setTitleItalic(other.isTitleItalic());
        setTitleUnderlined(other.isTitleUnderlined());
        setTitleStriked(other.isTitleStriked());

        List<PangaeaNoteExt> newChildren = new ArrayList<>();
        for (PangaeaNote nn : other.getChildren()) {
            newChildren.add(new PangaeaNoteExt().copyFrom(nn));
        }
        this.error=other.error;
        setChildren(newChildren);
        return this;
    }

    public PangaeaNote toNote() {
        PangaeaNote n = new PangaeaNote();
        n.setName(this.getName());
        n.setIcon(this.getIcon());
        n.setContentType(this.getContentType());
        n.setTags(this.getTags());
        n.setContent(this.getContent());

        n.setCreationTime(this.getCreationTime());
        n.setLastModified(this.getLastModified());
        n.setVersion(this.getVersion());
        n.setCypherInfo(this.getCypherInfo());
        n.setReadOnly(this.isReadOnly());

        n.setTitleBackground(getTitleBackground());
        n.setTitleForeground(getTitleForeground());
        n.setTitleBold(isTitleBold());
        n.setTitleItalic(isTitleItalic());
        n.setTitleUnderlined(isTitleUnderlined());
        n.setTitleStriked(isTitleStriked());
        n.setLoaded(isLoaded());

        List<PangaeaNote> newChildren = new ArrayList<>();
        for (PangaeaNoteExt nn : getChildren()) {
            newChildren.add(nn.toNote());
        }
        n.setChildren(newChildren);
        n.setProperties(getProperties() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(getProperties()));
        n.error=this.error;
        return n;
    }

    @Override
    public String toString() {
        String n = name == null ? "" : name.trim();
        if (n.isEmpty()) {
            n = "<no-name>";
        }
        return n;
    }

    public int indexOfChild(PangaeaNoteExt current) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == current) {
                return i;
            }
        }
        return -1;
    }

    public boolean addAfterThis(PangaeaNoteExt n) {
        PangaeaNoteExt p = this.getParent();
        if (p != null) {
            int i = p.indexOfChild(this);
            if (i >= 0) {
                p.addChild(i + 1, n);
                return true;
            }
        }
        return false;
    }

    public boolean addBeforeThis(PangaeaNoteExt n) {
        PangaeaNoteExt p = this.getParent();
        if (p != null) {
            int i = p.indexOfChild(this);
            if (i >= 0) {
                p.addChild(i, n);
                return true;
            }
        }
        return false;
    }

    public PangaeaNoteExt addDuplicate() {
        PangaeaNoteExt d = duplicate();
        addAfterThis(d);
        return d;
    }

    public PangaeaNoteExt duplicate() {
        return new PangaeaNoteExt().copyFrom(toNote());
    }

    public boolean delete() {
        PangaeaNoteExt p = this.getParent();
        if (p != null) {
            int i = p.indexOfChild(this);
            if (i >= 0) {
                p.removeChild(i);
                return true;
            }
        }
        return false;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public CypherInfo getCypherInfo() {
        return cypherInfo;
    }

    public void setCypherInfo(CypherInfo cypherInfo) {
        this.cypherInfo = cypherInfo;
    }

    public boolean isTitleBold() {
        return titleBold;
    }

    public void setTitleBold(boolean titleBold) {
        this.titleBold = titleBold;
    }

    public boolean isTitleItalic() {
        return titleItalic;
    }

    public void setTitleItalic(boolean titleItalic) {
        this.titleItalic = titleItalic;
    }

    public boolean isTitleUnderlined() {
        return titleUnderlined;
    }

    public void setTitleUnderlined(boolean titleUnderlined) {
        this.titleUnderlined = titleUnderlined;
    }

    public boolean isTitleStriked() {
        return titleStriked;
    }

    public void setTitleStriked(boolean titleStriked) {
        this.titleStriked = titleStriked;
    }
    

    public String getTitleForeground() {
        return titleForeground;
    }

    public void setTitleForeground(String titleForeground) {
        this.titleForeground = titleForeground;
    }

    public String getTitleBackground() {
        return titleBackground;
    }

    public void setTitleBackground(String titleBackground) {
        this.titleBackground = titleBackground;
    }

    public PangaeaNoteExt copy() {
        return PangaeaNoteExt.of(toNote());
    }

    public void moveDown(int from) {
        OtherUtils.switchListValues(children, from, from + 1);
    }

    public void moveUp(int from) {
        OtherUtils.switchListValues(children, from, from - 1);
    }

    public void moveLast(int from) {
        if (from >= 0 && from < children.size() - 1) {
            PangaeaNoteExt a = children.remove(from);
            children.add(a);
        }
    }

    public void moveFirst(int from) {
        if (from > 0 && from <= children.size() - 1) {
            PangaeaNoteExt a = children.remove(from);
            children.add(0, a);
        }
    }

}
