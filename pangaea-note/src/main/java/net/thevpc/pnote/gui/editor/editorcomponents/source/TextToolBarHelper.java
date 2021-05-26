package net.thevpc.pnote.gui.editor.editorcomponents.source;

import net.thevpc.echo.Button;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppEditTextControl;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

public class TextToolBarHelper {
    public static void prepare(PangaeaNoteFrame frame) {
        frame.findOrCreateToolBar("Edit", editToolBar -> {
            PangaeaNoteApp app = frame.app();
            editToolBar.children().add(new Button("copy", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppEditTextControl) {
                    AppEditTextControl t = (AppEditTextControl) fo;
                    app.clipboard().putString(t.textSelection().get());
                }
            }, app).with(
                    x -> app.toolkit().focusOwner().onChangeAndInit(() -> {
                        AppComponent fo = app.toolkit().focusOwner().get();
                        boolean copyEnabled = false;
                        if (fo instanceof AppEditTextControl) {
                            copyEnabled = true;
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));
            editToolBar.children().add(new Button("paste", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppEditTextControl) {
                    if (fo.editable().get()) {
                        AppEditTextControl t = (AppEditTextControl) fo;
                        t.replaceSelection(app.clipboard().getString());
                    }
                }
            }, app).with(
                    x -> app.toolkit().focusOwner().onChangeAndInit(() -> {
                        AppComponent fo = app.toolkit().focusOwner().get();
                        boolean copyEnabled = false;
                        if (fo instanceof AppEditTextControl) {
                            if (fo.editable().get()) {
                                copyEnabled = true;
                            }
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));
            editToolBar.children().add(new Button("cut", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                AppComponent fo = app.toolkit().focusOwner().get();
                if (fo instanceof AppEditTextControl) {
                    if (fo.editable().get()) {
                        AppEditTextControl t = (AppEditTextControl) fo;
                        app.clipboard().putString(t.textSelection().get());
                        t.replaceSelection("");
                    }
                }
            }, app).with(
                    x -> app.toolkit().focusOwner().onChangeAndInit(() -> {
                        AppComponent fo = app.toolkit().focusOwner().get();
                        boolean copyEnabled = false;
                        if (fo instanceof AppEditTextControl) {
                            if (fo.editable().get()) {
                                copyEnabled = true;
                            }
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));
        });

    }
}
