package com.marktruong.reddit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marktruong.reddit.dto.VoteDto;
import com.marktruong.reddit.exception.PostNotFoundException;
import com.marktruong.reddit.exception.SpringRedditException;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.Vote;
import com.marktruong.reddit.model.VoteType;
import com.marktruong.reddit.repository.PostRepository;
import com.marktruong.reddit.repository.VoteRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {
	
	private final VoteRepository voteRepository;
	private final PostRepository postRepository;
	private final AuthService authService;
	
	@Transactional
	public void vote(VoteDto voteDto) {
		Post post = postRepository.findById(voteDto.getPostId())
						.orElseThrow(() -> new PostNotFoundException("Post id not found " + voteDto.getPostId()));
		
		Optional<Vote> voteByPostAndUser = 
				voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
		
		if (voteByPostAndUser.isPresent()) {
			if (voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
				checkVoteTypeAndAddToPostForSameVoteType(voteDto, post);
				postRepository.save(post);
				voteRepository.delete(voteByPostAndUser.get());
				return;
			}
			else {
				checkVoteTypeAndAddToPostForDifferentVoteType(voteDto,post);
			}
		} else {
			checkVoteTypeAndAddToPostForNotPresent(voteDto,post);
		}
		/*
		if (voteByPostAndUser.isPresent()) {
			if (voteByPostAndUser.get().size() == 0) {
				checkVoteTypeAndAddToPost(voteDto,post);
				voteRepository.save(mapToVote(voteDto,post));
				return;
			}
			else if (voteByPostAndUser.get().size() == 1) {
				if (voteByPostAndUser.get().get(0).getVoteType().equals(voteDto.getVoteType())) {
					throw new SpringRedditException("You have already " + voteDto.getVoteType() + "D for this post");
				}
				checkVoteTypeAndAddToPost(voteDto,post);
				voteRepository.save(mapToVote(voteDto,post));
				return;
			}
		}
		
		if(voteByPostAndUser.isPresent() 
				&& voteByPostAndUser.get().get(0).getVoteType().equals(voteDto.getVoteType()) 
				&& voteByPostAndUser.get().get(1).getVoteType().equals(voteDto.getVoteType())) {
			throw new SpringRedditException("You have already " + voteDto.getVoteType() + "D for this post");
		}
		else {
			if (voteByPostAndUser.get().get(1).getVoteType().equals(voteDto.getVoteType())) {
				voteRepository.save(mapToVote(voteDto,post));
				voteRepository.delete(voteByPostAndUser.get().get(0));
			}
			checkVoteTypeAndAddToPost(voteDto,post);
		}
		voteRepository.save(mapToVote(voteDto,post));
		voteRepository.delete(voteByPostAndUser.get().get(1));
		*/
		voteRepository.save(mapToVote(voteDto,post));
		if (voteByPostAndUser.isPresent()) {
			voteRepository.delete(voteByPostAndUser.get());
		}
	}
	
	private Vote mapToVote(VoteDto voteDto, Post post) {
		return Vote.builder().voteType(voteDto.getVoteType())
					.user(authService.getCurrentUser())
					.post(post)
					.build();
	}
	private void checkVoteTypeAndAddToPostForDifferentVoteType(VoteDto voteDto, Post post) {
		if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 2);
		} else if (VoteType.DOWNVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() - 2);
		}
	}
	private void checkVoteTypeAndAddToPostForSameVoteType(VoteDto voteDto, Post post) {
		if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() - 1);
		}
		else if (VoteType.DOWNVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		}
	}
	private void checkVoteTypeAndAddToPostForNotPresent(VoteDto voteDto, Post post) {
		if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		}
		else if (VoteType.DOWNVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() - 1);
		}
	}
}
	