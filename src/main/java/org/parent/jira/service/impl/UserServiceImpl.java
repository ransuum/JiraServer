package org.parent.jira.service.impl;

import org.parent.jira.exception.EntityNotFoundException;
import org.parent.jira.models.entity.User;
import org.parent.jira.repository.UserRepository;
import org.parent.jira.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    }
}
