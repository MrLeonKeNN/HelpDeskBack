package com.ilyankov.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ilyankov.helpdesk.converter.impl.CategoryConverter;
import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.dto.CategoryDto;
import com.ilyankov.helpdesk.repository.api.CategoryRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CategoryServiceImplTest {

	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private CategoryConverter converter;
	@InjectMocks
	private CategoryServiceImpl categoryService;

	@Test
	void getByName() {
		Optional<Category> actual = Optional.of(new Category(1L, "Name"));

		when(categoryRepository.getByName("Name")).thenReturn(actual);

		Optional<Category> expected = categoryService.getByName("Name");

		assertEquals(expected, actual);

		verify(categoryRepository, times(1)).getByName("Name");
	}

	@Test
	void getAll() {
		List<CategoryDto> expected = List.of(CategoryDto.builder().name("123").build(),
				CategoryDto.builder().name("222").build(), CategoryDto.builder().name("333").build());

		when(converter.toDto(anyList())).thenReturn(expected);

		List<CategoryDto> actual = categoryService.getAll();

		assertEquals(expected, actual);
		verify(categoryRepository, times(1)).getAll();
		verify(converter, times(1)).toDto(anyList());
	}
}
