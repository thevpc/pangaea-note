/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api.model;

import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class ContentTypeSelector {

    private PangaeaNoteMimeType contentType;
    private String group;
    private int order;

    public ContentTypeSelector(PangaeaNoteMimeType contentType, String group, int order) {
        this.contentType = contentType;
        this.group = group;
        this.order = order;
    }

    public PangaeaNoteMimeType getContentType() {
        return contentType;
    }

    public String getGroup() {
        return group;
    }

    public int getOrder() {
        return order;
    }

}
