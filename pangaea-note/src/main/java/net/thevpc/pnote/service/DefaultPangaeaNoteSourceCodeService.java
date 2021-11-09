/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author thevpc
 */
public class DefaultPangaeaNoteSourceCodeService extends AbstractPangaeaNoteSourceCodeService {

    public DefaultPangaeaNoteSourceCodeService(PangaeaNoteMimeType contentType,
            String group,
            String[] mimetypes, String[] extensions) {
        super(contentType, group, mimetypes, extensions);
    }

}
