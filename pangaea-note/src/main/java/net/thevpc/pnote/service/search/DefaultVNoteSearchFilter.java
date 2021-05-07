/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import net.thevpc.pnote.service.search.strsearch.StringQuerySearch;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import java.util.stream.Stream;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
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
    public Stream<StringSearchResult<PangaeaNoteExt>> search(PangaeaNoteExt note, SearchProgressMonitor monitor,PangaeaNoteService service) {
        return p.search(new PangaeaNoteExtDocumentTextNavigator(service, note), monitor);
    }

}
