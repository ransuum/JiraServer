package org.parent.jira.service;

import org.parent.jira.models.entity.User;

public interface UserService {
    User findEntityByEmail(String email);
}
