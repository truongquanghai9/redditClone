package com.marktruong.reddit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marktruong.reddit.model.Post;
import com.marktruong.reddit.model.User;
import com.marktruong.reddit.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

		
}
	