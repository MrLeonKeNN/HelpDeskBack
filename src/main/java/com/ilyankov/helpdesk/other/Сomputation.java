package com.ilyankov.helpdesk.other;

public class Сomputation {
	public static Integer getFirstResult(Integer page, Integer quantity) {
		return page * quantity - quantity;
	}
}
