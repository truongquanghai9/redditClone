package com.marktruong.reddit.exception;

public class SpringRedditException extends RuntimeException {

	public SpringRedditException(String ex) {
		super(ex);
	}
	public SpringRedditException(String exString, Exception ex) {
		super(exString,ex);
	}
}
