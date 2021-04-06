/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.c;

import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteCService extends AbstractPangaeaNoteSourceCodeService {

    public static final String C = "text/c";

    public PangaeaNoteCService() {
        super(C,"file-c");
    }

    
}
