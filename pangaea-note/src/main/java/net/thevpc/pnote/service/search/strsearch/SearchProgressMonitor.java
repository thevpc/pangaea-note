/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search.strsearch;

/**
 *
 * @author vpc
 */
public interface SearchProgressMonitor {
    void startSearch();
    void searchProgress(Object current);
    void completeSearch();
}
