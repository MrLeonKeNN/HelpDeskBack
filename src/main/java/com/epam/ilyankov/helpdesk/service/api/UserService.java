package com.epam.ilyankov.helpdesk.service.api;

import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.domain.user.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getByEmail(String email);

    List<String> getEmailsByRole(Role role);

    User getCurrentUser();
}
