package Secure;

import android.util.Base64;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {

    public static String encrypt(final String plainMessage, String symKeyHex) {
        symKeyHex = asciiToHex(symKeyHex);
        final byte[] symKeyData = Base64.decode(symKeyHex,Base64.DEFAULT);
        final byte[] encodedMessage = plainMessage.getBytes(Charset.forName("UTF-8"));
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();
            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");
            // generate random IV using block size (possibly create a method for
            // this)
            final byte[] ivData = new byte[blockSize];
            final SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.nextBytes(ivData);
            final IvParameterSpec iv = new IvParameterSpec(ivData);
            cipher.init(Cipher.ENCRYPT_MODE, symKey, iv);
            final byte[] encryptedMessage = cipher.doFinal(encodedMessage);
            // concatenate IV and encrypted message
            final byte[] ivAndEncryptedMessage = new byte[ivData.length + encryptedMessage.length];
            System.arraycopy(ivData, 0, ivAndEncryptedMessage, 0, blockSize);
            System.arraycopy(encryptedMessage, 0, ivAndEncryptedMessage, blockSize, encryptedMessage.length);
            final String ivAndEncryptedMessageBase64 = Base64.encodeToString(ivAndEncryptedMessage,Base64.DEFAULT);
            return ivAndEncryptedMessageBase64;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("key argument does not contain a valid AES key");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unexpected exception during encryption", e);
        }
    }

    public static String decrypt(final String ivAndEncryptedMessageBase64, String symKeyHex) {
        symKeyHex = asciiToHex(symKeyHex);
        final byte[] symKeyData = Base64.decode((symKeyHex),Base64.DEFAULT);
        final byte[] ivAndEncryptedMessage = Base64.decode(ivAndEncryptedMessageBase64,Base64.DEFAULT);
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final int blockSize = cipher.getBlockSize();
            // create the key
            final SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");
            // retrieve random IV from start of the received message
            final byte[] ivData = new byte[blockSize];
            System.arraycopy(ivAndEncryptedMessage, 0, ivData, 0, blockSize);
            final IvParameterSpec iv = new IvParameterSpec(ivData);
            // retrieve the encrypted message itself
            final byte[] encryptedMessage = new byte[ivAndEncryptedMessage.length - blockSize];
            System.arraycopy(ivAndEncryptedMessage, blockSize, encryptedMessage, 0, encryptedMessage.length);
            cipher.init(Cipher.DECRYPT_MODE, symKey, iv);
            final byte[] encodedMessage = cipher.doFinal(encryptedMessage);
            // concatenate IV and encrypted message
            final String message = new String(encodedMessage, Charset.forName("UTF-8"));
            return message;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("key argument does not contain a valid AES key");
        } catch (BadPaddingException e) {
            // you'd better know about padding oracle attacks
            return null;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unexpected exception during decryption", e);
        }
    }

    private static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    private static String hexToASCII(String hexValue) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2) {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}