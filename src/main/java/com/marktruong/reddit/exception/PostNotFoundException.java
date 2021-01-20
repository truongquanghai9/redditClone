package com.marktruong.reddit.exception;

public class PostNotFoundException extends RuntimeException {
	
	public PostNotFoundException (String ex) {
		super(ex);
	}
}
