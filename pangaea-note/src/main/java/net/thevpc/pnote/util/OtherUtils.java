/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.util;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Color;
import net.thevpc.echo.FontPosture;
import net.thevpc.echo.FontWeight;
import net.thevpc.echo.WritableTextStyle;
import net.thevpc.echo.api.AppFont;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.api.components.AppTextControl;
import net.thevpc.echo.api.components.AppToggleControl;
import net.thevpc.pnote.gui.PangaeaNoteApp;

import java.util.List;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 * @author vpc
 */
public class OtherUtils {

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

    public static void applyTitle(PangaeaNote n, AppTextControl textControl, boolean selected) {
        if (n == null) {
            return;
        }
        WritableTextStyle textStyle = textControl.textStyle();
        AppFont font = textStyle.font().get();

        textStyle.font().set(font == null ? null : font.derive(null, null, n.isTitleBold() ? FontWeight.BOLD : null,
                n.isTitleItalic() ? FontPosture.ITALIC : null));
        textStyle.underline().set(n.isTitleUnderlined());
        textStyle.strikethrough().set(n.isTitleStriked());
        PangaeaNoteApp app = (PangaeaNoteApp) textControl.app();
        Color b = Color.of(n.getTitleBackground(), app);
        if (b != null) {
            textControl.backgroundColor().set(b);
            textControl.opaque().set(true);
        } else {
            textControl.opaque().set(false);
        }
        b = Color.of(n.getTitleForeground(), app);
        if (b != null) {
            textControl.foregroundColor().set(b);
        }
        //do not apply to CheckBox or radio Button!
        textControl.text().set(Str.of((n.getName())));
        if (!(textControl instanceof AppToggleControl)) {
            String iconName = app.getNoteIcon(n);
            AppImage icon = app.iconSets().icon(iconName,textControl);
            textControl.icon().set(icon);
        }
    }
}
