/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.util;

import net.thevpc.common.props.WritableList;

import java.util.List;

/**
 * @author thevpc
 */
public class PNoteUtils {

    public static boolean nonNullAndTrue(Boolean b) {
        return b != null && b;
    }

    public static boolean isLenientEquals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            if (b instanceof String) {
                if (b.toString().trim().isEmpty()) {
                    return true;
                }
                return false;
            }
            return true;
        }
        if (b == null) {
            if (a instanceof String) {
                if (a.toString().trim().isEmpty()) {
                    return true;
                }
                return false;
            }
            return true;
        }
        if (a instanceof String && b instanceof String) {
            if (a.toString().trim().equals(b.toString().trim())) {
                return true;
            }
            return false;
        }
        return a.equals(b);
    }

    public static boolean nullOrTrue(Boolean b) {
        return b == null || b;
    }

    public static String toEscapedName(String in) {
        return toEscapedString(in, '`', false, "<no-name>");
    }

    public static String toEscapedValue(String in) {
        return toEscapedString(in, '\"', true, "null");
    }

    public static String toEscapedString(String in, char quoteType, boolean always, String nullValue) {
        if (in == null) {
            if (nullValue == null) {
                if (always && quoteType != '\0') {
                    return "null";
                }
                return "<null>";
            } else {
                return nullValue;
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean hasSpace = true;
        for (char c : in.toCharArray()) {
            switch (c) {
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                case ' ': {
                    if (quoteType == '\0') {
                        sb.append("\\ ");
                    } else {
                        sb.append(" ");
                    }
                    hasSpace = true;
                    break;
                }
                case '`':
                case '\'':
                case '\"': {
                    if (quoteType == c) {
                        sb.append('\\').append(c);
                    } else {
                        sb.append(c);
                    }
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        if (hasSpace) {
            always = true;
        }
        if (always && quoteType != '\0') {
            sb.insert(0, quoteType);
            sb.append(quoteType);
        }
        return sb.toString();
    }

    public static <T> boolean moveFirst(List<T> values, int index) {
        return switchListValues(values,index,0);
    }
    public static <T> boolean moveLast(List<T> values, int index) {
        return switchListValues(values,index,values.size());
    }

    public static <T> boolean moveUp(List<T> values, int index) {
        return switchListValues(values,index,index-1);
    }

    public static <T> boolean moveDown(List<T> values, int index) {
        return switchListValues(values,index,index+1);
    }

    public static <T> boolean moveWritableListItemFirst(WritableList<T> values, WritableList<T> selection, int index) {
        return switchWritableListValues(values,selection,index,0);
    }
    public static <T> boolean moveWritableListItemLast(WritableList<T> values, WritableList<T> selection, int index) {
        return switchWritableListValues(values,selection,index,values.size());
    }

    public static <T> boolean moveWritableListItemUp(WritableList<T> values, WritableList<T> selection, int index) {
        return switchWritableListValues(values,selection,index,index-1);
    }

    public static <T> boolean moveWritableListItemDown(WritableList<T> values, WritableList<T> selection, int index) {
        return switchWritableListValues(values,selection,index,index+1);
    }

    public static <T> boolean switchListValues(List<T> values, int index1, int index2) {
        if (values != null) {
            if (index1 >= 0 && index1 < values.size()) {
                if (index2 >= 0 && index2 < values.size()) {
                    T a = values.get(index1);
                    values.set(index1, values.get(index2));
                    values.set(index2, a);
                }
            }
        }
        return false;
    }

    public static <T> boolean switchWritableListValues(WritableList<T> values, WritableList<T> selection,int index1, int index2) {
        if (values != null) {
            if (index1 >= 0 && index1 < values.size()) {
                if (index2 >= 0 && index2 < values.size()) {
                    T a = values.get(index1);
                    values.set(index1, values.get(index2));
                    values.set(index2, a);
                    if(selection!=null) {
                        selection.set(a);
                    }
                }
            }
        }
        return false;
    }

    public static String escapeHtml(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
