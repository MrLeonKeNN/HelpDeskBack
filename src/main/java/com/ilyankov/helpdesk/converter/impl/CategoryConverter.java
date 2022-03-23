package com.ilyankov.helpdesk.converter.impl;

import org.springframework.stereotype.Component;

import com.ilyankov.helpdesk.converter.api.Converter;
import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.dto.CategoryDto;

@Component
public class CategoryConverter implements Converter<Category, CategoryDto> {

	@Override
	public CategoryDto toDto(Category category) {
		return CategoryDto.builder().name(category.getName()).build();
	}

	@Override
	public Category fromDto(CategoryDto categoryDto) {
		return Category.builder().name(categoryDto.getName()).build();
	}
}
