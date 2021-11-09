/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.thevpc.echo.impl.TreeNode;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.core.frame.util.PangaeaNoteError;

/**
 *
 * @author thevpc
 */
public class PangaeaNote implements Cloneable {

    private String version;
    private Instant creationTime;
    private Instant lastModified;
    private String name;
    private boolean titleBold;
    private boolean titleItalic;
    private boolean titleUnderlined;
    private boolean titleStriked;
    private String titleForeground;
    private String titleBackground;
    private String icon;
    private String folderIcon;
    private String contentType;
    private String editorType;
    private NutsElement content;

    /**
     * any structured information that can be used by parent notes and that are
     * related to this note
     */
    private NutsElement childData;
    private boolean readOnly;
    private Set<String> tags = new HashSet<String>();
    private List<PangaeaNote> children = new ArrayList<>();
    private Map<String, String> properties = new LinkedHashMap<String, String>();
    private CypherInfo cypherInfo;
    private transient boolean loaded;
    public transient PangaeaNoteError error;
    public transient TreeNode<PangaeaNote> guiNode;
//    public transient PangaeaNoteFrame guiFrame;

    public String getFolderIcon() {
        return folderIcon;
    }

    public void setFolderIcon(String folderIcon) {
        this.folderIcon = folderIcon;
    }

    public String getContentType() {
        return contentType;
    }

    public PangaeaNote setContentType(String type) {
        this.contentType = type;
        return this;
    }

    public String getEditorType() {
        return editorType;
    }

    public void setEditorType(String editorType) {
        this.editorType = editorType;
    }

    public NutsElement getContent() {
        return content;
    }

    public PangaeaNote setContent(NutsElement content) {
        this.content = content;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public PangaeaNote setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getName() {
        return name;
    }

    public PangaeaNote setName(String name) {
        this.name = name;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public PangaeaNote setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public List<PangaeaNote> getChildren() {
        return children;
    }

    public PangaeaNote setChildren(List<PangaeaNote> children) {
        this.children = children;
        return this;
    }

    public PangaeaNoteError getError() {
        return error;
    }

    public void setError(PangaeaNoteError error) {
        this.error = error;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.version);
        hash = 17 * hash + Objects.hashCode(this.creationTime);
        hash = 17 * hash + Objects.hashCode(this.lastModified);
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + (this.titleBold ? 1 : 0);
        hash = 17 * hash + (this.titleItalic ? 1 : 0);
        hash = 17 * hash + (this.titleUnderlined ? 1 : 0);
        hash = 17 * hash + (this.titleStriked ? 1 : 0);
        hash = 17 * hash + Objects.hashCode(this.titleForeground);
        hash = 17 * hash + Objects.hashCode(this.titleBackground);
        hash = 17 * hash + Objects.hashCode(this.icon);
        hash = 17 * hash + Objects.hashCode(this.folderIcon);
        hash = 17 * hash + Objects.hashCode(this.contentType);
        hash = 17 * hash + Objects.hashCode(this.editorType);
        hash = 17 * hash + Objects.hashCode(this.content);
        hash = 17 * hash + (this.readOnly ? 1 : 0);
        hash = 17 * hash + Objects.hashCode(this.error);
        hash = 17 * hash + Objects.hashCode(this.tags);
        hash = 17 * hash + Objects.hashCode(this.children);
        hash = 17 * hash + Objects.hashCode(this.properties);
        hash = 17 * hash + Objects.hashCode(this.cypherInfo);
        hash = 17 * hash + Objects.hashCode(this.childData);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PangaeaNote other = (PangaeaNote) obj;
        if (this.titleBold != other.titleBold) {
            return false;
        }
        if (this.titleItalic != other.titleItalic) {
            return false;
        }
        if (this.titleUnderlined != other.titleUnderlined) {
            return false;
        }
        if (this.titleStriked != other.titleStriked) {
            return false;
        }
        if (this.readOnly != other.readOnly) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.titleForeground, other.titleForeground)) {
            return false;
        }
        if (!Objects.equals(this.titleBackground, other.titleBackground)) {
            return false;
        }
        if (!Objects.equals(this.icon, other.icon)) {
            return false;
        }
        if (!Objects.equals(this.folderIcon, other.folderIcon)) {
            return false;
        }
        if (!Objects.equals(this.contentType, other.contentType)) {
            return false;
        }
        if (!Objects.equals(this.editorType, other.editorType)) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (!Objects.equals(this.creationTime, other.creationTime)) {
            return false;
        }
        if (!Objects.equals(this.lastModified, other.lastModified)) {
            return false;
        }
        if (!Objects.equals(this.error, other.error)) {
            return false;
        }
        if (!Objects.equals(this.tags, other.tags)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        if (!Objects.equals(this.cypherInfo, other.cypherInfo)) {
            return false;
        }
        return true;
    }

    public CypherInfo getCypherInfo() {
        return cypherInfo;
    }

    public void setCypherInfo(CypherInfo cypherInfo) {
        this.cypherInfo = cypherInfo;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public PangaeaNote setLoaded(boolean value) {
        this.loaded = value;
        return this;
    }

    public PangaeaNote copy() {
        PangaeaNote n2;
        try {
            n2 = (PangaeaNote) clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Impossible");
        }
        n2.guiNode=null;
        n2.setTags(this.getTags() == null ? null : new HashSet<>(this.getTags()));
        n2.setCypherInfo(getCypherInfo() == null ? null : getCypherInfo().copy());
        n2.setProperties(getProperties() == null ? null : new LinkedHashMap<>(getProperties()));
        n2.setChildren(new ArrayList<>());
        if (this.getChildren() != null) {
            for (PangaeaNote c : this.getChildren()) {
                if (c != null) {
                    n2.getChildren().add(c.copy());
                }
            }
        }
        return n2;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "PangaeaNote{" + "name=" + name + ", contentType=" + contentType + '}';
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

    public NutsElement getChildData() {
        return childData;
    }

    public void setChildData(NutsElement childData) {
        this.childData = childData;
    }

    public void copyFrom(PangaeaNote a) {
        this.version = a.version;
        this.creationTime = a.creationTime;
        this.lastModified = a.lastModified;
        this.name = a.name;
        this.titleBold = a.titleBold;
        this.titleItalic = a.titleItalic;
        this.titleUnderlined = a.titleUnderlined;
        this.titleStriked = a.titleStriked;
        this.titleForeground = a.titleForeground;
        this.titleBackground = a.titleBackground;
        this.icon = a.icon;
        this.folderIcon = a.folderIcon;
        this.contentType = a.contentType;
        this.editorType = a.editorType;
        this.content = a.content;
        this.readOnly = a.readOnly;
        this.tags = new HashSet<String>(a.tags == null ? new HashSet<>() : a.tags);
        this.children = new ArrayList<>();
        this.properties = new LinkedHashMap<String, String>(a.properties == null ? new LinkedHashMap<>() : a.properties);
        this.cypherInfo = a.cypherInfo == null ? null : a.cypherInfo.copy();
        this.loaded = a.loaded;
        this.error = a.error;
        this.childData = a.childData;
        if (a.children != null) {
            for (PangaeaNote c : a.children) {
                this.children.add(c.copy());
            }
        }
    }

}
