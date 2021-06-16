package net.thevpc.pnote.service.security;

import net.thevpc.echo.api.CancelException;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.nuts.NutsContentType;
import net.thevpc.pnote.api.InvalidSecretException;
import net.thevpc.pnote.api.PangaeaNoteCypher;
import net.thevpc.pnote.api.model.CypherInfo;
import net.thevpc.pnote.api.model.PangaeaNote;

import java.util.function.Supplier;

public abstract class PangaeaNoteCypherBase implements PangaeaNoteCypher {
    protected NutsApplicationContext context;
    private String id;

    public PangaeaNoteCypherBase(String id, NutsApplicationContext context) {
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
        String s = context.getWorkspace().elem()
                .setContentType(NutsContentType.JSON)
                .setValue(a)
                .setCompact(true)
                .setSession(context.getSession())
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
        return context.getWorkspace().elem()
                .setContentType(NutsContentType.JSON)
                .setValue(password)
                .setCompact(true)
                .setSession(context.getSession())
                .parse(s, PangaeaNote.class);
    }

    protected abstract String encryptString(String strToEncrypt, String secret);

    protected abstract String decryptString(String strToDecrypt, String secret);
}
