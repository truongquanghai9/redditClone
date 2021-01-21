package com.marktruong.reddit.security;



import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.marktruong.reddit.exception.SpringRedditException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtProvider {
	
	private KeyStore keyStore;
	
	@Value("${jwt.expiration.time}")
	private Long jwtExpirationTimeInMillis;
	
	@PostConstruct
	public void init() {
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
			keyStore.load(resourceAsStream,"secret".toCharArray());
		} catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ex) {
			throw new SpringRedditException("Exception occured while loading keystore", ex);
		}
	}
	
	public String generateToken(Authentication authentication) {
		User principal = (User) authentication.getPrincipal();
		log.info(">>>>>>principal.getUsername() " + principal.getUsername());
		log.info(">>>>>>Instant.now() " + Instant.now());
		log.info(">>>> Date.from(Instant.now()) " + Date.from(Instant.now()));
		return Jwts.builder().setSubject(principal.getUsername())
							.setIssuedAt(Date.from(Instant.now()))
							.signWith(getPrivateKey())
							.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTimeInMillis)))
							.compact();
	
	}
	
	public String generateTokenWithUsername(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(Date.from(Instant.now()))
				.signWith(getPrivateKey())
				.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTimeInMillis)))
				.compact();
	}
	
	private PrivateKey getPrivateKey() {
		try {
			return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new SpringRedditException("Exception occured while retrieving public key from keystore" , e);
		}
	}
	
	public boolean validateToken(String jwt) {
		Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(jwt);
		return true;
	}
	private PublicKey getPublicKey() {
		try {
			return keyStore.getCertificate("springblog").getPublicKey();
		} catch (KeyStoreException e) {
			throw new SpringRedditException("Exception occured while retrieving"
					+ " public key from keystore" + e);
		}
	}
	public String getUsernameFromJwt(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
		log.info(">>>>>>>>>claims.getSubject() : " + claims.getSubject());
		return claims.getSubject();
	}
	public Long getJwtExpirationTimeInMillis() {
		return jwtExpirationTimeInMillis;
	}
}
  