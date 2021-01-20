package com.marktruong.reddit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marktruong.reddit.dto.PostRequest;
import com.marktruong.reddit.dto.PostResponse;
import com.marktruong.reddit.exception.PostNotFoundException;
import com.marktruong.reddit.exception.SubredditNotFoundException;
import com.marktruong.reddit.mapper.PostMapper;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.Subreddit;
import com.marktruong.reddit.model.User;
import com.marktruong.reddit.repository.PostRepository;
import com.marktruong.reddit.repository.SubredditRepository;
import com.marktruong.reddit.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {
	
	private final PostRepository postRepository;
	private final SubredditRepository subredditRepository;
	private final AuthService authService;
	private final PostMapper postMapper;
	private final UserRepository userRepository;
	
	
	public void save(PostRequest postRequest) {
		Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
								.orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
		Post post = postMapper.mapPostRequestToPost(postRequest, subreddit, authService.getCurrentUser());
		subreddit.getPosts().add(post);
		postRepository.save(post);
	}
	@Transactional(readOnly=true)
	public PostResponse getPost(Long id) {
		return postMapper.mapPostToPostResponse(postRepository.findById(id)
					.orElseThrow(() -> new PostNotFoundException(id.toString())));
	}
	
	@Transactional(readOnly=true)
	public List<PostResponse> getAllPosts() {
		return postRepository.findAll().stream()
				.map(postMapper::mapPostToPostResponse)
				.collect(Collectors.toList());
	}
	@Transactional(readOnly=true)
	public List<PostResponse> getPostsBySubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id)
				.orElseThrow(() -> new SubredditNotFoundException(id.toString()));
		List<Post> posts = postRepository.findAllBySubreddit(subreddit);
		return posts.stream().map(postMapper::mapPostToPostResponse).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public List<PostResponse> getPostsByUsername(String username) {
		User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException(username));
		return postRepository.findByUser(user)
					.stream().map(postMapper::mapPostToPostResponse)
					.collect(Collectors.toList());
		
	}
}
