/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.objectlist;

import net.thevpc.pnote.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.model.PangaeaNoteObject;
import net.thevpc.pnote.model.PangaeaNoteObjectDescriptor;

/**
 *
 * @author vpc
 */
public class PangaeaNoteObjectExt {
    private PangaeaNoteObject object;
    private PangaeaNoteObjectDescriptor descriptor;
    private PangageaNoteObjectDocument document;

    public PangaeaNoteObjectExt() {
    }

    public PangaeaNoteObjectExt(PangaeaNoteObject object, PangaeaNoteObjectDescriptor descriptor, PangageaNoteObjectDocument document) {
        this.object = object;
        this.descriptor = descriptor;
        this.document = document;
    }

    public PangaeaNoteObject getObject() {
        return object;
    }

    public PangaeaNoteObjectDescriptor getDescriptor() {
        return descriptor;
    }

    public PangageaNoteObjectDocument getDocument() {
        return document;
    }
    
}
