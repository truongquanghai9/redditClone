package com.marktruong.reddit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marktruong.reddit.dto.CommentDto;
import com.marktruong.reddit.exception.PostNotFoundException;
import com.marktruong.reddit.mapper.CommentMapper;
import com.marktruong.reddit.model.Comment;
import com.marktruong.reddit.model.NotificationEmail;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.User;
import com.marktruong.reddit.repository.CommentRepository;
import com.marktruong.reddit.repository.PostRepository;
import com.marktruong.reddit.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class CommentService {
	
	
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final PostRepository postRepository;
	private final CommentMapper commentMapper;
	private final MailContentBuilder mailContentBuilder;
	private final MailService mailService;
	
	public void save(CommentDto commentDto) {
		User user = authService.getCurrentUser();
		Post post = postRepository.findById(commentDto.getPostId())
					.orElseThrow(() -> new PostNotFoundException("Post id not found " + commentDto.getPostId()));
		Comment comment = commentMapper.mapCommentDtoToComment(commentDto, post, user);
		comment.setUser(user);
		comment.setPost(post);
		commentRepository.save(comment);
		String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + post.getUrl());
		
		sendCommentNotification(message, post.getUser());
		
	}
	private void sendCommentNotification (String message, User user) {
		mailService.sendMail(new NotificationEmail(user.getUsername() + " commented on your post",
											user.getEmail(),
											message));
	}
	public List<CommentDto> getAllCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId)
								.orElseThrow(() -> new PostNotFoundException("Post id not found " + postId));
		return commentRepository.findByPost(post).stream()
							.map(commentMapper::mapCommentToCommentDto)
							.collect(Collectors.toList());
	}
	public List<CommentDto> getAllCommentsForUser(String username) {
		User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("Username not found " + username));
		return commentRepository.findByUser(user).stream()
								.map(commentMapper::mapCommentToCommentDto)
								.collect(Collectors.toList());
		
	}
}
