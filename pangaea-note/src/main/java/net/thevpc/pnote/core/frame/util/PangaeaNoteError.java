/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.util;

/**
 *
 * @author vpc
 */
public class PangaeaNoteError {
    private Exception ex;

    public PangaeaNoteError(Exception ex) {
        this.ex = ex;
    }

    public Exception getEx() {
        return ex;
    }
    
    
}
