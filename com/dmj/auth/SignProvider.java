package com.dmj.auth;

import com.dmj.auth.util.Base64;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import org.apache.log4j.Logger;

/* loaded from: SignProvider.class */
public class SignProvider {
    Logger logger = Logger.getLogger(getClass());

    public static boolean verify(byte[] pubKeyText, String plainText, byte[] signText) {
        try {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decode(pubKeyText));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
            byte[] signed = Base64.decode(signText);
            Signature signatureChecker = Signature.getInstance("MD5withRSA");
            signatureChecker.initVerify(pubKey);
            signatureChecker.update(plainText.getBytes());
            if (signatureChecker.verify(signed)) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mian() {
    }
}
