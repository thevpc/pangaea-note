/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.pnote.core.types.sourcecode.html.PangaeaNoteHtmlService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.rich.PangaeaNoteRichService;

/**
 *
 * @author vpc
 */
public class PangaeaContentTypes {

    public static final PangaeaNoteMimeType UNSUPPORTED = PangaeaNoteMimeType.of("application/unsupported");
    public static final String PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION = "pnote";

    public static boolean isFormattedText(PangaeaNoteMimeType contentType) {
        String c = contentType == null ? "" : contentType.toString();
        return (c.equals(PangaeaNoteHtmlService.HTML.toString())
                || c.equals("text/x-nuts-text-format")
                || c.equals("text/markdown"));
    }

    public static boolean isXmlLike(PangaeaNoteMimeType contentType) {
        String c = contentType == null ? "" : contentType.toString();
        return (c.equals(PangaeaNoteHtmlService.HTML.toString())
                || c.equals(PangaeaNoteRichService.RICH_HTML.toString()));
    }

    public static boolean isSourceCode(PangaeaNoteMimeType contentType) {
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
