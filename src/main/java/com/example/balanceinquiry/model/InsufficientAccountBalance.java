package com.example.balanceinquiry.model;

public class InsufficientAccountBalance extends RuntimeException {
	private static final long serialVersionUID = 1L;
	String message;

	public InsufficientAccountBalance(String message) {
		super(message);
		this.message = message;
	}
}
