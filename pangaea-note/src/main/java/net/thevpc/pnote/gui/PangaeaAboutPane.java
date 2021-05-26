package net.thevpc.pnote.gui;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.ScrollPane;
import net.thevpc.echo.TabPane;
import net.thevpc.echo.TextArea;
import net.thevpc.echo.api.components.AppComponent;

public class PangaeaAboutPane extends TabPane {
    public PangaeaAboutPane(PangaeaNoteApp app) {
        super(app);
        children().addAll(
                new ScrollPane(
                        new TextArea(app)
                                .with((TextArea t) -> {
                                    t.text().set(
                                            Str.of(
                                                    "Pangaea Note is a multi purpose Note Taking Application that you can use to store your ideas, passwords, code snippets, cloud accounts and much more.\n"+
                                                            "Pangaea Note organizes your notes in a tree that you can customize to include :\n"+
                                                            "+ simple text\n"+
                                                            "+ rich text (WYSIWYG)\n"+
                                                            "+ watch an external folder\n"+
                                                            "+ watch an external file (pdf,text, image, excel, ...)\n"+
                                                            "+ source code snippet with syntax highlighting\n"+
                                                            "+ markdown\n"+
                                                            "+ 2D diagram\n"+
                                                            "+ web urls / web accounts\n"+
                                                            "+ custom forms\n"+
                                                            "+ ...\n"+
                                                            "\n"+
                                                            "You can also group your notes in:\n"+
                                                            "+ Tree (Hierarchy)\n"+
                                                            "+ List (vertical/horizontal split)\n"+
                                                            "+ Tabs \n"+
                                                            "\n"+
                                                            "and of course you can protect your note contents with a password.\n"+
                                                            ""
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.About"));
                }),
                new ScrollPane(
                        new TextArea(app)
                                .with((TextArea t) -> {
                                    t.text().set(
                                            Str.of(
                                                            "architecture: " + app.getNutsWorkspace().env().getArch() + "\n" +
                                                            "os: " + app.getNutsWorkspace().env().getOs() + "\n" +
                                                            "os distribution: " + app.getNutsWorkspace().env().getOsDist() + "\n" +
                                                            "platform: " + app.getNutsWorkspace().env().getPlatform() + "\n" +
                                                            "nuts version: " + app.getNutsWorkspace().getApiVersion() + " (" + app.getNutsWorkspace().getRuntimeId() + ")" + "\n" +
                                                            "config file: " + app.service().getConfigFilePath() + "\n" +
                                                            "toolkit : " + app.toolkit().id() + "\n"
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Environment"));
                }),
                new ScrollPane(
                        new TextArea(app)
                                .with((TextArea t) -> {
                                    t.text().set(
                                            Str.of(
                                                    "Pangaea Note uses other Open source software including :\n" +
                                                            "\n" +
                                                            "* nuts          (Package manager and App Framework) https://github.com/thevpc/nuts\n" +
                                                            "* echo          (Swing/JavaFX application Framework) https://github.com/thevpc/echo\n" +
                                                            "* jeep          (Source code Parser and syntax highlighter Framework) https://github.com/thevpc/jeep\n" +
                                                            "* diagram4j     (Simple Diagram editor Framework) https://github.com/thevpc/diagram4j\n" +
                                                            "* shef          (Simple HTML Editor Framework) https://github.com/thevpc/shef\n" +
                                                            "* svgSalamander (SVG Library) https://github.com/blackears/svgSalamander\n" +
                                                            "* jtidy         (HTML parser) https://github.com/jtidy/jtidy\n" +
//                                                            "* jOpenDocument (Open Document Viewer)\n" +
                                                            "* jodconverter  (Document Conversion) https://github.com/mirkonasato/jodconverter\n" +
                                                            "* icepdf        (Pdf Viewer) https://github.com/pcorless/icepdf\n" +
                                                            "* tess4j        (OCR library) https://github.com/nguyenq/tess4j\n" +
                                                            "* FlatLaf       (Look and Feel) https://github.com/JFormDesigner/FlatLaf\n" +
                                                            "* feather       (icon sets) https://github.com/feathericons/feather\n" +
                                                            "* svgrepo       (icon sets) https://www.svgrepo.com\n"
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Dependencies"));
                }),
                new ScrollPane(
                        new TextArea(app)
                                .with((TextArea t) -> {
                                    t.text().set(
                                            Str.of(
                                                    "Copyright 2021 vpc community\n" +
                                                            "\n" +
                                                            "Pangaea Note is Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                                                            "you may not use this file except in compliance with the License.\n" +
                                                            "You may obtain a copy of the License at\n" +
                                                            "\n" +
                                                            "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                                                            "\n" +
                                                            "Unless required by applicable law or agreed to in writing, software\n" +
                                                            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                                                            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                                                            "See the License for the specific language governing permissions and\n" +
                                                            "limitations under the License."
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Licenses"));
                }),
                new ScrollPane(
                        new TextArea(app)
                                .with((TextArea t) -> {
                                    t.text().set(
                                            Str.of(
                                                    "Software can always be improved, and you - the user - must tell us when something does not work as expected or could be done better.\n" +
                                                            "\n" +
                                                            "Pangaea Note has a bug tracking system. Visit https://github.com/thevpc/pangaea-note issues tab.\n" +
                                                            "\n" +
                                                            "If you have a suggestion for improvement then you are more than welcome to use the bug tracking system to register your wish."
                                                            + "You are more than welcome if you want to contribute to Pangaea Note. You just need to contact us at our github repository.\n"
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Contribute"));
                })
        );
    }
}
