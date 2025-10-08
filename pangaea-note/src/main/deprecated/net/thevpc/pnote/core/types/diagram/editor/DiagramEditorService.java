//package net.thevpc.pnote.core.types.diagram.editor;
//
//import net.thevpc.pnote.api.PangaeaNoteEditorService;
//import net.thevpc.pnote.core.types.diagram.PangaeaNoteDiaService;
//import net.thevpc.pnote.core.frame.PangaeaNoteApp;
//import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
//import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
//
//public class DiagramEditorService implements PangaeaNoteEditorService {
//    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
//        switch (name) {
//            case PangaeaNoteDiaService.DIAGRAM_EDITOR:
//                return new PangaeaNoteDiagramEditorTypeComponent(compactMode, win);
//        }
//        return null;
//    }
//
//    @Override
//    public void onInstall(PangaeaNoteApp app) {
//
//    }
//}
