/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search.strsearch;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.thevpc.echo.SearchQuery;

/**
 *
 * @author thevpc
 */
public class StringQuerySearch<T> {

    private List<Pattern> all = new ArrayList<>();
    private SearchQuery squery;
    public StringQuerySearch(SearchQuery squery) {
        this.squery=squery;
        if (squery != null) {
            switch (squery.getStrategy()) {
                case LITERAL: {
                    all.add(compitePattern(Pattern.quote(squery.getText().trim())));
                    break;
                }
                case SIMPLE: {
                    String query = squery == null ? null : squery.getText();
                    try {
                        StreamTokenizer st = new StreamTokenizer(new StringReader(query == null ? "" : query));
                        st.resetSyntax();
                        st.wordChars('a', 'z');
                        st.wordChars('A', 'Z');
                        st.wordChars('0', '9');
                        st.wordChars('-', '-');
                        st.wordChars('_', '_');
                        st.wordChars('+', '+');
                        st.wordChars('/', '/');
                        st.wordChars('*', '*');
                        st.wordChars('\'', '\'');
                        st.wordChars(128 + 32, 255);
                        st.whitespaceChars(0, ' ');
                        st.quoteChar('"');
                        while (st.nextToken() != StreamTokenizer.TT_EOF) {
                            switch (st.ttype) {
                                case StreamTokenizer.TT_WORD: {
                                    if (st.sval.length() > 0) {
                                        all.add(build(st.sval));
                                    }
                                    break;
                                }
                                case '"': {
                                    if (st.sval.length() > 0) {
                                        all.add(build(st.sval));
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                    break;
                }
                case REGEXP: {
                    all.add(compitePattern(squery.getText()));
                    break;
                }
                default:{
                    throw new IllegalArgumentException("unsupported");
                }
            }

        }
    }
    private Pattern compitePattern(String s){
        if(squery.isWholeWord()){
            s="\\b"+s+"\\b";
        }
        return Pattern.compile(s, 0 | (squery.isMatchCase() ? 0 : Pattern.CASE_INSENSITIVE));
    }

    public Stream<StringSearchResult<T>> search(DocumentTextNavigator<T> doc, SearchProgressMonitor monitor) {
        return searchByAllPatterns(doc, monitor);
    }

    private Stream<StringSearchResult<T>> searchByAllPatterns(DocumentTextNavigator<T> doc, SearchProgressMonitor monitor) {
        Stream<StringSearchResult<T>> s = Stream.empty();
        for (Pattern pattern : all) {
            List<StringSearchResult<T>> li = searchByPattern(pattern, doc, monitor).collect(Collectors.toList());
            if (li.isEmpty()) {
                return Stream.empty();
            }
            s = Stream.concat(s, li.stream());
        }
        return s;
    }

    private Stream<StringSearchResult<T>> searchByPattern(Pattern pattern, DocumentTextNavigator<T> doc, SearchProgressMonitor monitor) {
        monitor.searchProgress(doc);
        List<StringSearchResult<T>> found = new ArrayList<>();
        Iterator<DocumentTextPart<T>> it = doc.iterator();
        while (it.hasNext()) {
            DocumentTextPart p = it.next();
            monitor.searchProgress(p);
            Matcher m = pattern.matcher(p.getString());
            while (m.find()) {
                int s = m.start();
                String v = m.group();
                found.add(p.resolvePosition(s, v));
            }
        }
        return found.stream();
    }

    private Pattern build(String s) {
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '*': {
                    sb.append(".*");
                    break;
                }
                case '.':
                case '[':
                case ']':
                case '{':
                case '}':
                case '?':
                case '\\': {
                    sb.append('\\');
                    sb.append(chars[i]);
                    break;
                }
                default: {
                    sb.append(chars[i]);
                }
            }
        }
        return compitePattern(sb.toString());
    }
}
