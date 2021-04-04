/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import javax.swing.JComponent;

/**
 *
 * @author vpc
 */
public interface EditorKitHeader {

    JComponent component();

    boolean acceptContentType(String contentType);
    
}
