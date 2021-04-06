/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vpc
 */
public abstract class AbstractPangaeaNoteTypeService implements PangaeaNoteTypeService {

//    protected ContentTypeSelector[] getDefaultContentTypeSelectors(String group) {
//        String[] a = normalizeEditorTypes(getContentType());
//        if (a.length == 1) {
//            return new ContentTypeSelector[]{
//                new ContentTypeSelector(getContentType(), getContentType(), a[0], group, 0)
//            };
//        }
//        List<ContentTypeSelector> all = new ArrayList<>();
//        all.add(new ContentTypeSelector(getContentType(), getContentType(), a[0], group, 0));
//        for (int i = 1; i < a.length; i++) {
//            all.add(new ContentTypeSelector(getContentType()+":"+a[i], getContentType(), a[i], group, 0));
//        }
//        return all.toArray()
//    }

}
