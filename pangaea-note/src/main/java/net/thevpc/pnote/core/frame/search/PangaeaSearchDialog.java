/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.search;

import net.thevpc.echo.*;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

/**
 * @author vpc
 */
public class PangaeaSearchDialog extends SearchDialog{

    public PangaeaSearchDialog(PangaeaNoteFrame owner) {
        super(owner);
        queryEditor().values().setCollection(owner.getRecentSearchQueries());
    }
}
