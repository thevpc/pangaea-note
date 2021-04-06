/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.java;

import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteJavaService extends AbstractPangaeaNoteSourceCodeService {

    public static final String JAVA = "text/java";

    public PangaeaNoteJavaService() {
        super(JAVA,"file-java");
    }
}
