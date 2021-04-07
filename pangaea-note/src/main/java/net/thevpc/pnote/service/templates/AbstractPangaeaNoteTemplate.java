/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.templates;

import net.thevpc.common.i18n.I18n;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTemplate;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public abstract class AbstractPangaeaNoteTemplate implements PangaeaNoteTemplate {

    private String shortId;
    private PangaeaNoteContentType id;
    private String icon;

    public AbstractPangaeaNoteTemplate(String shortId, String icon) {
        this.shortId = shortId;
        this.id = PangaeaNoteContentType.of("application/pangaea-note-extra-" + shortId);
        this.icon = icon;
    }

    protected String str(String s, PangaeaNoteService service) {
        String prefix = "PangaeaNoteTypeFamily." + getContentType() + ".";
        I18n i18n = service.i18n();
        return i18n.getString(prefix + "" + s);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return id;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getLabel(PangaeaNoteService service) {
        I18n i18n = service.i18n();
        return i18n.getString("PangaeaNoteTypeFamily." + getContentType().toString());
    }

    public String getShortId() {
        return shortId;
    }

}
