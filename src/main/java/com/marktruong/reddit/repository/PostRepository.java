package com.marktruong.reddit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marktruong.reddit.model.Comment;
import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.Subreddit;
import com.marktruong.reddit.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

	//User findUserByPostId(Long postId);

	List<Post> findAllBySubreddit(Subreddit subreddit);

	List<Post> findByUser(User user);
}
