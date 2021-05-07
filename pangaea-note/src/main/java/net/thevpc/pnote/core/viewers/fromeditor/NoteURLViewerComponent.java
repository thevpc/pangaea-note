/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.fromeditor;

import java.io.InputStream;
import java.util.function.Consumer;
import javax.swing.JComponent;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class NoteURLViewerComponent implements URLViewerComponent {

    PangaeaNoteWindow win;
    PangaeaNoteEditor ed;
    private final URLViewer outer;
    private boolean editable = true;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private String contentType;

    public NoteURLViewerComponent(String contentType, PangaeaNoteWindow win, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        this.outer = outer;
        this.win = win;
        this.contentType = contentType;
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.ed = new PangaeaNoteEditor(win, true);
    }

    public void setURL(String url) {
        win.service().executorService().submit(
                () -> {
                    InputStream is = null;
                    try {
                        try {
                            is = OtherUtils.asURL(url).openStream();
                            if (is == null) {
                                SwingUtilities3.invokeLater(() -> setContent("", contentType));
                                onError.accept(new IllegalArgumentException("null stream"));
                            } else {
                                String s = new String(OtherUtils.toByteArray(is));
                                SwingUtilities3.invokeLater(() -> setContent(s, contentType));
                                onSuccess.run();
                            }
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }
                    } catch (Exception ex) {
                        onError.accept(ex);
                    }
                }
        );
    }

    public void setContent(String content, String contentType) {
        PangaeaNoteExt d = PangaeaNoteExt.of(win.service().newDocument());
        PangaeaNoteExt n = PangaeaNoteExt.of(new PangaeaNote().setContentType(contentType).setContent(win.service().stringToElement(content)));
        d.addChild(n);
        ed.setNote(n);
    }

    @Override
    public JComponent component() {
        return ed;
    }

    @Override
    public boolean isEditable() {
        return editable && ed.getNote() != null && ed.isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        ed.setEditable(editable);
    }

    @Override
    public void save() {
        //
    }

    @Override
    public void disposeComponent() {
        ed.uninstall();
    }

}
