package com.marktruong.reddit.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marktruong.reddit.dto.SubredditDto;
import com.marktruong.reddit.exception.SpringRedditException;
import com.marktruong.reddit.mapper.SubredditMapper;
import com.marktruong.reddit.model.Subreddit;
import com.marktruong.reddit.model.User;
import com.marktruong.reddit.repository.SubredditRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
	
	private final SubredditRepository subredditRepository;
	private final SubredditMapper subredditMapper;
	private final AuthService authService;
	
	@Transactional
	public SubredditDto save(SubredditDto subRedditDto) {
		User user = authService.getCurrentUser();
		Subreddit subreddit = subredditMapper.mapDtoToSubreddit(subRedditDto);
		subreddit.setUser(user);
		Subreddit save = subredditRepository.save(subreddit);

		subRedditDto.setSubredditId(save.getSubredditId());
		return subRedditDto;
	}

	
	@Transactional(readOnly=true)
	public List<SubredditDto> getAll() {
		return subredditRepository.findAll()
							.stream()
							.map(subredditMapper::mapSubredditToDto)
							.collect(Collectors.toList());
	}


	public SubredditDto getSubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return subredditMapper.mapSubredditToDto(subreddit);
	}
}
