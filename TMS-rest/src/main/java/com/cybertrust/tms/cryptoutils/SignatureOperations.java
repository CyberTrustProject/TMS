package com.cybertrust.tms.cryptoutils;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import java.util.Base64;

public class SignatureOperations {

	public static String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
	    Signature sign = Signature.getInstance("SHA256withRSA");
	    sign.initSign(privateKey);
	    sign.update(message.getBytes(StandardCharsets.UTF_8));
	    return new String(Base64.getEncoder().encode(sign.sign()), StandardCharsets.UTF_8);
	}	
	
	public static boolean verify(PublicKey publicKey, String message, String signature, String signatureAlgorithm) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
	    Signature sign = Signature.getInstance(signatureAlgorithm);
	    sign.initVerify(publicKey);
	    sign.update(message.getBytes(StandardCharsets.UTF_8));
	    return sign.verify(Base64.getDecoder().decode(signature.getBytes(StandardCharsets.UTF_8)));
	}
}
