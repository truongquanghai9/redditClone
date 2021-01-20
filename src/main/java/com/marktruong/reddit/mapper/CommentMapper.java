package com.marktruong.reddit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.marktruong.reddit.dto.CommentDto;
import com.marktruong.reddit.model.Comment;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.User;

@Mapper(componentModel="spring")
public interface CommentMapper {
	
	@Mapping(target="post", source="post")
	@Mapping(target="user", source="user")
	@Mapping(target="createdDate", expression="java(java.time.Instant.now())")
	Comment mapCommentDtoToComment(CommentDto commentDto, Post post, User user);
	
	@Mapping(target="postId", expression="java(comment.getPost().getPostId())")
	@Mapping(target="username", expression="java(comment.getUser().getUsername())")
	CommentDto mapCommentToCommentDto(Comment comment);
}
