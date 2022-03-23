package com.ilyankov.helpdesk.repository.api;

import com.ilyankov.helpdesk.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> getByName(String name);

    List<Category> getAll();
}
