/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.cpp;

import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteCppService extends AbstractPangaeaNoteSourceCodeService {

    public static final String CPP = "text/cpp";

    public PangaeaNoteCppService() {
        super(PangaeaNoteContentType.of(CPP), "file-cpp");
    }

}
