package com.marktruong.reddit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marktruong.reddit.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

	Optional<RefreshToken> findByToken(String token);

	void deleteByToken(String token);
	
}
