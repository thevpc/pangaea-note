package net.thevpc.pnote.gui.editor.editorcomponents.source;

import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.Path;
import net.thevpc.echo.Button;
import net.thevpc.echo.ColorButton;
import net.thevpc.echo.RichHtmlEditor;
import net.thevpc.echo.api.TextAlignment;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppRichHtmlEditor;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.function.Consumer;

public class RichHtmlToolBarHelper {
    public static void prepare(PangaeaNoteFrame frame) {
        frame.findOrCreateToolBar("RichHtml", editToolBar -> {
            PangaeaNoteApp app = frame.app();
            WithEnableIfRichHtmlEditor enableIfRichHtmlEditor = new WithEnableIfRichHtmlEditor(app);

            AppComponent folder = editToolBar.children().addFolder(Path.of("Styles"));
//            System.out.println(folder.smallIcon().getId());
            folder.icon().set(Str.i18n("/mainFrame/toolBar/RichHtml/Styles.icon"));
//            folder.smallIcon().reEvalValue();
            folder.with(enableIfRichHtmlEditor);
            for (String tag : new String[]{"h1", "h2", "h3", "h5", "h6", "pre", "div", "p", "ol", "ul"}) {
                editToolBar.children().add(new Button("insert-" + tag, event -> {
                    //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                    AppComponent fo = app.toolkit().focusOwner().get();
                    if (fo instanceof AppRichHtmlEditor) {
                        RichHtmlEditor t = (RichHtmlEditor) fo;
                        t.runTextInsertTag(tag);
                    }
                }, app).with(enableIfRichHtmlEditor), Path.of("Styles/*"));
            }

            editToolBar.children().add(new Button("font-bold", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppRichHtmlEditor) {
                    RichHtmlEditor t = (RichHtmlEditor) fo;
                    t.runTextBold();
                }
            }, app).with(enableIfRichHtmlEditor));
            editToolBar.children().add(new Button("font-italic", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppRichHtmlEditor) {
                    RichHtmlEditor t = (RichHtmlEditor) fo;
                    t.runTextItalic();
                }
            }, app).with(enableIfRichHtmlEditor));
            editToolBar.children().add(new Button("font-underline", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppRichHtmlEditor) {
                    RichHtmlEditor t = (RichHtmlEditor) fo;
                    t.runTextUnderline();
                }
            }, app).with(enableIfRichHtmlEditor));
            editToolBar.children().add(new Button("font-strike-through", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppRichHtmlEditor) {
                    RichHtmlEditor t = (RichHtmlEditor) fo;
                    t.runTextStrikeThrough();
                }
            }, app).with(enableIfRichHtmlEditor));
            for (TextAlignment align : TextAlignment.values()) {
                editToolBar.children().add(new Button("align-" + (align.name().toLowerCase()), event -> {
                    //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                    AppComponent fo = app.toolkit().focusOwner().get();
                    if (fo instanceof AppRichHtmlEditor) {
                        RichHtmlEditor t = (RichHtmlEditor) fo;
                        t.runTextAlignment(align);
                    }
                }, app).with(enableIfRichHtmlEditor));
            }
            editToolBar.children().add(new ColorButton("foreground-color", app)
                    .with(enableIfRichHtmlEditor)
                    .with((ColorButton c) -> {
                        c.value().onChange(cc -> {
                            AppComponent fo = app.toolkit().focusOwner().get();
                            if (fo instanceof AppRichHtmlEditor) {
                                RichHtmlEditor t = (RichHtmlEditor) fo;
                                if (t.textSelection().get().length() > 0) {
                                    t.runTextForegroundColor(c.value().get());
                                } else {
                                    t.textStyle().foregroundColor().set(c.value().get());
                                }
                            }
                        });
                    })
            );
            editToolBar.children().add(new ColorButton("background-color", app)
                    .with(enableIfRichHtmlEditor)
                    .with((ColorButton c) -> {
                        c.value().onChange(cc -> {
                            AppComponent fo = app.toolkit().focusOwner().get();
                            if (fo instanceof AppRichHtmlEditor) {
                                RichHtmlEditor t = (RichHtmlEditor) fo;
                                if (t.textSelection().get().length() > 0) {
                                    t.runTextBackgroundColor(c.value().get());
                                } else {
                                    t.textStyle().backgroundColor().set(c.value().get());
                                }
                            }
                        });
                    })
            );
        });
    }

    private static class WithEnableIfRichHtmlEditor implements Consumer<AppComponent> {
        private final PangaeaNoteApp app;

        public WithEnableIfRichHtmlEditor(PangaeaNoteApp app) {
            this.app = app;
        }

        @Override
        public void accept(AppComponent x) {
            app.toolkit().focusOwner().onChangeAndInit(() -> {
                AppComponent fo = app.toolkit().focusOwner().get();
                boolean copyEnabled = false;
                if (fo instanceof AppRichHtmlEditor) {
                    copyEnabled = true;
                }
                x.enabled().set(copyEnabled);
                x.visible().set(
                        !app.hideDisabled().get() || copyEnabled
                );
            });
        }
    }
}
