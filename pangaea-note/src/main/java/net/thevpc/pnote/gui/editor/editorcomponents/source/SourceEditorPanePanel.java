/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.source;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppContextMenu;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.SelectableElement;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.StringQuerySearch;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

import java.util.stream.Stream;

/**
 *
 * @author vpc
 */
public class SourceEditorPanePanel extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private TextArea textArea;
    private PangaeaNoteExt currentNote;
    private boolean compactMode;
    private boolean editable = true;
    private PangaeaNoteFrame frame;
    private SelectableElement selectableElement=new SelectableElement() {
        @Override
        public String getSelectedText() {
            String s = textArea.textSelection().get();
            return (s!=null && s.length()>0)?s:null;
        }
    };

    public SourceEditorPanePanel(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.compactMode = compactMode;
        this.textArea = new TextArea(frame.app());
        textArea.installDefaults();
        textArea.registerAccelerator("search-text", "control F", () -> showSearchDialog());
//        for (PangaeaNoteTypeService contentTypeService : win.service().getContentTypeServices()) {
//            EditorKit k=contentTypeService.getSourceEditorKit();
//            if(k!=null){
//                        editorBuilder.editor().setEditorKitForContentType(
//                                contentTypeService.getContentType().toString()
//                                , k);
//            }
//        }
        textArea.rowNumberRuler().set(true);
        textArea.zoomOnMouseWheel().set(true);
        this.frame = frame;
        this.textArea.text().onChange(e->{
            if (currentNote != null) {
                frame.onDocumentChanged();
                currentNote.setContent(frame.service().stringToElement(textArea.text().get().value()));
            }
        });
        AppContextMenu popup = textArea.contextMenu().getOrCompute(() -> new ContextMenu(app()));
        children().add(new ScrollPane(textArea));
//        GuiHelper.installUndoRedoManager(editorBuilder.editor());
//        children().add(editorBuilder);

        textArea.focused()
                .onChange(e -> {
            if (e.newValue()) {
                frame.selectableElement().set(selectableElement);
            } else {
                if (selectableElement == frame.selectableElement().get()) {
                    frame.selectableElement().set(null);
                }
            }
        });

    }

//    public boolean isSupportedType(String contentType) {
//        return supported != null && supported.contains(contentType);
//    }
    @Override
    public void uninstall() {
//        textExtension.uninstall(editorBuilder, win);
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteFrame win) {
        this.currentNote = note;
        String c = win.service().elementToString(note.getContent());
        String type = note.getContentType();
        if (type == null) {
            type = "";
        }
        textArea.textContentType().set(type.isEmpty() ? "text/plain" : type);
        textArea.text().set(Str.of(win.service().elementToString(note.getContent())));
        setEditable(!note.isReadOnly());
//        UndoManager um = GuiHelper.getUndoRedoManager(editorBuilder.editor());
//        um.discardAllEdits();
    }

    @Override
    public AppComponent component() {
        return this;
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        textArea.editable().set(b);
    }

    @Override
    public boolean isEditable() {
        return textArea.editable().get();
    }

    @Override
    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void moveTo(int pos) {
        textArea.caretPosition().set(pos);
    }

    @Override
    public void removeHighlights(HighlightType highlightType) {
        textArea.removeAllHighlights(highlightType);
    }

    @Override
    public void highlight(int from, int to, HighlightType highlightType) {
        AppColor c = frame.colorForHighlightType(highlightType);
        textArea.highlight(from, to, c, highlightType);
    }

    public void showSearchDialog(){
        SearchDialog dialog = new SearchDialog(frame);
        dialog.setTitle(Str.i18n("Message.search.searchInDocument"));
        dialog.setSearchText(textArea.text().get().value());
        SearchQuery query = dialog.showDialog();
        if (query != null) {
            StringQuerySearch<String> fi = new StringQuerySearch(query);
            String txt = textArea.getText(0, textArea.getTextLength());
            Stream<StringSearchResult<String>> found = fi.search(StringDocumentTextNavigator.of(txt), SearchProgressMonitor.NONE);
            found.forEach(x -> {
                int from = x.getStart();
                int to = x.getEnd();
                highlight(from, to, HighlightType.SEARCH_MAIN);
            });
        }
    }
}
