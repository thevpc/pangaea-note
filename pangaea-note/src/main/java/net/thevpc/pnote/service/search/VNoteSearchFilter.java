/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import java.util.stream.Stream;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

/**
 *
 * @author vpc
 */
public interface VNoteSearchFilter {

    Stream<StringSearchResult<PangaeaNote>> search(PangaeaNote note, SearchProgressMonitor monitor, PangaeaNoteApp app, PangaeaNoteFrame frame);
}
