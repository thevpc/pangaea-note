/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

//import java.awt.Font;
//import java.awt.font.TextAttribute;
//import java.util.Map;
//import javax.swing.text.Document;
//import javax.swing.text.JTextComponent;
//import javax.swing.undo.UndoManager;
//import net.thevpc.common.swing.text.UndoRedoHelper;
//import net.thevpc.jeep.editor.JSyntaxDocument;
//
/**
 *
 * @author vpc
 */
public class GuiHelper {

//    public static Font deriveFont(Font _font, boolean bold, boolean italic, boolean underline, boolean strike) {
//        Font f = _font.deriveFont((bold ? Font.BOLD : 0) + (italic ? Font.ITALIC : 0));
//        Map attributes = null;
//        if (underline) {
//            if (attributes == null) {
//                attributes = f.getAttributes();
//            }
//            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
//        }
//        if (strike) {
//            if (attributes == null) {
//                attributes = f.getAttributes();
//            }
//            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
//        }
//        if (attributes != null) {
//            f = f.deriveFont(attributes);
//        }
//        return f;
//    }
//
//
//    public static UndoManager getUndoRedoManager(JTextComponent c) {
//        Document d = c.getDocument();
//        if (d instanceof JSyntaxDocument) {
//            UndoManager v = ((JSyntaxDocument) d).getUndoManager();
//            return v;
//        } else {
//            UndoManager um = (UndoManager) c.getClientProperty("undo-manager");
//            if (um == null) {
//                um = new UndoManager();
//                c.putClientProperty("undo-manager", um);
//            }
//            return um;
//        }
//    }
//
//    public static void installUndoRedoManager(JTextComponent c) {
//        UndoManager v = getUndoRedoManager(c);
//        UndoRedoHelper.installUndoRedoManager(c, v);
//    }

}
