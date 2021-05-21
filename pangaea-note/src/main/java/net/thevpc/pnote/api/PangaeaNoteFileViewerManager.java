/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteFileViewerManager {

    int getSupport(String path, String extension, PangaeaNoteMimeType probedContentType, PangaeaNoteFrame win);

    URLViewerComponent createComponent(String path, String extension, PangaeaNoteMimeType probedContentType, URLViewer viewer,PangaeaNoteFrame win);
}
