package com.marktruong.reddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marktruong.reddit.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

}
