/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteObjectExt {
    private PangaeaNoteObject object;
    private PangaeaNoteObjectDescriptor descriptor;
    private PangaeaNoteObjectDocument document;

    public PangaeaNoteObjectExt() {
    }

    public PangaeaNoteObjectExt(PangaeaNoteObject object, PangaeaNoteObjectDescriptor descriptor, PangaeaNoteObjectDocument document) {
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

    public PangaeaNoteObjectDocument getDocument() {
        return document;
    }
    
}
