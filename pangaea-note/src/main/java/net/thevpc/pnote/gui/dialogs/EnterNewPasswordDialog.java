/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.CancelException;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.security.PasswordHandler;

import net.thevpc.echo.constraints.AllFill;
import net.thevpc.echo.constraints.AllGrow;
import net.thevpc.echo.constraints.AllMargins;
import net.thevpc.echo.constraints.ContainerGrow;

/**
 * @author vpc
 */
public class EnterNewPasswordDialog{

    private Panel panel;
    private PasswordField passwordComponent1;
    private PasswordField passwordComponent2;

    private boolean ok = false;
    private String path;
    private PasswordHandler ph;
    private PangaeaNoteFrame frame;

    public EnterNewPasswordDialog(PangaeaNoteFrame frame, String path, PasswordHandler ph)  {
        super();
        this.ph = ph;
        this.frame = frame;
        passwordComponent1 = new PasswordField(frame.app());

        passwordComponent2 = new PasswordField(frame.app());

        panel=new GridPane(1,frame.app())
                .with(p->{
                    p.parentConstraints().addAll(AllMargins.of(3),AllFill.HORIZONTAL, ContainerGrow.TOP_ROW,AllGrow.HORIZONTAL);
                    p.children().addAll(
                            new net.thevpc.echo.Label(Str.i18n("Message.enter-password"),frame.app()),
                            passwordComponent1,
                            new net.thevpc.echo.Label(Str.i18n("Message.retype-password"),frame.app()),
                            passwordComponent2,
                        new Label(Str.i18n("Message.for-file"), frame.app()),
                            new net.thevpc.echo.TextField(null,Str.of(path),frame.app())
                                    .with((net.thevpc.echo.TextField t)->t.editable().set(false))
                    );
                });


//        passwordComponent2.setMinimumSize(new Dimension(50, 30));
//        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EnterNewPasswordDialog.class.getResource(
//                "/net/thevpc/pnote/forms/EnterNewPassword.gbl-form"
//        ));
//        gbs.bind("label", new JLabel(win.app().i18n().getString("Message.enter-password")));
//        gbs.bind("file", new JLabel(path));
//        gbs.bind("pwd1", passwordComponent1);
//        gbs.bind("pwd2", passwordComponent2);
//        panel = gbs.apply(new JPanel());
    }

    protected void install() {
    }

    protected void uninstall() {
    }

    protected void ok() {
        uninstall();
        this.ok = true;
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
    }

    public String showDialog() {
        while (true) {
            install();
            this.ok = false;
            new Alert(frame)
                            .with((Alert a)->{
                                a.title().set(Str.i18n("Message.passwordTitle"));
                                a.headerText().set(Str.i18n("Message.passwordHeader"));
                                a.headerIcon().set(Str.of("lock"));
                            })
                    .setContent(panel)
                    .withOkCancelButtons(
                            (a) -> {
                                ok();
                                a.getDialog().closeDialog();
                            },
                            (a) -> {
                                cancel();
                                a.getDialog().closeDialog();
                            }
                            )
                    .showDialog(null);
            try {
                return get();
            } catch (Exception ex) {
                if (!ph.reTypePasswordOnError()) {
                    throw new CancelException();
                }
                //exHandler.accept(ex);
            }
        }
    }

    public String get() {
        if (ok) {
            String s1 = passwordComponent1.text().get().value();
            String s2 = passwordComponent2.text().get().value();
            if (s1 != null && s1.trim().length() > 0 && s1.equals(s2)) {
                return s1;
            }
            throw new IllegalArgumentException(frame.app().i18n().getString("Message.passwordsDoNotMatch"));
        }
        return null;
    }

}
