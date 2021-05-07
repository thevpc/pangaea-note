/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Set;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.thevpc.common.swing.color.ColorUtils;
import net.thevpc.common.swing.border.ComponentBasedBorder;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.common.swing.list.JComponentList;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.common.swing.list.JComponentListItem;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JComponentList<PangaeaNoteExt> componentList;
    private PangaeaNoteExt currentNote;
    private PangaeaNoteListModel noteListModel;
    private PangaeaNoteWindow win;
    private boolean editable = true;
    private boolean compactMode = true;

    public PangaeaNoteListEditorTypeComponent(boolean compactMode, PangaeaNoteWindow win) {
        super(new BorderLayout());
        this.win = win;
        this.compactMode = compactMode;
        componentList = new JComponentList<PangaeaNoteExt>(new JComponentListItem<PangaeaNoteExt>() {
            @Override
            public JComponent createComponent(int pos, int size) {
                return new Item(win);
            }

            @Override
            public void setComponentValue(JComponent comp, PangaeaNoteExt value, int pos, int size) {
                Item b = (Item) comp;
                b.setValue(value, pos, size);
            }

            @Override
            public PangaeaNoteExt getComponentValue(JComponent comp, int pos) {
                return ((Item) comp).getValue(pos);
            }

            @Override
            public void uninstallComponent(JComponent comp) {
                ((Item) comp).onUninstall();
            }

            @Override
            public void setEditable(JComponent component, boolean editable, int pos, int size) {
                ((Item) component).setEditable(editable);
            }

        });
        JScrollPane scrollPane = new JScrollPane(componentList);
        scrollPane.setWheelScrollingEnabled(true);
        add(scrollPane);
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win) {
        this.currentNote = note;
        PangaeaNoteListService s = (PangaeaNoteListService) win.service().getContentTypeService(PangaeaNoteListService.LIST);
        this.noteListModel = s.elementToContent(note.getContent());
        if (this.noteListModel == null) {
            this.noteListModel = new PangaeaNoteListModel();
        }
        componentList.setAllObjects(note.getChildren());
        PangaeaNoteListLayout layout = noteListModel.getLayout();
        if (layout == null) {
            layout = PangaeaNoteListLayout.VERTICAL;
            noteListModel.setLayout(layout);
        }
        switch (layout) {
            case VERTICAL: {
                int cr = noteListModel.getColsRows();
                if (cr <= 1) {
                    componentList.setListLayout(new JComponentList.Vertical());
                } else {
                    if (cr >= 100) {
                        cr = 100;
                    }
                    componentList.setListLayout(new JComponentList.Grid(0, cr));
                }
                break;
            }

            case HORIZONTAL: {
                int cr = noteListModel.getColsRows();
                if (cr <= 1) {
                    componentList.setListLayout(new JComponentList.Horizontal());
                } else {
                    if (cr >= 100) {
                        cr = 100;
                    }
                    componentList.setListLayout(new JComponentList.Grid(cr, 0));
                }
                break;
            }
            case TAB: {
                componentList.setListLayout(new JComponentList.Tab());
                break;
            }
            default: {
                componentList.setListLayout(new JComponentList.Vertical());
            }
        }
        setEditable(!note.isReadOnly());
    }

    public boolean setSelectedName(String name, boolean sel) {
        if (this.currentNote != null && this.noteListModel != null) {
            Set<String> n = this.noteListModel.getSelectedNames();
            if (sel) {
                n.add(name);
            } else {
                n.remove(name);
            }
            win.onDocumentChanged();
            PangaeaNoteListService s = (PangaeaNoteListService) win.service().getContentTypeService(PangaeaNoteListService.LIST);
            this.currentNote.setContent(s.contentToElement(this.noteListModel));
        }
        return false;
    }

    public boolean isSelectedIndex(String name) {
        if (this.currentNote != null && this.noteListModel != null) {
            return this.noteListModel.getSelectedNames().contains(name);
        }
        return false;
    }

    public void onAddObjectAt(int pos) {
        if (currentNote != null) {
            if (currentNote.getChildren().size() > 0) {
                PangaeaNoteExt p = currentNote.getChildren().get(pos);
                currentNote.addChild(pos, PangaeaNoteExt.of(new PangaeaNote().setContentType(p.getContentType())));
            } else {
                currentNote.addChild(pos, PangaeaNoteExt.of(new PangaeaNote().setContentType(PangaeaNotePlainTextService.PLAIN.toString())));
            }
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    public void onDuplicateObjectAt(int pos) {
        if (currentNote != null) {
            PangaeaNoteExt p = currentNote.getChildren().get(pos);
            currentNote.addChild(pos, p.duplicate());
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    public void onRemoveObjectAt(int pos) {
        if (currentNote != null) {
            currentNote.removeChild(pos);
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    public void onMoveUpAt(int pos) {
        if (currentNote != null) {
            currentNote.moveUp(pos);
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    public void onMoveDownAt(int pos) {
        if (currentNote != null) {
            currentNote.moveDown(pos);
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    public void onMoveFirstAt(int pos) {
        if (currentNote != null) {
            currentNote.moveFirst(pos);
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    public void onEditAt(int pos) {
        PangaeaNoteExt cc = currentNote.getChildren().get(pos);
        PangaeaNote n = new EditNoteDialog(win, cc).showDialog();
        if (n != null) {
            win.tree().fireNoteChanged(cc);
            this.invalidate();
            this.repaint();
        }
    }

    public void onMoveLastAt(int pos) {
        if (currentNote != null) {
            currentNote.moveLast(pos);
            componentList.setAllObjects(currentNote.getChildren());
            win.tree().fireNoteChanged(currentNote);
        }
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        componentList.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    private class Item extends PangaeaNoteEditor {

        private JCheckBox check;
        private SwingApplicationsUtils.Tracker stracker;
        private int pos;
        private Font _font;
        private Color _foreground;
        private Color _background;
        private ComponentBasedBorder border;

        public Item(PangaeaNoteWindow win) {
            super(win, true);
            stracker = new SwingApplicationsUtils.Tracker(win.app());
            ComponentBasedBorder.ComponentBasedBorderBuilder b = ComponentBasedBorder.of(this).withCheckbox();
            border = b.install();
            check = (JCheckBox) border.getBorderComponent();
            check.addActionListener(e -> {
                setSelectedName(getNote().getName(), check.isSelected());
            });
            JPopupMenu bar = new JPopupMenu();
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onAddObjectAt(pos), "addToObjectList"))));
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onDuplicateObjectAt(pos), "duplicateInObjectList"))));
            bar.addSeparator();
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onRemoveObjectAt(pos), "removeInObjectList"))));
            bar.addSeparator();
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onMoveUpAt(pos), "moveUpInObjectList"))));
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onMoveDownAt(pos), "moveDownInObjectList"))));
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onMoveFirstAt(pos), "moveFirstInObjectList"))));
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onMoveLastAt(pos), "moveLastInObjectList"))));
            bar.addSeparator();
            bar.add(prepareButton(new JMenuItem(stracker.registerStandardAction(() -> onEditAt(pos), "NoteProperties"))));
            this.setComponentPopupMenu(bar);
        }

        public JMenuItem prepareButton(JMenuItem b) {
//            b.setHideActionText(true);
            return b;
        }

        public void setValue(PangaeaNoteExt value, int pos, int size) {
            this.pos = pos;
            String s = value.getName();
            if (s == null || s.length() == 0) {
                s = "no-name";
            }
            setNote(value);
            check.setText((pos + 1) + " - " + value.getName());
            check.setSelected(isSelectedIndex(value.getName()));
            if (_font == null) {
                _font = check.getFont();
            }
            if (_foreground == null) {
                _foreground = check.getForeground();
            }
            if (_background == null) {
                _background = check.getBackground();
            }
            check.setFont(GuiHelper.deriveFont(_font, value.isTitleBold(), value.isTitleItalic(), value.isTitleUnderlined(), value.isTitleStriked()));
            Color b = ColorUtils.parseColor(value.getTitleBackground());
            check.setBackground(b != null ? b : _background);
            b = ColorUtils.parseColor(value.getTitleForeground());
            check.setForeground(b != null ? b : _foreground);
            String iconName = win.service().getNoteIcon(value.toNote(), value.getChildren().size() > 0, false);
            Icon icon = win.app().iconSets().icon(iconName).get();
            border.setIcon(icon);
            repaint();
        }

        @Override
        public void setEditable(boolean editable) {
            super.setEditable(editable);
            check.setEnabled(editable);
            for (Action action : stracker.getActions()) {
                action.setEnabled(editable);
            }
        }

        public PangaeaNoteExt getValue(int pos) {
            return this.getNote();
        }

        public void onUninstall() {
            this.uninstall();
        }

    }
}
