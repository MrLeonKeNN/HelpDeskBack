package com.ilyankov.helpdesk.service.api;

import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.dto.CategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> getByName(String name);

    List<CategoryDto> getAll();
}
