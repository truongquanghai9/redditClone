package com.marktruong.reddit.service;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.marktruong.reddit.dto.AuthenticationResponse;
import com.marktruong.reddit.dto.LoginRequest;
import com.marktruong.reddit.dto.RefreshTokenRequest;
import com.marktruong.reddit.dto.RegisterRequest;
import com.marktruong.reddit.exception.SpringRedditException;
import com.marktruong.reddit.model.NotificationEmail;
import com.marktruong.reddit.model.User;
import com.marktruong.reddit.model.VerificationToken;
import com.marktruong.reddit.repository.UserRepository;
import com.marktruong.reddit.repository.VerificationTokenRepository;
import com.marktruong.reddit.security.JwtProvider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Main business logic to register a user

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthService {
	
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;
	
	public boolean signup(RegisterRequest registerRequest) {
		
		if ((userRepository.findByUsername(registerRequest.getUsername())).isPresent()) {
			return false;
		}

		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEmail(registerRequest.getEmail());
		user.setCreated(Instant.now());
		user.setEnabled(false);
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
		mailService.sendMail(new NotificationEmail("Please Activate your account", 
						user.getEmail(), "Thank you for signing up to Spring Reddit Clone " +
						"please click on the below url to activate your account : " +
						"http://localhost:8080/api/auth/accountVerification/" + token));
		
		return true;
		
	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);
		return token;
	}

	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
		verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
		fetchUserAndEnable(verificationToken.get());
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(() ->
														new SpringRedditException("Username not found " + username));
		user.setEnabled(true);
		userRepository.save(user);
	}

	public AuthenticationResponse login(LoginRequest loginRequest) {		
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String token = jwtProvider.generateToken(authenticate);
		return AuthenticationResponse.builder().authenticationToken(token)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())
				.expiresAt((Instant.now().plusMillis(jwtProvider.getJwtExpirationTimeInMillis())).toEpochMilli())
				.username(loginRequest.getUsername())
				.build();
	}	


	@Transactional(readOnly=true)
	public User getCurrentUser() {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		//org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		String username = loggedInUser.getName();
		 
		return userRepository.findByUsername(username)
							.orElseThrow(() -> new UsernameNotFoundException("Username not found - " + username));
	}

	public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		String token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
		return AuthenticationResponse.builder()
				.authenticationToken(token)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.username(refreshTokenRequest.getUsername())
				.expiresAt((Instant.now().plusMillis(jwtProvider.getJwtExpirationTimeInMillis())).toEpochMilli())
				.build();
	}
	public boolean isLoggedIn() {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		
		return !(loggedInUser instanceof AnonymousAuthenticationToken) && loggedInUser.isAuthenticated();
	}
}
