package com.epam.ilyankov.helpdesk.service.api;

import com.epam.ilyankov.helpdesk.domain.Category;
import com.epam.ilyankov.helpdesk.dto.CategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> getByName(String name);

    List<CategoryDto> getAll();
}
