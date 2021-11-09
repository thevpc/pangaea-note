/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteExtSearchResult {

    private PangaeaNote rootNote;
    private Stream<StringSearchResult<PangaeaNote>> stream;

    public PangaeaNoteExtSearchResult(PangaeaNote rootNote, Stream<StringSearchResult<PangaeaNote>> stream) {
        this.rootNote = rootNote;
        this.stream = stream;
    }

    public PangaeaNote getRootNote() {
        return rootNote;
    }

    public List<StringSearchResult<PangaeaNote>> list() {
        return stream.collect(Collectors.toList());
    }

    public Stream<StringSearchResult<PangaeaNote>> stream() {
        return stream;
    }

}
