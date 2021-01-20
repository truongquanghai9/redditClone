package com.marktruong.reddit.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marktruong.reddit.dto.CommentDto;
import com.marktruong.reddit.model.Comment;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPost(Post post);

	List<Comment> findByUser(User user);

}
