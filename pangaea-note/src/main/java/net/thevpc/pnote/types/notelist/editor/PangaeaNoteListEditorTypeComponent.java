/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.notelist.editor;

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
import net.thevpc.common.swing.ComponentBasedBorder;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.common.swing.list.JComponentList;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.common.swing.list.JComponentListItem;
import net.thevpc.echo.swing.core.swing.SwingApplicationsHelper;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.types.notelist.model.PangageaNoteListModel;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.types.notelist.PangaeaNoteListService;
import net.thevpc.pnote.types.plain.PangaeaNotePlainTextService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JComponentList<PangaeaNoteExt> componentList;
    private PangaeaNoteExt currentNote;
    private PangageaNoteListModel noteListModel;
    private PangaeaNoteGuiApp sapp;
    private boolean editable = true;
    private boolean compactMode = true;

    public PangaeaNoteListEditorTypeComponent(boolean compactMode, PangaeaNoteGuiApp sapp) {
        super(new BorderLayout());
        this.sapp = sapp;
        this.compactMode = compactMode;
        componentList = new JComponentList<PangaeaNoteExt>(new JComponentListItem<PangaeaNoteExt>() {
            @Override
            public JComponent createComponent(int pos, int size) {
                return new Item(sapp);
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
    public void setNote(PangaeaNoteExt note, PangaeaNoteGuiApp sapp) {
        this.currentNote = note;
        PangaeaNoteListService s = (PangaeaNoteListService) sapp.service().getContentTypeService(PangaeaNoteListService.C_NOTE_LIST);
        this.noteListModel = s.parseNoteListModel(note.getContent());
        if (this.noteListModel == null) {
            this.noteListModel = new PangageaNoteListModel();
        }
        componentList.setAllObjects(note.getChildren());
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
            sapp.onDocumentChanged();
            PangaeaNoteListService s = (PangaeaNoteListService) sapp.service().getContentTypeService(PangaeaNoteListService.C_NOTE_LIST);
            this.currentNote.setContent(s.stringifyNoteListInfo(this.noteListModel));
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
                currentNote.addChild(pos, PangaeaNoteExt.of(new PangaeaNote().setContentType(PangaeaNotePlainTextService.PLAIN)));
            }
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
        }
    }

    public void onDuplicateObjectAt(int pos) {
        if (currentNote != null) {
            PangaeaNoteExt p = currentNote.getChildren().get(pos);
            currentNote.addChild(pos, p.duplicate());
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
        }
    }

    public void onRemoveObjectAt(int pos) {
        if (currentNote != null) {
            currentNote.removeChild(pos);
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
        }
    }

    public void onMoveUpAt(int pos) {
        if (currentNote != null) {
            currentNote.moveUp(pos);
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
        }
    }

    public void onMoveDownAt(int pos) {
        if (currentNote != null) {
            currentNote.moveDown(pos);
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
        }
    }

    public void onMoveFirstAt(int pos) {
        if (currentNote != null) {
            currentNote.moveFirst(pos);
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
        }
    }

    public void onEditAt(int pos) {
        PangaeaNoteExt cc = currentNote.getChildren().get(pos);
        PangaeaNote n = new EditNoteDialog(sapp, cc).showDialog();
        if (n != null) {
            sapp.tree().fireNoteChanged(cc);
            this.invalidate();
            this.repaint();
        }
    }

    public void onMoveLastAt(int pos) {
        if (currentNote != null) {
            currentNote.moveLast(pos);
            componentList.setAllObjects(currentNote.getChildren());
            sapp.tree().fireNoteChanged(currentNote);
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
        private SwingApplicationsHelper.Tracker stracker;
        private int pos;
        private Font _font;
        private Color _foreground;
        private Color _background;
        private ComponentBasedBorder border;

        public Item(PangaeaNoteGuiApp sapp) {
            super(sapp, true);
            stracker = new SwingApplicationsHelper.Tracker(sapp.app());
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
            Color b = GuiHelper.parseColor(value.getTitleBackground());
            check.setBackground(b != null ? b : _background);
            b = GuiHelper.parseColor(value.getTitleForeground());
            check.setForeground(b != null ? b : _foreground);
            String iconName = sapp.service().getNoteIcon(value.toNote(), value.getChildren().size() > 0, false);
            Icon icon = sapp.app().iconSets().icon(iconName).get();
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
