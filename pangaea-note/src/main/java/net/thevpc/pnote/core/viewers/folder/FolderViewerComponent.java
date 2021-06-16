/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.folder;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author vpc
 */
public class FolderViewerComponent extends FolderView implements URLViewerComponent {

    private final URLViewer viewer;
    PangaeaNoteFrame frame;
    private Runnable onSuccess;
    public FolderViewerComponent(PangaeaNoteFrame frame, final URLViewer viewer, Runnable onSuccess, Consumer<Exception> onError) {
        super(frame.app());
        this.onSuccess = onSuccess;
        this.viewer = viewer;
        this.frame = frame;
        selection().onChange(() -> {
            String t = selection().get();
            if(t!=null){
                File file = Applications.asFile(t);
                if(file !=null && file.isDirectory()){
                    navigate(t);
                }else{
                    viewer.navigate(t);
                }
            }
        });
    }

    @Override
    public void navigate(String url) {
        if(editable().get()) {
            viewer.getHeader().getTextField().text().set(Str.of(url));
            String old = folder().get();
            if (Objects.equals(url, old)) {
                refresh();
            } else {
                folder().set(url);
            }
            if (onSuccess != null) {
                onSuccess.run();
            }
        }
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void save() {
        //
    }

    @Override
    public void setEditable(boolean editable) {
        editable().set(editable);
    }


}
