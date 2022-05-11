package com.epam.ilyankov.helpdesk.pagination;

public class PaginationHelper {

	public static Integer getFirstResult(Integer page, Integer quantity) {
		return page * quantity - quantity;
	}
}
