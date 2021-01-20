package com.marktruong.reddit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {
	private final VoteService voteService;
	
	@PostMapping
	public ResponseEntity<Void> vote(@RequestBody VoteDto voteDto) {
		voteService.vote(voteDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
