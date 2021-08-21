package net.thevpc.pnote.core.frame.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableString;
import net.thevpc.common.props.WritableValue;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppAlertInputPane;
import net.thevpc.echo.api.AppAlertResult;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.echo.iconset.IconConfig;
import net.thevpc.echo.iconset.IconSets;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

public class PangaeaNoteIconsButton extends Button {
    PangaeaNoteFrame frame;
    private final WritableString iconIdValue = Props.of("iconIdValue").stringOf( null);

    public PangaeaNoteIconsButton(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        icon().set(getIcon(""));
        action().set(() -> {
            AppAlertResult r = new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("PangaeaNoteIconsButton.title"));
                        a.headerText().set(Str.i18n("PangaeaNoteIconsButton.header"));
                        a.headerIcon().set(Str.of("image"));
                        a.content().set(
                                new PangaeaNoteIconsListAndPreview(frame)
                        );
                    }).withOkCancelButtons().showDialog();
            switch (r.button()){
                case "ok":{
                    iconIdValue().set(r.value());
                    break;
                }
            }
        });
        iconIdValue().onChange(
                ()->icon().set(getIcon(iconIdValue().get()))
        );
        propagateEvents(iconIdValue);
    }

    public WritableString iconIdValue() {
        return iconIdValue;
    }

    public AppImage getIcon(String icon) {
        IconSets iconSets = frame.app().iconSets();
        IconConfig c = iconSets.config().get();
        return ((icon == null || icon.length() == 0) ?
                new Image(c.getWidth(), c.getHeight(), null, app())
                : iconSets.icon(icon,this) == null ? null :
                iconSets.icon(icon,this)
        );
    }
    public static class PangaeaNoteIconsListAndPreview extends BorderPane implements AppAlertInputPane {
        private PangaeaNoteIconsList list;
        private ImageView imageView;
        public PangaeaNoteIconsListAndPreview(PangaeaNoteFrame frame) {
            super(frame.app());
            children().add(
                    list=new PangaeaNoteIconsList(frame)
                    .with(p->{
                        p.anchor().set(Anchor.CENTER);
                    })
            );
            list.prefSize().set(new Dimension(400,400));
            children().add(
                    imageView=new ImageView(app())
                            .with(p->{
                                p.anchor().set(Anchor.RIGHT);
                            })
            );
            imageView.prefSize().set(new Dimension(120,120));

            list.selection().onChange(new Runnable() {
                @Override
                public void run() {
                    imageView.image().set(
                            list.getSelectedIcon().scaleTo(120,120)
                    );
                }
            });
            imageView.image().set(
                    list.getSelectedIcon().scaleTo(120,120)
            );
        }

        @Override
        public Object getValue() {
            return list.getSelectedIconId();
        }
    }
}
