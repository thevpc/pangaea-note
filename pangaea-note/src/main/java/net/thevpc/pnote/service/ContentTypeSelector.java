/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class ContentTypeSelector {

    private PangaeaNoteContentType contentType;
    private String group;
    private int order;

    public ContentTypeSelector(PangaeaNoteContentType contentType, String group, int order) {
        this.contentType = contentType;
        this.group = group;
        this.order = order;
    }

    public PangaeaNoteContentType getContentType() {
        return contentType;
    }

    public String getGroup() {
        return group;
    }

    public int getOrder() {
        return order;
    }

}
