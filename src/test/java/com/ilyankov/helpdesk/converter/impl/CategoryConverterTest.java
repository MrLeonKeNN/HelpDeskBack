package com.ilyankov.helpdesk.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.dto.CategoryDto;

@RunWith(JUnitPlatform.class)
class CategoryConverterTest {

	private final CategoryConverter converter = new CategoryConverter();

	@Test
	void toDto() {
		Category category = Category.builder().name("Name").build();
		CategoryDto expected = CategoryDto.builder().name("Name").build();

		CategoryDto actual = converter.toDto(category);

		assertEquals(expected, actual);
	}

	@Test
	void fromDto() {
		CategoryDto categoryDto = CategoryDto.builder().name("Name").build();
		Category expected = Category.builder().name("Name").build();

		Category actual = converter.fromDto(categoryDto);

		assertEquals(expected.getName(), actual.getName());
	}
}
