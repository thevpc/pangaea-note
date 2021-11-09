/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search.strsearch;

import java.util.Iterator;

/**
 *
 * @author thevpc
 */
public interface DocumentTextNavigator<T> {

    Iterator<DocumentTextPart<T>> iterator();

}
