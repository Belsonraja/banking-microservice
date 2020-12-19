package com.oracle.payments.accounts.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordUtil {
	
	private PasswordUtil() {
	}
	
	public static String generateHash(String input) throws NoSuchAlgorithmException {
		log.debug("inside generateHash - input : {}", input);
		StringBuilder hash = new StringBuilder();
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] hashedBytes = sha.digest(input.getBytes());
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		for (int idx = 0; idx < hashedBytes.length; idx++) {
			byte b = hashedBytes[idx];
			hash.append(digits[(b & 0xf0) >> 4]);
			hash.append(digits[b & 0x0f]);
		}
		log.debug("generatedHash - output : {}", hash.toString());
		return hash.toString();
	}
}
