/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree;

import javax.swing.AbstractAction;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.api.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public abstract class TreeAction extends AbstractAction {
    
    private String id;
    private final PangaeaNoteDocumentTree outer;

    public TreeAction(String id, final PangaeaNoteDocumentTree outer) {
        super(id);
        this.outer = outer;
        this.id = id;
        outer.actions.add(this);
        SwingApplicationsUtils.registerAction(this, "Action." + id, "$Action." + id + ".icon", outer.app);
        //            onLocaleChanged();
    }

    protected boolean isNonRootNote(PangaeaNoteExt note) {
        return note != null && note != outer.tree.getModel().getRoot();
    }

    protected void requireSelectedNote(PangaeaNoteExt note) {
        boolean nonRootSelected = isNonRootNote(note);
        //            System.out.println("requireSelectedNote " + note);
        setEnabled(nonRootSelected);
        putValue("visible", nonRootSelected);
    }

    protected void onSelectedNote(PangaeaNoteExt note) {
    }

    protected void onLocaleChanged() {
        //            putValue(NAME, app.i18n().getString("Action." + id + ".name"));
    }
    
}
