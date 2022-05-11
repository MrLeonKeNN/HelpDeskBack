package com.epam.ilyankov.helpdesk.repository.api;

import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getByEmail(String email);

    List<String> getEmailsByRole(Role role);
}
