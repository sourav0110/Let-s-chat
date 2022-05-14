package com.example.letschat;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.Key;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CipherTextConverter
{
    private static final String ALGO = "AES";
    private String keyValue;
    public CipherTextConverter(String key)
    {
        keyValue=key;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt (String data) throws Exception
    {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(keyValue.toCharArray(),"salt".getBytes(),100, 128);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(data.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String encryptedData) throws Exception
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(keyValue.toCharArray(), "salt".getBytes(),100, 128);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(encryptedData));
        return new String(plainText);
    }


}
