/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.api.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public class PangaeaNoteExtSearchResult {

    private PangaeaNoteExt rootNote;
    private Stream<StringSearchResult<PangaeaNoteExt>> stream;

    public PangaeaNoteExtSearchResult(PangaeaNoteExt rootNote, Stream<StringSearchResult<PangaeaNoteExt>> stream) {
        this.rootNote = rootNote;
        this.stream = stream;
    }

    public PangaeaNoteExt getRootNote() {
        return rootNote;
    }

    public List<StringSearchResult<PangaeaNoteExt>> list() {
        return stream.collect(Collectors.toList());
    }

    public Stream<StringSearchResult<PangaeaNoteExt>> stream() {
        return stream;
    }

}
