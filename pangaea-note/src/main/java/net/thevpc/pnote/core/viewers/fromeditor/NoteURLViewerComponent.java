/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.fromeditor;

import java.io.InputStream;
import java.util.function.Consumer;

import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public class NoteURLViewerComponent extends PangaeaNoteEditor implements URLViewerComponent {

    PangaeaNoteFrame frame;
    private final URLViewer outer;
    private boolean editable = true;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private String contentType;

    public NoteURLViewerComponent(String contentType, PangaeaNoteFrame frame, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        super(frame,true);
        this.outer = outer;
        this.frame = frame;
        this.contentType = contentType;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    public void navigate(String url) {
        frame.service().executorService().submit(
                () -> {
                    InputStream is = null;
                    try {
                        try {
                            is = Applications.asURL(url).openStream();
                            if (is == null) {
                                frame.app().runUI(() -> setContent("", contentType));
                                onError.accept(new IllegalArgumentException("null stream"));
                            } else {
                                String s = new String(Applications.toByteArray(is));
                                frame.app().runUI(() -> setContent(s, contentType));
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
        PangaeaNoteExt d = PangaeaNoteExt.of(frame.service().newDocument());
        PangaeaNoteExt n = PangaeaNoteExt.of(new PangaeaNote().setContentType(contentType).setContent(frame.service().stringToElement(content)));
        d.addChild(n);
        super.setNote(n);
    }


    @Override
    public boolean isEditable() {
        return editable && super.getNote() != null && super.isEditable();
    }

    @Override
    public void save() {
        //
    }

    @Override
    public void disposeComponent() {
        super.uninstall();
    }

}
