/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.security;

import java.util.function.Supplier;
import net.thevpc.pnote.model.CypherInfo;
import net.thevpc.pnote.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteObfuscator {

    CypherInfo encrypt(PangaeaNote a, Supplier<String> passwordSupplier);

    PangaeaNote decrypt(CypherInfo cypherInfo, PangaeaNote original,Supplier<String> passwordSupplier);
}
