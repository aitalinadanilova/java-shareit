package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public User save(User user);

    public User update(User user);

    public Optional<User> findById(Long id);

    public Optional<User> findByEmail(String email);

    public List<User> findAll();

    public void delete(Long id);

}
