/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.markdown;

import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteMarkdownService extends AbstractPangaeaNoteSourceCodeService {

    public static final String MARKDOWN = "text/markdown";

    public PangaeaNoteMarkdownService() {
        super(MARKDOWN, "file-markdown");
    }

}
