/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

/**
 *
 * @author vpc
 */
public class SearchQuery {
    private String text;
    private boolean matchCase;
    private boolean wholeWord;
    private Strategy strategy;

    public SearchQuery(String text, boolean matchCase, boolean wholeWord, Strategy strategy) {
        this.text = text;
        this.matchCase = matchCase;
        this.wholeWord = wholeWord;
        this.strategy = strategy;
    }

    public String getText() {
        return text;
    }

    public boolean isMatchCase() {
        return matchCase;
    }

    public boolean isWholeWord() {
        return wholeWord;
    }

    public Strategy getStrategy() {
        return strategy;
    }
    
    public static enum Strategy{
        LITERAL,
        SIMPLE,
        REGEXP,
    }
}
