package net.thevpc.pnote.core.frame.util;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Color;
import net.thevpc.echo.FontPosture;
import net.thevpc.echo.FontWeight;
import net.thevpc.echo.WritableTextStyle;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.AppFont;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.api.components.AppTextControl;
import net.thevpc.echo.api.components.AppToggleControl;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

public class PangaeaNoteLabelHelper {
    public static void formalLabel(LabelFormat n, AppTextControl textControl) {
        if (n == null) {
            return;
        }
        WritableTextStyle textStyle = textControl.textStyle();
        AppFont font = textStyle.font().get();

        boolean titleBold = n.isBold();
        boolean titleItalic = n.isItalic();
        boolean titleUnderlined = n.isUnderlined();

        textStyle.font().set(font == null ? null : font.derive(null, null, titleBold ? FontWeight.BOLD : null,
                titleItalic ? FontPosture.ITALIC : null));
        textStyle.underline().set(titleUnderlined);
        textStyle.strikethrough().set(n.isStriked());
        PangaeaNoteApp app = (PangaeaNoteApp) textControl.app();
        AppColor b = Color.of(n.getBackgroundColor(), app);
        if (b != null) {
            textControl.backgroundColor().set(b);
            textControl.opaque().set(true);
        } else {
            textControl.opaque().set(false);
        }
        b = Color.of(n.getForegroundColor(), app);
        if (b != null) {
            textControl.foregroundColor().set(b);
        }
        //do not apply to CheckBox or radio Button!
        textControl.text().set(Str.of((n.getText())));
        if (!(textControl instanceof AppToggleControl)) {
            String iconName = n.getIcon();
            AppImage icon = app.iconSets().icon(iconName, textControl);
            textControl.icon().set(icon);
        }
    }

    public static class LabelFormat {
        private boolean bold;
        private boolean italic;
        private boolean underlined;
        private boolean striked;
        private String backgroundColor;
        private String foregroundColor;
        private String text;
        private String icon;

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public LabelFormat setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public String getForegroundColor() {
            return foregroundColor;
        }

        public LabelFormat setForegroundColor(String foregroundColor) {
            this.foregroundColor = foregroundColor;
            return this;
        }

        public boolean isStriked() {
            return striked;
        }

        public LabelFormat setStriked(boolean striked) {
            this.striked = striked;
            return this;
        }

        public boolean isBold() {
            return bold;
        }

        public LabelFormat setBold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public boolean isItalic() {
            return italic;
        }

        public LabelFormat setItalic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public boolean isUnderlined() {
            return underlined;
        }

        public LabelFormat setUnderlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        public String getText() {
            return text;
        }

        public LabelFormat setText(String text) {
            this.text = text;
            return this;
        }

        public String getIcon() {
            return icon;
        }

        public LabelFormat setIcon(String icon) {
            this.icon = icon;
            return this;
        }
    }
}
