package com.marktruong.reddit.mapper;

import java.util.Optional;

//import static com.marktruong.reddit.model.VoteType.DOWNVOTE;
//import static com.marktruong.reddit.model.VoteType.UPVOTE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.marktruong.reddit.dto.PostRequest;
import com.marktruong.reddit.dto.PostResponse;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.Subreddit;
import com.marktruong.reddit.model.User;
import com.marktruong.reddit.model.Vote;
import com.marktruong.reddit.model.VoteType;
import com.marktruong.reddit.repository.CommentRepository;
import com.marktruong.reddit.repository.VoteRepository;
import com.marktruong.reddit.service.AuthService;

@Mapper(componentModel="spring")
public abstract class PostMapper {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private AuthService authService;

	
	@Mapping(target="user", source="user")
	@Mapping(target="subreddit", source="subreddit")
	@Mapping(target="createdDate", expression="java(java.time.Instant.now())")
	@Mapping(target="description", source="postRequest.description")
	@Mapping(target="voteCount", constant="0")
	public abstract Post mapPostRequestToPost(PostRequest postRequest, Subreddit subreddit, User user);
	

	@Mapping(target="subredditName", source="subreddit.name")
	@Mapping(target="username", source="user.username")
    @Mapping(target = "commentCount", expression = "java(getCommentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    //@Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    //@Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")	
	public abstract PostResponse mapPostToPostResponse(Post post);
	
	
	
	Integer getCommentCount(Post post) {
		return commentRepository.findByPost(post).size();
	}
	String getDuration(Post post) {
		return TimeAgo.using(post.getCreatedDate().toEpochMilli());
	}
	/*
	boolean isPostUpVoted(Post post) {
		return checkVoteType(post,VoteType.UPVOTE);
	}
	boolean isPostDownVoted(Post post) {
		return checkVoteType(post, VoteType.DOWNVOTE);
	}
	private boolean checkVoteType(Post post, VoteType voteType) {
		if (authService.isLoggedIn()) {
			Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
										authService.getCurrentUser());
			return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
		}
		return false;
	}
	*/

}
