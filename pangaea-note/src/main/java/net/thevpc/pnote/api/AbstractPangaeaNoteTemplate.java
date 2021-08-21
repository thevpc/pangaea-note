/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.common.i18n.I18n;
import net.thevpc.common.i18n.Str;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author vpc
 */
public abstract class AbstractPangaeaNoteTemplate implements PangaeaNoteTemplate {

    private String shortId;
    private PangaeaNoteMimeType id;
    private String icon;

    public AbstractPangaeaNoteTemplate(String shortId, String icon) {
        this.shortId = shortId;
        this.id = PangaeaNoteMimeType.of("application/x-pangaea-note-template-" + shortId);
        this.icon = icon;
    }

    protected String str(String s, PangaeaNoteApp app) {
        String prefix = "content-type." + getContentType() + ".";
        I18n i18n = app.i18n();
        return i18n.getString(prefix + "" + s);
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return id;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public Str getLabel(PangaeaNoteApp app) {
        return Str.i18n("content-type." + getContentType().toString());
    }

    public String getShortId() {
        return shortId;
    }

}
