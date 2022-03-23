package com.ilyankov.helpdesk.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ilyankov.helpdesk.converter.impl.CategoryConverter;
import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.dto.CategoryDto;
import com.ilyankov.helpdesk.repository.api.CategoryRepository;
import com.ilyankov.helpdesk.service.api.CategoryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryConverter converter;

	@Override
	@Transactional(readOnly = true)
	public Optional<Category> getByName(String name) {
		return categoryRepository.getByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryDto> getAll() {
		return converter.toDto(categoryRepository.getAll());
	}
}
