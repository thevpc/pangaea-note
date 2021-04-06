/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vpc
 */
public class PangaeaNoteTypes {

    public static final String EDITOR_PANGAEA_NOTE_DOCUMENT = "pangaea-note-document";
    public static final String EDITOR_NOTE_LIST = "pangaea-note-list";
    public static final String EDITOR_OBJECT_LIST = "pangaea-object-list";
    public static final String EDITOR_FILE = "file";
    public static final String EDITOR_WYSIWYG = "wysiwyg";
    public static final String EDITOR_SOURCE = "source";
    public static final String EDITOR_UNSUPPORTED = "unsupported";


//    public static Set<String> ALL_CONTENT_TYPES = new LinkedHashSet<String>(
//            Arrays.asList(PangaeaContentTypes.PLAIN, PangaeaContentTypes.HTML, PangaeaContentTypes.MARKDOWN, PangaeaContentTypes.NUTS_TEXT_FORMAT, PangaeaContentTypes.JAVA, PangaeaContentTypes.JAVASCRIPT, PangaeaContentTypes.C, PangaeaContentTypes.CPP, PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT, PangaeaContentTypes.FILE, PangaeaContentTypes.NOTE_LIST, PangaeaContentTypes.OBJECT_LIST)
//    );

    public static Set<String> ALL_USER_ICONS = new TreeSet<String>(
            Arrays.asList(
                    "bell",
                    "book",
                    "circle",
                    "clock",
                    "coffee",
                    "database",
                    "disc",
                    "file",
                    "file",
                    "file-c",
                    "file-cpp",
                    "file-html",
                    "file-java",
                    "file-javascript",
                    "file-markdown",
                    "file-pangaea-note",
                    "file-nuts-text-format",
                    "file-text",
                    "gift",
                    "heart",
                    "moon",
                    "network",
                    "password",
                    "phone",
                    "smile",
                    "star",
                    "string",
                    "sun",
                    "url",
                    "wifi",
                    "datatype-audio",
                    "datatype-calendar",
                    "datatype-chart",
                    "datatype-checkbox",
                    "datatype-combobox",
                    "datatype-email",
                    "datatype-image",
                    "datatype-link",
                    "datatype-list",
                    "datatype-map",
                    "datatype-money",
                    "datatype-number",
                    "datatype-numbered-list",
                    "datatype-password",
                    "datatype-pen",
                    "datatype-phone",
                    "datatype-tags",
                    "datatype-text",
                    "datatype-textarea",
                    "datatype-url",
                    "datatype-video"
            )
    );

}
