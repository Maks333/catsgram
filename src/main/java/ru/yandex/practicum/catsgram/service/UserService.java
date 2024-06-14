package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email must be present");
        }

        users.values()
                .stream()
                .map(User::getEmail)
                .filter(email -> email.equals(user.getEmail()))
                .findFirst()
                .ifPresent(email -> {
                    throw new DuplicatedDataException("Email " + email + " is already in use");
                });

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id must be present");
        }

        if (users.containsKey(user.getId())) {
            User userToUpdate = users.get(user.getId());
            if (user.getEmail() != null &&
                    !user.getEmail().isBlank() &&
                    !userToUpdate.getEmail().equals(user.getEmail())) {
                users.values()
                        .stream()
                        .map(User::getEmail)
                        .filter(email -> email.equals(user.getEmail()))
                        .findFirst()
                        .ifPresentOrElse(email -> {
                            throw new DuplicatedDataException("Email " + email + " is already in use");
                        }, () -> userToUpdate.setEmail(user.getEmail()));
            }
            if (user.getPassword() != null) {
                userToUpdate.setPassword(user.getPassword());
            }
            if (user.getUsername() != null) {
                userToUpdate.setUsername(user.getUsername());
            }
            return userToUpdate;
        }
        throw new NotFoundException("User with id = " + user.getId() + " is not found");
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User findUserById(long userId) {
        return users.values().stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }
}
