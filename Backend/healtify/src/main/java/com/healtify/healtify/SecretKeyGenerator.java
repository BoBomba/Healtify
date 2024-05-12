package com.healtify.healtify;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // for example
        KeyPair keyPair = keyGen.generateKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey);
        String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey);
        System.out.println("Enkodowany klucz publiczny: "+encodedPublicKey);
        System.out.println("Enkodowany klucz prywatny: "+encodedPrivateKey);
    }
}