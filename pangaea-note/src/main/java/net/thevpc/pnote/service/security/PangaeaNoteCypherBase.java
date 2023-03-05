package net.thevpc.pnote.service.security;

import net.thevpc.echo.api.CancelException;
import net.thevpc.nuts.NApplicationContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.pnote.api.InvalidSecretException;
import net.thevpc.pnote.api.PangaeaNoteCypher;
import net.thevpc.pnote.api.model.CypherInfo;
import net.thevpc.pnote.api.model.PangaeaNote;

import java.util.function.Supplier;

public abstract class PangaeaNoteCypherBase implements PangaeaNoteCypher {
    protected NApplicationContext context;
    private String id;

    public PangaeaNoteCypherBase(String id, NApplicationContext context) {
        this.id = id;
        this.context = context;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CypherInfo encrypt(PangaeaNote a, Supplier<String> passwordSupplier) {
        String password = passwordSupplier.get();
        if (password == null || password.length() == 0) {
            throw new CancelException();
        }
        NSession session = context.getSession();
        String s = NElements.of(session)
                .json()
                .setValue(a)
                .setCompact(true)
                .setNtf(false)
                .format().filteredText();
        return new CypherInfo(getId(),
                encryptString(s, password)
        );
    }

    @Override
    public PangaeaNote decrypt(CypherInfo cypherInfo, PangaeaNote original, Supplier<String> passwordSupplier) {
        if (!getId().equals(cypherInfo.getAlgo())) {
            throw new IllegalArgumentException("unsupported algo: " + cypherInfo.getAlgo());
        }
        String password = passwordSupplier.get();
        if (password == null || password.length() == 0) {
            throw new CancelException();
        }
        String s = null;
        try {
            s = decryptString(cypherInfo.getValue(), password);
        } catch (Exception ex) {
            throw new InvalidSecretException();
        }
        NSession session = context.getSession();
        return NElements.of(session)
                .json()
                .setValue(password)
                .setCompact(true)
                .parse(s, PangaeaNote.class);
    }

    protected abstract String encryptString(String strToEncrypt, String secret);

    protected abstract String decryptString(String strToDecrypt, String secret);
}
