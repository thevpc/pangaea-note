package net.thevpc.pnote.types.cpp;

import net.thevpc.jeep.JTokenType;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.editor.JSyntaxKit;
import net.thevpc.jeep.editor.JSyntaxStyle;
import net.thevpc.jeep.editor.JSyntaxStyleManager;

import java.awt.*;
import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JOperatorPrecedences;
import net.thevpc.jeep.JTokenConfigBuilder;
import net.thevpc.jeep.core.DefaultJeep;
import net.thevpc.jeep.core.JTokenState;
import net.thevpc.jeep.core.tokens.JTokenPatternOrder;
import net.thevpc.jeep.core.tokens.JavaIdPattern;
import net.thevpc.jeep.core.tokens.SeparatorsPattern;
import net.thevpc.jeep.core.tokens.SimpleTokenPattern;
import net.thevpc.jeep.editor.ColorResource;
import net.thevpc.jeep.impl.JEnumDefinition;
import net.thevpc.jeep.impl.JEnumTypeRegistry;
import net.thevpc.jeep.impl.JEnumTypes;
import net.thevpc.jeep.impl.tokens.JTokenizerImpl;
import net.thevpc.jeep.impl.tokens.JavaNumberTokenEvaluator;
import net.thevpc.nuts.NutsSession;

public class CppLangJSyntaxKit extends JSyntaxKit {

    public static final int OFFSET_LEFT_PARENTHESIS = 80;
    public static final int OFFSET_RIGHT_CURLY_BRACKET = 88;
    public static final int OFFSET_COMMA = 90;
    public static final int TOKEN_DIRECTIVE = -300;
    public static final JTokenDef TOKEN_DEF_DIRECTIVE = new JTokenDef(
            TOKEN_DIRECTIVE,
            "directive",
            TOKEN_DIRECTIVE, "directive", "#...");

    public static class LangState extends JTokenState {

        public static final int STATE_DEFAULT = 1;

        public static final JEnumDefinition<LangState> _ET = JEnumTypes.of(LangState.class);

        public static class Enums {

            public static final LangState STATE_DEFAULT = _ET.valueOf("STATE_DEFAULT");
        }

        private LangState(JEnumDefinition type, String name, int value) {
            super(type, name, value);
        }
    }

    private static NutsSession session;
    private static JContext langContext;
    private static boolean cpp;

    public CppLangJSyntaxKit(boolean cpp) {
        super();
        this.cpp = cpp;
        JContext jContext = getSingleton();
        JSyntaxStyleManager styles = new JSyntaxStyleManager();
        JSyntaxStyle keywords = new JSyntaxStyle(ColorResource.of(UI_KEY_RESERVED_WORD, Color.decode("#735db7")), JSyntaxStyle.BOLD);
        JSyntaxStyle comments = new JSyntaxStyle(ColorResource.of(UI_KEY_COMMENTS, Color.DARK_GRAY), JSyntaxStyle.ITALIC);
        JSyntaxStyle strings = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_STRING, Color.decode("#2b9946")), JSyntaxStyle.BOLD);
        JSyntaxStyle numbers = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_NUMBER, Color.RED.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle operators = new JSyntaxStyle(ColorResource.of(UI_KEY_OPERATOR, Color.cyan.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle separators = new JSyntaxStyle(ColorResource.of(UI_KEY_SEPARATOR, Color.red), JSyntaxStyle.PLAIN);
        JSyntaxStyle regexs = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_REGEXP, Color.MAGENTA.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle temporals = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_DATE, Color.pink.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle directive = new JSyntaxStyle(ColorResource.of(UI_KEY_DIRECTIVE, Color.pink.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle primitiveTypes = new JSyntaxStyle(ColorResource.of(UI_KEY_TYPE_PRIMITIVE, Color.decode("#aa557f")), JSyntaxStyle.BOLD);
        JSyntaxStyle trueFalseLiterals = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_BOOLEAN, Color.decode("#f1a100")), JSyntaxStyle.BOLD);
        for (JTokenDef o : jContext.tokens().tokenDefinitions()) {
            switch (o.ttype) {
                case JTokenType.TT_KEYWORD: {
                    switch (o.idName) {
                        case "int":
                        case "void":
                        case "boolean":
                        case "char":
                        case "byte":
                        case "short":
                        case "long":
                        case "float":
                        case "double": {
                            styles.setTokenIdStyle(o.id, primitiveTypes);
                            break;
                        }
                        case "true":
                        case "false":
                        case "null": {
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
                case TOKEN_DIRECTIVE: {
                    styles.setTokenIdStyle(o.id, directive);
                    break;
                }
            }
        }
        setJcontext(jContext);
        setStyles(styles);
        //setCompletionSupplier(new HLJCompletionSupplier(jContext));
    }

    private JContext getSingleton() {
        if (langContext == null) {
            langContext = new DefaultJeep();

            JTokenConfigBuilder config = langContext.tokens().config().builder();
            config
                    .setParseWhitespaces(true)
                    .setParseIntNumber(true)
                    .setParseFloatNumber(true)
                    .setParsetInfinity(true)
                    .setParseWhitespaces(true)
                    .setParseDoubleQuotesString(true)
                    .setParseSimpleQuotesString(true)
                    .setParseCStyleBlockComments()
                    .setParseCStyleLineComments();
            config.setIdPattern(new JavaIdPattern());
            config.addPatterns(new SeparatorsPattern("Separators1", OFFSET_LEFT_PARENTHESIS, JTokenType.Enums.TT_GROUP_SEPARATOR,
                    "(", ")", "[", "]", "{")
            );
            //this will be handled in a special way
            config.addPatterns(new SeparatorsPattern("Separators2",
                    OFFSET_RIGHT_CURLY_BRACKET,
                    JTokenPatternOrder.ORDER_OPERATOR,
                    JTokenType.Enums.TT_GROUP_SEPARATOR,
                    "}")
            );

            config.addPatterns(new SeparatorsPattern("Separators3", OFFSET_COMMA,
                    JTokenPatternOrder.valueOf(JTokenPatternOrder.ORDER_OPERATOR.getValue() - 1, "BEFORE_OPERATOR"),
                    JTokenType.Enums.TT_SEPARATOR,
                    ",", ";", ":", "->", "@")
            );
            config.addPatterns(
                    new SimpleTokenPattern(TOKEN_DEF_DIRECTIVE) {
                @Override
                public boolean accept(CharSequence prefix, char c) {
                    if (prefix.length() == 0) {
                        return c == '#';
                    }
                    return c > 32;
                }
            }
            );

            //numbers
            config.setNumberEvaluator(new JavaNumberTokenEvaluator());
            config.setNumberSuffixes(new char[]{'f', 'F'});

            config.addKeywords("public", "private", "protected", "abstract", "static", "final", "package", "import");
            config.addKeywords("try", "catch", "finally");
            config.addKeywords("void", "var", "val", "class", "interface", "extends", "return", "default");
            config.addKeywords("if", "else", "switch", "case", "break", "continue", "for", "do", "while");
            config.addKeywords("double", "float", "long", "int", "short", "byte", "float", "char", "boolean");
            config.addKeywords("null", "true", "false");
            config.addKeywords("strictfp", "volatile", "transient");

            config.addKeywords("struct", "const");
            config.addKeywords("super", "this", "operator");

            //other reserved words for future use
            config.addKeywords("record", "instanceof", "native", "synchronized");
            config.addKeywords("yield", "_", "it", "record");
            langContext.tokens().setConfig(config);

            langContext.operators().declareCStyleOperators();

            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_1, "=", "+=", "-=", "*=", "|=", "&=", "~=", "^=", "%=");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_3, "||");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_4, "&&");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_5, "|");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_6, "^");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_7, "&");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_8, "==", "!=");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_9, "<", ">", "<=", ">=");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_10, "<<", "<<<", ">>", ">>>");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_11, "+", "-");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_12, "*", "/", "%");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, "++", "--", "~");
            langContext.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_15, ".");

            langContext.operators().declareSpecialOperators("...");

            /*
         * this is the default state, handling HL tokens
             */
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
