package com.ilyankov.helpdesk.service.api;

import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.domain.user.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getByEmail(String email);

    List<String> getEmailsByRole(Role role);

    User getCurrentUser();
}
