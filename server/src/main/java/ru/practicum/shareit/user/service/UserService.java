package ru.practicum.shareit.user.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getById(Long userId);

    List<UserDto> findAll();

    void delete(Long userId);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

}
