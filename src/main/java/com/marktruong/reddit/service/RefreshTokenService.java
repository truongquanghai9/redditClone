package com.marktruong.reddit.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marktruong.reddit.exception.SpringRedditException;
import com.marktruong.reddit.model.RefreshToken;
import com.marktruong.reddit.repository.RefreshTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	public RefreshToken generateRefreshToken() {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setCreatedDate(Instant.now());
		
		return refreshTokenRepository.save(refreshToken);
	}
	
	public void validateRefreshToken(String token) {
		refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new SpringRedditException("Invalid refresh token"));
	}
	public void deleteRefreshToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}
}
