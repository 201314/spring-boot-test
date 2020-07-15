package com.baomidou.springboot.services;

public interface IMyService {
	default public void add() {
	}

	default public void doSomeThing(String someThing) {
	}
}
