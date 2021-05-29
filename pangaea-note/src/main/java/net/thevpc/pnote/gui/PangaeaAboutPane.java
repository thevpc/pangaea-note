package net.thevpc.pnote.gui;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.echo.impl.Applications;

public class PangaeaAboutPane extends TabPane {
    public PangaeaAboutPane(PangaeaNoteApp app) {
        super(app);
        children().addAll(
                new ScrollPane(
                        new WebView(app)
                                .with((WebView t) -> {
                                    t.text().set(
                                            Str.of(
                                                    Applications.loadStreamAsString(
                                                            PangaeaAboutPane.class.getResource("/net/thevpc/pnote/about/about.html")
                                                    )
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.About"));
                }),
                new ScrollPane(
                        new GridPane(2,app)
                                .with((GridPane t) -> {
                                    t.parentConstraints().addAll(AllMargins.of(2),
                                            AllFill.HORIZONTAL, AllAnchors.LEFT,
                                            AllGrow.NONE,
                                            GrowContainer.BOTH);
                                    t.children().addAll(
                                            new Label(Str.of("architecture"),app),
                                            new TextField(Str.of(app.getNutsWorkspace().env().getArch().toString()),app)
                                                .with(tt-> {
                                                    tt.childConstraints().add(Grow.HORIZONTAL);
                                                    tt.editable().set(false);
                                                }),
                                            new Label(Str.of("os"),app),
                                            new TextField(Str.of(app.getNutsWorkspace().env().getOs().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.of("os distribution"),app),
                                            new TextField(Str.of(app.getNutsWorkspace().env().getOsDist().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.of("platform"),app),
                                            new TextField(Str.of(app.getNutsWorkspace().env().getPlatform().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.of("nuts api version"),app),
                                            new TextField(Str.of(app.getNutsWorkspace().getApiVersion().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.of("nuts implementation"),app),
                                            new TextField(Str.of(app.getNutsWorkspace().getRuntimeId().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.of("Pangaea Note config file"),app),
                                            new TextField(Str.of(app.getConfigFilePath().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.of("Rendering Toolkit"),app),
                                            new TextField(Str.of(app.toolkit().id().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    })
                                    );
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Environment"));
                }),
                new ScrollPane(
                        new WebView(app)
                                .with((WebView t) -> {
                                    t.text().set(
                                            Str.of(
                                                    Applications.loadStreamAsString(
                                                            PangaeaAboutPane.class.getResource("/net/thevpc/pnote/about/dependencies.html")
                                                    )
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Dependencies"));
                }),
                new ScrollPane(
                        new WebView(app)
                                .with((WebView t) -> {
                                    t.text().set(
                                            Str.of(
                                                    Applications.loadStreamAsString(
                                                            PangaeaAboutPane.class.getResource("/net/thevpc/pnote/about/licenses.html")
                                                    )
                                            )
                                    );
                                    t.editable().set(false);
                                })
                ).with((AppComponent c) -> {
                    c.title().set(Str.i18n("About.Licenses"));
                }),
                new ScrollPane(
                        new WebView(app)
                                .with((WebView t) -> {
                                    t.text().set(
                                            Str.of(
                                                    Applications.loadStreamAsString(
                                                            PangaeaAboutPane.class.getResource("/net/thevpc/pnote/about/contribute.html")
                                                    )
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
