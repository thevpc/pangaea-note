/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.security;

import net.thevpc.nuts.NSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteCypher_v100 extends PangaeaNoteCypherBase {
    public static final String ID="v1.0.0";
    public PangaeaNoteCypher_v100(NSession session) {
        super(ID,session);
    }


    private class KeyInfo {

        SecretKeySpec secretKey;
        byte[] key;
    }

    private KeyInfo createKeyInfo(String password) {
        if (password == null || password.length() == 0) {
            password = "password";
        }
        MessageDigest sha = null;
        KeyInfo k = new KeyInfo();
        try {
            k.key = password.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            k.key = sha.digest(k.key);
            k.key = Arrays.copyOf(k.key, 16);
            k.secretKey = new SecretKeySpec(k.key, "AES");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        return k;
    }

    protected String encryptString(String strToEncrypt, String secret) {
        try {
            KeyInfo k = createKeyInfo(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, k.secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String decryptString(String strToDecrypt, String secret) {
        try {
            KeyInfo k = createKeyInfo(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, k.secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
