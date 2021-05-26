/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.echo.api.SupportSupplier;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;

/**
 * @author vpc
 */
public interface PangaeaNoteFileViewerManager {
    SupportSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType probedContentType, URLViewer viewer, PangaeaNoteFrame win);
}
