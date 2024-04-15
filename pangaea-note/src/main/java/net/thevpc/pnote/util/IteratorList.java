package net.thevpc.pnote.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorList<T> implements Iterator<T> {
    private List<Iterator<T>> all = new ArrayList<>();
    private Iterator<T> curr;

    public void add(Iterator<T> a){
        if(a!=null) {
            all.add(a);
        }
    }
    @Override
    public boolean hasNext() {
        while (true) {
            if (curr == null) {
                if (all.isEmpty()) {
                    return false;
                }
                curr = all.remove(0);
                if (curr == null) {
                    return false;
                }
            }
            if (curr.hasNext()) {
                return true;
            }
            curr = null;
        }
    }

    @Override
    public T next() {
        if (curr != null) {
            return curr.next();
        }
        return null;
    }
}
