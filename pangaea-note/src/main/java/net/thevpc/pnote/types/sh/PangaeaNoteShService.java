/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.sh;

import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteShService extends AbstractPangaeaNoteSourceCodeService {

    public static final String SH = "text/sh";

    public PangaeaNoteShService() {
        super(SH, "file-sh");
    }

}
