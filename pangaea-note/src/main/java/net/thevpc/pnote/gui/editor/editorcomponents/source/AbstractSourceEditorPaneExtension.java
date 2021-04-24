/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.text.EditorKit;
import net.thevpc.common.swing.JDropDownButton;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public abstract class AbstractSourceEditorPaneExtension implements SourceEditorPaneExtension {

    public static Action al(ActionListener a) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                a.actionPerformed(e);
            }
        };
    }

    public static class ActionInfo {

        private String id;
        private ActionListener listener;
        private String messageId;
        private String constMessage;
        private Icon constIcon;

        public ActionInfo(String id, ActionListener a) {
            this.id = id;
            this.listener = a;
        }

        public String getMessageId() {
            return messageId;
        }

        public ActionInfo setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public String getConstMessage() {
            return constMessage;
        }

        public ActionInfo setConstMessage(String staticMessage) {
            this.constMessage = staticMessage;
            return this;
        }

        public Icon getConstIcon() {
            return constIcon;
        }

        public ActionInfo setConstIcon(Icon constIcon) {
            this.constIcon = constIcon;
            return this;
        }

    }

    public static void addActionList(String id, ActionInfo[] sub, JToolBar bar, JPopupMenu popup, Context context) {
        Application app = context.app();
        addActionList(id, sub, bar, popup, app);
    }
    public static void addActionList(String id, ActionInfo[] sub, JToolBar bar, JPopupMenu popup, Application app) {
        if (bar != null) {
            JDropDownButton insertMenu = new JDropDownButton("");
            insertMenu.setHideActionText(true);
            SwingApplicationsUtils.registerButton(insertMenu, null, "$Action." + id+".icon", app);
            insertMenu.setQuickActionDelay(0);
            for (ActionInfo actionInfo : sub) {
                if (actionInfo == null) {
                    insertMenu.addSeparator();
                } else {
                    Action a = null;
                    if (!(actionInfo.listener instanceof Action)) {
                        a = al(actionInfo.listener);
                    } else {
                        a = (Action) actionInfo.listener;
                    }
                    String messageId = "Action." + actionInfo.id;
                    String iconId = "$Action." + actionInfo.id + ".icon";
                    if (actionInfo.messageId != null) {
                        messageId = actionInfo.messageId;
                    }
                    if (actionInfo.constMessage != null) {
                        messageId = null;
                        a.putValue(AbstractAction.NAME, actionInfo.constMessage);
                    }
                    if (actionInfo.constIcon != null) {
                        iconId = null;
                        a.putValue(AbstractAction.SMALL_ICON, actionInfo.constIcon);
                    }
                    insertMenu.add(SwingApplicationsUtils.registerAction(a, messageId, iconId, app));
                }
            }
            bar.add(insertMenu);
        }
        if (popup != null) {
            JMenu insertMenu = new JMenu("");
            SwingApplicationsUtils.registerButton(insertMenu, "Action."+id, "$Action."+id+".icon", app);
            for (ActionInfo actionInfo : sub) {
                if (actionInfo == null) {
                    insertMenu.addSeparator();
                } else {
                    Action a = null;
                    if (!(actionInfo.listener instanceof Action)) {
                        a = al(actionInfo.listener);
                    } else {
                        a = (Action) actionInfo.listener;
                    }
                    String messageId = "Action." + actionInfo.id;
                    String iconId = "$Action." + actionInfo.id + ".icon";
                    if (actionInfo.messageId != null) {
                        messageId = actionInfo.messageId;
                    }
                    if (actionInfo.constMessage != null) {
                        messageId = null;
                        a.putValue(AbstractAction.NAME, actionInfo.constMessage);
                    }
                    if (actionInfo.constIcon != null) {
                        iconId = null;
                        a.putValue(AbstractAction.SMALL_ICON, actionInfo.constIcon);
                    }
                    insertMenu.add(SwingApplicationsUtils.registerAction(a, messageId, iconId, app));
                }
            }
            popup.add(insertMenu);
        }
    }

    public void addAction(ActionInfo id, JToolBar bar, JPopupMenu popup, Context context) {
        addActionListener(id.id, id.listener, bar, popup, context);
    }

    public void addActionListener(String id, ActionListener a, JToolBar bar, JPopupMenu popup, Context context) {
        Action pa = prepareAction(id, al(a), context);
        if (bar != null) {
            bar.add(pa);
        }
        if (popup != null) {
            popup.add(pa);
        }
    }

    public void addAction(String id, Action a, JToolBar bar, JPopupMenu popup, Context context) {
        Action pa = prepareAction(id, a, context);
        if (bar != null) {
            bar.add(pa);
        }
        if (popup != null) {
            popup.add(pa);
        }
    }

    public static void addSeparator(JToolBar bar, JPopupMenu popup, Context context) {
        if (bar != null) {
            bar.addSeparator();
        }
        if (popup != null) {
            popup.addSeparator();
        }
    }
    public static void addSeparator(JToolBar bar, JPopupMenu popup, Application context) {
        if (bar != null) {
            bar.addSeparator();
        }
        if (popup != null) {
            popup.addSeparator();
        }
    }

    public Action uninstallAction(Action a, Context context) {
        SwingApplicationsUtils.unregisterAction(a, context.app());
        return a;
    }

    public Action prepareAction(String id, Action a, Context context) {
        SwingApplicationsUtils.registerAction(a, "Action." + id, "$Action." + id + ".icon", context.app());
        context.add(a);
        return a;
    }

    public void addContentTypeChangeListener(Context context, ContentTypeChangeListener listener) {
        context.getPane().addPropertyChangeListener("editorKit", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                EditorKit ek = (EditorKit) evt.getNewValue();
                String ct = ek == null ? "" : ek.getContentType();
                if (ct == null) {
                    ct = "";
                }
                listener.onContentTypeChanged(context.service().normalizeContentType(ct), context);
            }
        }
        );

    }

    public static interface ContentTypeChangeListener {

        void onContentTypeChanged(PangaeaNoteContentType contentType, Context context);
    }

    public static class Context {

        private List<Action> actions = new ArrayList<>();
        private PangaeaNoteWindow sapp;
        private Application app;
        private JEditorPane pane;

        public Context(PangaeaNoteWindow sapp, JEditorPane pane) {
            this.sapp = sapp;
            this.app = sapp.app();
            this.pane = pane;
        }

        public JEditorPane getPane() {
            return pane;
        }

        public Application app() {
            return app;
        }

        public PangaeaNoteService service() {
            return sapp.service();
        }

        public void setApp(Application app) {
            this.app = app;
        }

        public void add(Action a) {
            actions.add(a);
        }

        public void setActionVisible(Action a, boolean b) {
            a.putValue("visible", b);
        }

        public void setActionEnabled(Action a, boolean b) {
            a.putValue("enabled", b);
        }

        public void setAllActionsVisible(boolean b) {
            for (Action action : actions) {
                setActionVisible(action, b);
            }
        }

        public void setAllActionsEnabled(boolean b) {
            for (Action action : actions) {
                setActionEnabled(action, b);
            }
        }

        public List<Action> getActions() {
            return actions;
        }

    }
}
