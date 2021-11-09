/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author thevpc
 */
public abstract class AbstractPangaeaNoteTypeService implements PangaeaNoteTypeService {

    private Set<String> supportedMimeTypes = new HashSet<String>();
    private PangaeaNoteMimeType contentType;
    private PangaeaNoteApp app;

    public AbstractPangaeaNoteTypeService(PangaeaNoteMimeType contentType, String... mimetypes) {
        this.contentType = contentType;
        this.supportedMimeTypes.add(contentType.toString());
        this.supportedMimeTypes.addAll(Arrays.asList(mimetypes));
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {
        this.app = app;
    }


    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "content-type." + getContentType().toString();
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        return supportedMimeTypes.contains(probedContentType) ? 10 : -1;
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return contentType;
    }

    public NutsElement getContentAsElement(String s) {
        return app.stringToElement(s);
    }

    public String getContentAsString(NutsElement s) {
        return app.elementToString(s);
    }

    @Override
    public boolean isEmptyContent(NutsElement content) {
        if (content == null) {
            return true;
        }
        return content.isEmpty();
    }

    public PangaeaNoteApp app() {
        return app;
    }

    
}
