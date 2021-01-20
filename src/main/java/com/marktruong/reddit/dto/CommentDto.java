package com.marktruong.reddit.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
	private Long id;
	private String text;
	private Long postId;
	private String username;
	private Instant createdDate;
}
