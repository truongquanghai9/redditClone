package com.marktruong.reddit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
	private Long postId;
	private String subredditName;
	private String username;
	private String postName;
	private String url;
	private String description;
	private Integer voteCount;
	private Integer commentCount;
	private String duration;
	private boolean upVote;
	private boolean downVote;
}
