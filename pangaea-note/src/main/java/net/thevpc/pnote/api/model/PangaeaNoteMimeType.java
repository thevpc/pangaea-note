/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author vpc
 */
public class PangaeaNoteMimeType {

    private String major;
    private String minor;
    private String editor;
    private String charset;
    private Map<String, String> properties;

    public PangaeaNoteMimeType(String major, String minor, String charset, String editor, Map<String, String> properties) {
        this.major = major;
        this.minor = minor;
        this.editor = editor;
        this.charset = charset;
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }

    public static PangaeaNoteMimeType of(String s) {
        return of(s,true);
    }
    
    public static PangaeaNoteMimeType of(String s, boolean lenient) {
        if (s != null && s.length() > 0) {
            String[] a = s.split(";");
            if (a.length > 0) {
                String[] b = a[0].split("/");
                if (b.length == 2) {
                    String editor = null;
                    String charset = null;
                    Map<String, String> p = new LinkedHashMap<>();
                    for (int i = 1; i < a.length; i++) {
                        String[] q = a[i].split("=");
                        String k = null;
                        String v = null;
                        if (q.length == 1) {
                            k = q[0].trim();
                            v = "";
                        } else if (q.length == 2) {
                            k = q[0].trim();
                            v = q[1].trim();
                        } else {
                            if (lenient) {
                                return null;
                            }
                            throw new IllegalArgumentException("unable to parse content type " + s);
                        }
                        if (k != null && k.length() > 0) {
                            if (k.equals("editor")) {
                                editor = v;
                            } else if (k.equals("charset")) {
                                charset = v;
                            } else {
                                p.put(k, v);
                            }
                        }
                    }
                    return new PangaeaNoteMimeType(b[0], b[1], charset, editor, p);
                }
            } else {
                if (lenient) {
                    return null;
                }
                throw new IllegalArgumentException("unable to parse content type " + s);
            }
        }
        return null;
    }

    public String getContentType() {
        return getMajor()+"/"+getMinor();
    }
    
    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getEditor() {
        return editor;
    }

    public String getCharset() {
        return charset;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(major);
        sb.append("/");
        sb.append(minor);
        if(editor!=null && editor.length()>0){
            sb.append(";editor=").append(editor);
        }
        if(charset!=null && charset.length()>0){
            sb.append(";charset=").append(charset);
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            sb.append(";").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.major);
        hash = 29 * hash + Objects.hashCode(this.minor);
        hash = 29 * hash + Objects.hashCode(this.editor);
        hash = 29 * hash + Objects.hashCode(this.charset);
        hash = 29 * hash + Objects.hashCode(this.properties);
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
        final PangaeaNoteMimeType other = (PangaeaNoteMimeType) obj;
        if (!Objects.equals(this.major, other.major)) {
            return false;
        }
        if (!Objects.equals(this.minor, other.minor)) {
            return false;
        }
        if (!Objects.equals(this.editor, other.editor)) {
            return false;
        }
        if (!Objects.equals(this.charset, other.charset)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        return true;
    }
    
    
}
