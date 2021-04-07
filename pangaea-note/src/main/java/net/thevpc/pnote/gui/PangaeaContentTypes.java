/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.pnote.types.html.PangaeaNoteHtmlService;
import net.thevpc.pnote.types.markdown.PangaeaNoteMarkdownService;
import net.thevpc.pnote.types.ntf.PangaeaNoteNTFService;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.types.html.PangaeaNoteHtmlWysiwygService;

/**
 *
 * @author vpc
 */
public class PangaeaContentTypes {

    public static final PangaeaNoteContentType UNSUPPORTED = PangaeaNoteContentType.of("application/unsupported");
    public static final String PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION = "pnote";

    public static boolean isFormattedText(PangaeaNoteContentType contentType) {
        String c = contentType == null ? "" : contentType.toString();
        return (c.equals(PangaeaNoteHtmlService.HTML.toString())
                || c.equals(PangaeaNoteNTFService.NUTS_TEXT_FORMAT.toString())
                || c.equals(PangaeaNoteMarkdownService.MARKDOWN.toString()));
    }

    public static boolean isXmlLike(PangaeaNoteContentType contentType) {
        String c = contentType == null ? "" : contentType.toString();
        return (c.equals(PangaeaNoteHtmlService.HTML.toString())
                || c.equals(PangaeaNoteHtmlWysiwygService.RICH_HTML.toString()));
    }

    public static boolean isSourceCode(PangaeaNoteContentType contentType) {
        if (contentType == null) {
            return false;
        }
        if (contentType.getMajor().equals("text")) {
            switch (contentType.getMinor()) {
                case "c":
                case "cpp":
                case "java":
                case "javascript":
                case "sh": {
                    return true;
                }
            }
        }
        return false;

    }
}
