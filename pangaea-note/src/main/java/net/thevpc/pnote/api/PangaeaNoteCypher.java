/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.api.model.CypherInfo;
import net.thevpc.pnote.api.model.PangaeaNote;

import java.util.function.Supplier;

/**
 * @author vpc
 */
public interface PangaeaNoteCypher {

    String getId();

    CypherInfo encrypt(PangaeaNote a, Supplier<String> passwordSupplier);

    PangaeaNote decrypt(CypherInfo cypherInfo, PangaeaNote original, Supplier<String> passwordSupplier);
}
