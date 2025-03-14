package net.thevpc.pnote.core.frame;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.echo.impl.Applications;
import net.thevpc.nuts.NApp;
import net.thevpc.nuts.NWorkspace;

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
                                            ContainerGrow.ALL);
                                    t.children().addAll(
                                            new Label(Str.i18n("About.pangaeaNoteVersionLabel"),app),
                                            new TextField(Str.of(NApp.of().getId().get().getVersion().toString()),app)
                                                .with(tt-> {
                                                    tt.childConstraints().add(Grow.HORIZONTAL);
                                                    tt.editable().set(false);
                                                }),
                                            new Label(Str.i18n("About.architecture"),app),
                                            new TextField(Str.of(NWorkspace.of().getArch().toString()),app)
                                                .with(tt-> {
                                                    tt.childConstraints().add(Grow.HORIZONTAL);
                                                    tt.editable().set(false);
                                                }),
                                            new Label(Str.i18n("About.os"),app),
                                            new TextField(Str.of(NWorkspace.of().getOs().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.i18n("About.osDistribution"),app),
                                            new TextField(Str.of(NWorkspace.of().getOsDist().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.i18n("About.platform"),app),
                                            new TextField(Str.of(NWorkspace.of().getPlatform().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.i18n("About.nutsApiVersion"),app),
                                            new TextField(Str.of(NWorkspace.of().getApiVersion().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.i18n("About.nutsImplementation"),app),
                                            new TextField(Str.of(NWorkspace.of().getRuntimeId().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.i18n("About.renderingTToolkit"),app),
                                            new TextField(Str.of(app.toolkit().id().toString()),app)
                                                    .with(tt-> {
                                                        tt.childConstraints().add(Grow.HORIZONTAL);
                                                        tt.editable().set(false);
                                                    }),
                                            new Label(Str.i18n("About.pangaeaNoteConfigFile"),app),
                                            new TextField(Str.of(app.getConfigFilePath().toString()),app)
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
