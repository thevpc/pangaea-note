/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

/**
 *
 * @author vpc
 */
public class ContentTypeSelector {
    private String id;
    private String contentType;
    private String editorType;
    private String group;
    private int order;

    public ContentTypeSelector(String id, String contentType, String editorType, String group, int order) {
        this.id = id;
        this.contentType = contentType;
        this.editorType = editorType;
        this.group = group;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEditorType() {
        return editorType;
    }

    public String getGroup() {
        return group;
    }

    public int getOrder() {
        return order;
    }
    
}
