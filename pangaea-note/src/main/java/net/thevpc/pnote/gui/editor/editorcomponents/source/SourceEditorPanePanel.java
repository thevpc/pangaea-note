/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.source;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.undo.UndoManager;
import net.thevpc.common.swing.SwingComponentUtils;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.jeep.editor.JSyntaxStyleManager;
import net.thevpc.pnote.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.SelectableElement;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.StringQuerySearch;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.types.rich.editor.RichEditor;

/**
 *
 * @author vpc
 */
public class SourceEditorPanePanel extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JEditorPaneBuilder editorBuilder;
    private PangaeaNoteExt currentNote;
    private boolean source;
    private boolean compactMode;
    private boolean editable = true;
    private PangaeaNoteWindow sapp;
    private SourceEditorPaneExtension textExtension = new SourceEditorPanePanelTextExtension();
    DocumentListener documentListener = new AnyDocumentListener() {
        public void anyChange(DocumentEvent e) {
            if (currentNote != null) {
//                System.out.println("update note:" + editorBuilder.editor().getText());
                sapp.onDocumentChanged();
                currentNote.setContent(sapp.service().stringToElement(editorBuilder.editor().getText()));
            }
        }
    };
    private SelectableElement selectableElement=new SelectableElement() {
        @Override
        public String getSelectedText() {
            String s = editorBuilder.editor().getSelectedText();
            return (s!=null && s.length()>0)?s:null;
        }
    };

    public SourceEditorPanePanel(boolean source, boolean compactMode, PangaeaNoteWindow sapp) {
        super(new BorderLayout());
        this.compactMode = compactMode;
        boolean lineNumbers = source;
        this.editorBuilder = new JEditorPaneBuilder();
        this.editorBuilder.setEditor(new JTextPane());
        for (PangaeaNoteTypeService contentTypeService : sapp.service().getContentTypeServices()) {
            EditorKit k=contentTypeService.getSourceEditorKit();
            if(k!=null){
                        editorBuilder.editor().setEditorKitForContentType(
                                contentTypeService.getContentType().toString()
                                , k);
            }
        }
        if (lineNumbers) {
            editorBuilder.addLineNumbers();
        }
        if (!compactMode) {
            editorBuilder.footer()
                    //                .add(new JLabel("example..."))
                    //                .add(new JSyntaxPosLabel(e, completion))
                    .addGlue()
                    .addCaret()
                    .end();
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
//        editorBuilder.footer()
//                //                .add(new JLabel("example..."))
//                //                .add(new JSyntaxPosLabel(e, completion))
//                .addGlue()
//                .addCaret()
//                .end() //                .setEditorKit(HadraLanguage.MIME_TYPE, new HLJSyntaxKit(jContext))
//                //                    .component()
//                .header();
        //.header().add(new JLabel(title))

//        this.setWheelScrollingEnabled(true);
        this.sapp = sapp;
        this.source = source;
        this.editorBuilder.editor().getDocument().addDocumentListener(documentListener);
        this.editorBuilder.editor().addPropertyChangeListener("document", e -> {
            Document o = (Document) e.getOldValue();
            Document n = (Document) e.getNewValue();
            if (o != null) {
                o.removeDocumentListener(documentListener);
            }
            if (n != null) {
                n.addDocumentListener(documentListener);
            }
        });
        JPopupMenu popup = editorBuilder.editor().getComponentPopupMenu();
        if (popup == null) {
            popup = new JPopupMenu();
            editorBuilder.editor().setComponentPopupMenu(popup);
        }
        textExtension.prepareEditor(editorBuilder, compactMode, sapp);
        if (!compactMode) {
            this.editorBuilder.header().addGlue();
        }
        this.editorBuilder.editor().addPropertyChangeListener("document", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                GuiHelper.installUndoRedoManager(editorBuilder.editor());
            }
        });
        JEditorPane ed = this.editorBuilder.editor();
        ed.getInputMap().put(KeyStroke.getKeyStroke("control F"), "search-text");
        ed.getActionMap().put("search-text",
                SwingApplicationsUtils.registerAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SearchDialog dialog = new SearchDialog(sapp);
                        dialog.setTitle(sapp.app().i18n().getString("Message.search.searchInDocument"));
                        dialog.setSearchText(ed.getSelectedText());
                        SearchQuery query = dialog.showDialog();
                        if (query != null) {
                            StringQuerySearch<String> fi = new StringQuerySearch(query);
                            Stream<StringSearchResult<String>> found = fi.search(StringDocumentTextNavigator.of(ed.getText()), SearchProgressMonitor.NONE);
                            found.forEach(x -> {
                                int from = x.getStart();
                                int to = x.getEnd();
                                highlight(from, to, HighlightType.SEARCH_MAIN);
                            });
                        }
                    }

                }, "search", "search", sapp.app())
        );
        ed.getInputMap().put(KeyStroke.getKeyStroke("control D"), "duplicate-text");
        ed.getActionMap().put("duplicate-text",
                SwingApplicationsUtils.registerAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            int ss = ed.getSelectionStart();
                            int se = ed.getSelectionEnd();
                            if (se > ss) {
                                ed.getDocument().insertString(se, ed.getDocument().getText(ss, se - ss), null);
                                ed.setCaretPosition(se + se - ss);
                                ed.setSelectionStart(se);
                                ed.setSelectionEnd(se + se - ss);
                            } else {
                                int currentLine = SwingComponentUtils.getLineFromOffset(ed, ed.getCaret().getDot());
                                int startPos = SwingComponentUtils.getLineStartOffsetForLine(ed, currentLine);
                                int endOffset = SwingComponentUtils.getLineEndOffsetForLine(ed, currentLine);
                                ed.getDocument().insertString(endOffset, ed.getDocument().getText(startPos, endOffset - startPos), null);
                                ed.setCaretPosition(ed.getCaret().getDot() + endOffset - startPos);
                            }
                        } catch (BadLocationException ex) {
                            Logger.getLogger(SourceEditorPanePanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }, "search", "search", sapp.app())
        );
        ed.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "reset-highlights");
        ed.getActionMap().put("reset-highlights",
                SwingApplicationsUtils.registerAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ed.getHighlighter().removeAllHighlights();
                    }

                }, "reset-highlights", "reset-highlights", sapp.app())
        );

        GuiHelper.installUndoRedoManager(editorBuilder.editor());
        if (source) {
            this.editorBuilder.editor().setFont(JSyntaxStyleManager.getDefaultFont());
        }
//        editorBuilder.editor().addPropertyChangeListener("editorKit", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                EditorKit ek = (EditorKit) evt.getNewValue();
//                String ct = ek == null ? "" : ek.getContentType();
//                if (ct == null) {
//                    ct = "";
//                }
//                for (EditorKitHeader toolbar : headers) {
//                    toolbar.component().setVisible(toolbar.acceptContentType(ct));
//                }
//            }
//        }
//        );
        add(editorBuilder.component());
        SwingComponentUtils.addZoomTextOnMouseWheel(editorBuilder.editor());

        ed.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent evt) {
                if (evt.getDot() == evt.getMark()) {
                    return;
                }

                JTextPane txtPane = (JTextPane) evt.getSource();
                DefaultHighlighter highlighter = (DefaultHighlighter) txtPane.getHighlighter();
                String selText = txtPane.getSelectedText();
                String contText = "";// = jTextPane1.getText();
                DefaultStyledDocument document = (DefaultStyledDocument) txtPane.getDocument();
                try {
                    contText = document.getText(0, document.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                if (contText.length() > 0) {
                    int index = 0;
                    List<int[]> all = new ArrayList<>();
                    while ((index = contText.indexOf(selText, index)) > -1) {
                        all.add(new int[]{index, selText.length() + index});
                        index = index + selText.length();
                    }
                    if (all.size() > 1) {
                        highlighter.removeAllHighlights();
                        for (int[] is : all) {
                            highlight(is[0], is[1], HighlightType.CARET);
                        }
                    }
                }
            }
        });
        ed.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                sapp.selectableElement().set(selectableElement);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(selectableElement==sapp.selectableElement().get()){
                    sapp.selectableElement().set(null);
                }
            }
        });

    }

//    public boolean isSupportedType(String contentType) {
//        return supported != null && supported.contains(contentType);
//    }
    @Override
    public void uninstall() {
        textExtension.uninstall(editorBuilder, sapp);
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow sapp) {
        this.currentNote = note;
        String c = sapp.service().elementToString(note.getContent());
        String type = note.getContentType();
        if (type == null) {
            type = "";
        }
        editorBuilder.editor().setContentType(type.isEmpty() ? "text/plain" : type);
        editorBuilder.editor().setText(c == null ? "" : c);
        setEditable(!note.isReadOnly());
        UndoManager um = GuiHelper.getUndoRedoManager(editorBuilder.editor());
        um.discardAllEdits();
    }

    private Action prepareAction(AbstractAction a) {
        //align-justify.png
        String s = (String) a.getValue(AbstractAction.NAME);
        SwingApplicationsUtils.registerAction(a, null, s, sapp.app());
        return a;
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        editorBuilder.editor().setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editorBuilder.editor().isEditable();
    }

    public JEditorPaneBuilder getEditorBuilder() {
        return editorBuilder;
    }

    @Override
    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void moveTo(int pos) {
        editorBuilder.editor().setCaretPosition(pos);
    }

    @Override
    public void removeHighlights(HighlightType hightlightType) {
        editorBuilder.editor().getHighlighter().removeAllHighlights();
    }

    @Override
    public void highlight(int from, int to, HighlightType hightlightType) {
        try {
            Color c = sapp.colorForHighlightType(hightlightType);
            javax.swing.text.DefaultHighlighter.DefaultHighlightPainter highlightPainter
                    = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(c);
            editorBuilder.editor().getHighlighter().addHighlight(from, to, highlightPainter);
        } catch (BadLocationException ex) {
            Logger.getLogger(RichEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
