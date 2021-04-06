/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.pnote.types.c.PangaeaNoteCService;
import net.thevpc.pnote.types.cpp.PangaeaNoteCppService;
import net.thevpc.pnote.types.html.PangaeaNoteHtmlService;
import net.thevpc.pnote.types.java.PangaeaNoteJavaService;
import net.thevpc.pnote.types.javascript.PangaeaNoteJavascriptService;
import net.thevpc.pnote.types.markdown.PangaeaNoteMarkdownService;
import net.thevpc.pnote.types.ntf.PangaeaNoteNTFService;
import net.thevpc.pnote.types.sh.PangaeaNoteShService;

/**
 *
 * @author vpc
 */
public class PangaeaContentTypes {

    public static final String UNSUPPORTED = "application/unsupported";
    public static final String PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION = "pnote";

    public static boolean isFormattedText(String contentType) {
        switch (contentType == null ? "" : contentType) {
            case PangaeaNoteHtmlService.HTML:
            case PangaeaNoteNTFService.NUTS_TEXT_FORMAT:
            case PangaeaNoteMarkdownService.MARKDOWN: {
                return true;
            }
        }
        return false;
    }

    public static boolean isXmlLike(String contentType) {
        switch (contentType == null ? "" : contentType) {
            case PangaeaNoteHtmlService.HTML: {
                return true;
            }
        }
        return false;
    }

    public static boolean isSourceCode(String contentType) {
        switch (contentType == null ? "" : contentType) {
            case PangaeaNoteCService.C:
            case PangaeaNoteCppService.CPP:
            case PangaeaNoteJavaService.JAVA:
            case PangaeaNoteJavascriptService.JAVASCRIPT:
            case PangaeaNoteShService.SH: {
                return true;
            }
        }
        return false;
    }

}
