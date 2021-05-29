/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.search.strsearch.StringQuerySearch;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import java.util.stream.Stream;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;

/**
 *
 * @author vpc
 */
public class DefaultVNoteSearchFilter implements VNoteSearchFilter {

    private StringQuerySearch p;

    public DefaultVNoteSearchFilter(SearchQuery query) {
        p = new StringQuerySearch(query);
    }

    @Override
    public Stream<StringSearchResult<PangaeaNote>> search(PangaeaNote note, SearchProgressMonitor monitor, PangaeaNoteApp app, PangaeaNoteFrame frame) {
        return p.search(new PangaeaNoteExtDocumentTextNavigator(note, app), monitor);
    }

}
