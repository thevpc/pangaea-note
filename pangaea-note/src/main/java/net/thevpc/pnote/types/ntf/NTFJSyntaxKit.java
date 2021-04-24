package net.thevpc.pnote.types.ntf;

import net.thevpc.jeep.JTokenType;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.editor.JSyntaxKit;
import net.thevpc.jeep.editor.JSyntaxStyle;
import net.thevpc.jeep.editor.JSyntaxStyleManager;

import java.util.regex.Pattern;
import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JTokenConfigBuilder;
import net.thevpc.jeep.core.DefaultJeep;
import net.thevpc.jeep.core.JTokenState;
import net.thevpc.jeep.core.tokens.JTokenPatternOrder;
import net.thevpc.jeep.core.tokens.SimpleTokenPattern;
import net.thevpc.jeep.editor.ColorResource;
import net.thevpc.jeep.impl.JEnumDefinition;
import net.thevpc.jeep.impl.JEnumTypeRegistry;
import net.thevpc.jeep.impl.tokens.JTokenizerImpl;
import net.thevpc.jeep.impl.tokens.RegexpBasedTokenPattern;
import net.thevpc.nuts.NutsSession;

public class NTFJSyntaxKit extends JSyntaxKit {

    public static final int OFFSET_LEFT_PARENTHESIS = 80;
    public static final int OFFSET_RIGHT_CURLY_BRACKET = 88;
    public static final int OFFSET_COMMA = 90;
    public static final int TT_STAR1 = -1100;
    public static final int TT_STAR2 = -1101;
    public static final int TT_STAR3 = -1102;
    public static final int TT_TITLE1 = -1103;
    public static final int TT_TITLE2 = -1104;
    public static final int TT_TITLE3 = -1105;
    public static final int TT_TITLE4 = -1106;
    public static final int TT_TITLE5 = -1107;
    public static final int TT_TITLE6 = -1108;
    public static final int TT_TITLE7 = -1109;
    public static final int TT_TITLE8 = -1110;
    public static final int TT_TITLE9 = -1111;
    public static final int TT_PRE = -1112;
    public static final int TT_CODE = -1113;
//    public static final int TT_ANY = -1114;

    public static class LangState extends JTokenState {

        public static final int STATE_DEFAULT = 1;

        public static final JEnumDefinition<LangState> _ET = JEnumTypeRegistry.INSTANCE
                .register(LangState.class)
                .addConstIntFields(LangState.class, f -> f.getName().startsWith("STATE_"));

        public static class Enums {

            public static final LangState STATE_DEFAULT = _ET.valueOf("STATE_DEFAULT");
        }

        private LangState(JEnumDefinition type, String name, int value) {
            super(type, name, value);
        }
    }

    private static NutsSession session;
    private static JContext langContext;

//    public static void main(String[] args) {
//        JTokenizer a = getSingleton().tokens().of(
//"<pre>\n" +
//"     __        __    \n" +
//"  /\\ \\ \\ _  __/ /______\n" +
//" /  \\/ / / / / __/ ___/\n" +
//"/ /\\  / /_/ / /_(__  )\n" +
//"\\_\\ \\/\\__,_/\\__/____/    version 0.8.1.0\n" +
//"</pre>\n"
//                );
//        for (JToken t : a) {
//            System.out.println(t);
//        }
//    }

    public NTFJSyntaxKit() {
        super();
        JContext jContext = getSingleton();
        JSyntaxStyleManager styles = new JSyntaxStyleManager();
        JSyntaxStyle bold = new JSyntaxStyle(null, JSyntaxStyle.BOLD);
        JSyntaxStyle italic = new JSyntaxStyle(null, JSyntaxStyle.ITALIC);
        JSyntaxStyle boldItalic = new JSyntaxStyle(null, JSyntaxStyle.BOLD | JSyntaxStyle.ITALIC);
        JSyntaxStyle keywords = new JSyntaxStyle(ColorResource.of(UI_KEY_RESERVED_WORD), JSyntaxStyle.BOLD);
        JSyntaxStyle comments = new JSyntaxStyle(ColorResource.of(UI_KEY_COMMENTS), JSyntaxStyle.ITALIC);
        JSyntaxStyle strings = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_STRING), JSyntaxStyle.BOLD);
        JSyntaxStyle numbers = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_NUMBER), JSyntaxStyle.PLAIN);
        JSyntaxStyle operators = new JSyntaxStyle(ColorResource.of(UI_KEY_OPERATOR), JSyntaxStyle.PLAIN);
        JSyntaxStyle separators = new JSyntaxStyle(ColorResource.of(UI_KEY_SEPARATOR), JSyntaxStyle.PLAIN);
        JSyntaxStyle regexs = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_REGEXP), JSyntaxStyle.PLAIN);
        JSyntaxStyle temporals = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_DATE), JSyntaxStyle.PLAIN);
        JSyntaxStyle directive = new JSyntaxStyle(ColorResource.of(UI_KEY_DIRECTIVE), JSyntaxStyle.PLAIN);
        JSyntaxStyle primitiveTypes = new JSyntaxStyle(ColorResource.of(UI_KEY_TYPE_PRIMITIVE), JSyntaxStyle.BOLD);
        JSyntaxStyle trueFalseLiterals = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_BOOLEAN), JSyntaxStyle.BOLD);
        JSyntaxStyle title1 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE1), JSyntaxStyle.BOLD);
        JSyntaxStyle title2 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE2), JSyntaxStyle.BOLD);
        JSyntaxStyle title3 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE3), JSyntaxStyle.BOLD);
        JSyntaxStyle title4 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE4), JSyntaxStyle.BOLD);
        JSyntaxStyle title5 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE5), JSyntaxStyle.BOLD);
        JSyntaxStyle title6 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE6), JSyntaxStyle.BOLD);
        JSyntaxStyle title7 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE7), JSyntaxStyle.BOLD);
        JSyntaxStyle title8 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE8), JSyntaxStyle.BOLD);
        JSyntaxStyle title9 = new JSyntaxStyle(ColorResource.of(UI_KEY_FORE9), JSyntaxStyle.BOLD);
        JSyntaxStyle pre = new JSyntaxStyle(ColorResource.of("OptionPane.warningDialog.titlePane.shadow;ToolWindowTitleBarUI.background.active.end;Tree.selectionBackground"), JSyntaxStyle.PLAIN).setFillColor(ColorResource.of("#fff5b9"));
        JSyntaxStyle code = new JSyntaxStyle(ColorResource.of("OptionPane.warningDialog.titlePane.shadow;ToolWindowTitleBarUI.background.active.end;Tree.selectionBackground"), JSyntaxStyle.PLAIN).setFillColor(ColorResource.of("#fae0ff"));
        for (JTokenDef o : jContext.tokens().tokenDefinitions()) {
            switch (o.ttype) {
                case JTokenType.TT_KEYWORD: {
                    switch (o.idName) {
                        case "true":
                        case "false": {
                            styles.setTokenIdStyle(o.id, trueFalseLiterals);
                            break;
                        }
                        default: {
                            styles.setTokenIdStyle(o.id, keywords);
                        }
                    }
                    break;
                }
                case JTokenType.TT_BLOCK_COMMENTS:
                case JTokenType.TT_LINE_COMMENTS: {
                    styles.setTokenIdStyle(o.id, comments);
                    break;
                }
                case JTokenType.TT_STRING: {
                    styles.setTokenIdStyle(o.id, strings);
                    break;
                }
                case JTokenType.TT_NUMBER: {
                    styles.setTokenIdStyle(o.id, numbers);
                    break;
                }
                case JTokenType.TT_OPERATOR: {
                    styles.setTokenIdStyle(o.id, operators);
                    break;
                }
                case JTokenType.TT_GROUP_SEPARATOR:
                case JTokenType.TT_SEPARATOR: {
                    styles.setTokenIdStyle(o.id, separators);
                    break;
                }
                case JTokenType.TT_REGEX: {
                    styles.setTokenIdStyle(o.id, regexs);
                    break;
                }
                case JTokenType.TT_TEMPORAL: {
                    styles.setTokenIdStyle(o.id, temporals);
                    break;
                }
                case TT_STAR1: {
                    styles.setTokenIdStyle(o.id, italic);
                    break;
                }
                case TT_STAR2: {
                    styles.setTokenIdStyle(o.id, bold);
                    break;
                }
                case TT_STAR3: {
                    styles.setTokenIdStyle(o.id, boldItalic);
                    break;
                }
                case TT_TITLE1: {
                    styles.setTokenIdStyle(o.id, title1);
                    break;
                }
                case TT_TITLE2: {
                    styles.setTokenIdStyle(o.id, title2);
                    break;
                }
                case TT_TITLE3: {
                    styles.setTokenIdStyle(o.id, title3);
                    break;
                }
                case TT_TITLE4: {
                    styles.setTokenIdStyle(o.id, title4);
                    break;
                }
                case TT_TITLE5: {
                    styles.setTokenIdStyle(o.id, title5);
                    break;
                }
                case TT_TITLE6: {
                    styles.setTokenIdStyle(o.id, title6);
                    break;
                }
                case TT_TITLE7: {
                    styles.setTokenIdStyle(o.id, title7);
                    break;
                }
                case TT_TITLE8: {
                    styles.setTokenIdStyle(o.id, title8);
                    break;
                }
                case TT_TITLE9: {
                    styles.setTokenIdStyle(o.id, title9);
                    break;
                }
                case TT_PRE: {
                    styles.setTokenIdStyle(o.id, pre);
                    break;
                }
                case TT_CODE: {
                    styles.setTokenIdStyle(o.id, code);
                    break;
                }
            }
        }
        setJcontext(jContext);
        setStyles(styles);
        //setCompletionSupplier(new HLJCompletionSupplier(jContext));
    }

    private static JContext getSingleton() {
        if (langContext == null) {
            langContext = new DefaultJeep();

            JTokenConfigBuilder config = langContext.tokens().config().builder();
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_STAR3, "**"), JTokenPatternOrder.ORDER_OPERATOR, Pattern.compile(
                    "|([*]{3}((?![*]{3}).)+[*]{3})|([_]{3}[^_][_]{3})"
            )));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_STAR2, "**"), JTokenPatternOrder.ORDER_OPERATOR, Pattern.compile(
                    "|([*]{2}((?![*]{2}).)+[*]{2})|([_]{2}[^_][_]{2})"
            )));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_STAR1, "*"), JTokenPatternOrder.ORDER_OPERATOR, Pattern.compile(
                    "([*][^*]+[*])|([_][^_][_])"
            )));
//
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE9, "#9"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{9}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE8, "#8"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{8}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE7, "#7"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{7}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE6, "#6"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{6}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE5, "#5"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{5}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE4, "#4"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{4}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE3, "#3"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{3}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE2, "#2"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{2}[^#]\\).*")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE1, "#"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#][^#]\\).*")));
//
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE9, "#9"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{9}[^#]+[#]{9}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE8, "#8"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{8}[^#]+[#]{8}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE7, "#7"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{7}[^#]+[#]{7}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE6, "#6"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{6}[^#]+[#]{6}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE5, "#5"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{5}[^#]+[#]{5}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE4, "#4"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{4}[^#]+[#]{4}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE3, "#3"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{3}[^#]+[#]{3}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE2, "#2"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#]{2}[^#]+[#]{2}")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_TITLE1, "#"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("^( )*[#][^#]+[#]")));
            config.addPatterns(new RegexpBasedTokenPattern(new JTokenDef(TT_CODE, "code"), JTokenPatternOrder.ORDER_OPERATOR,
                    Pattern.compile("```((?!```).)*```",Pattern.DOTALL)));

            config.setIdPattern(new SimpleTokenPattern() {
                @Override
                public boolean accept(CharSequence prefix, char c) {
                    return !Character.isWhitespace(c);
                }
            });

            config
                    .setParseWhitespaces(true)
                    .setParseDoubleQuotesString(true)
                    .setParseSimpleQuotesString(true);
//            config.addPatterns(new SeparatorsPattern("Separators1", OFFSET_LEFT_PARENTHESIS, JTokenType.Enums.TT_GROUP_SEPARATOR,
//                    "(", ")", "[", "]", "{", "}")
//            );
//
//            config.addPatterns(new SeparatorsPattern("Separators3", OFFSET_COMMA,
//                    JTokenPatternOrder.valueOf(JTokenPatternOrder.ORDER_OPERATOR.getValue() - 1, "BEFORE_OPERATOR"),
//                    JTokenType.Enums.TT_SEPARATOR,
//                    ",", ";", ":", "->", "@")
//            );

            langContext.tokens().setConfig(config);

            JTokenConfigBuilder config_DEFAULT = new JTokenConfigBuilder(langContext.tokens().config());
            langContext.tokens().setFactory((reader, config1, context) -> {
                JTokenizerImpl t = new JTokenizerImpl(reader);
                t.addState(LangState.Enums.STATE_DEFAULT, config_DEFAULT);
                t.pushState(LangState.Enums.STATE_DEFAULT);
                return t;
            });
        }
        return langContext;
    }

}
