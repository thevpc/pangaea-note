/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.security;

import net.thevpc.nuts.NutsApplicationContext;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author thevpc
 */
public class PangaeaNoteCypher_v101 extends PangaeaNoteCypherBase {

    public static final String ID = "v1.0.1";

    public PangaeaNoteCypher_v101(NutsApplicationContext context) {
        super(ID, context);
    }

    private static KeyInfo createKeyInfo(String password) {
        if (password == null || password.length() == 0) {
            password = "password";
        }
        MessageDigest sha = null;
        KeyInfo k = new KeyInfo();
        try {
            k.key = password.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            k.key = sha.digest(k.key);
            k.secretKey = new SecretKeySpec(k.key, "AES");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        return k;
    }

    protected String encryptString(String strToEncrypt, String secret) {
        try {
            //strToEncrypt must be multiple of 16 (bug in jdk11)
            byte[] bytes = strToEncrypt.getBytes(StandardCharsets.UTF_8);
            int v = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write((v >>> 24) & 0xFF);
            out.write((v >>> 16) & 0xFF);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
            out.write(bytes);
            int s = v + 4;
            while (s % 16 != 0) {
                out.write(0);
                s++;
            }
            bytes = out.toByteArray();

            KeyInfo k = createKeyInfo(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, k.secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(bytes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String decryptString(String strToDecrypt, String secret) {
        try {
            KeyInfo k = createKeyInfo(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, k.secretKey);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));

            //bytes is padded to be multiple of 16 (bug in jdk11)
            if (bytes.length < 4) {
                throw new EOFException();
            }
            int ch1 = bytes[0] & 0xff;
            int ch2 = bytes[1] & 0xff;
            int ch3 = bytes[2] & 0xff;
            int ch4 = bytes[3] & 0xff;
            if ((ch1 | ch2 | ch3 | ch4) < 0) {
                throw new EOFException();
            }
            int v = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
            bytes = Arrays.copyOfRange(bytes, 4, 4 + v);
            return new String(bytes);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class KeyInfo {

        SecretKeySpec secretKey;
        byte[] key;
    }

}
