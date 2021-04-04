/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.templates;

import net.thevpc.common.i18n.I18n;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTemplate;

/**
 *
 * @author vpc
 */
public abstract class AbstractPangaeaNoteTemplate implements PangaeaNoteTemplate {

    private String shortId;
    private String id;
    private String icon;

    public AbstractPangaeaNoteTemplate(String shortId, String icon) {
        this.shortId = shortId;
        this.id = "application/pangaea-note-extra-" + shortId;
        this.icon = icon;
    }

    protected String str(String s, PangaeaNoteService service) {
        String prefix = "PangaeaNoteTypeFamily." + getId() + ".";
        I18n i18n = service.i18n();
        return i18n.getString(prefix + "" + s);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getLabel(PangaeaNoteService service) {
        I18n i18n = service.i18n();
        return i18n.getString("PangaeaNoteTypeFamily." + getId());
    }

    public String getShortId() {
        return shortId;
    }

}
