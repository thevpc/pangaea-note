/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

/**
 *
 * @author vpc
 */
public interface URLViewerListener {

    void onError(String path, Exception ex);

    void onStartLoading(String path);

    void onSuccessfulLoading(String path);

    void onReset();
    
}
