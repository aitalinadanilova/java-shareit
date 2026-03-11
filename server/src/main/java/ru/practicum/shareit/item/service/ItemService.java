package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemFullDto getById(Long userId, Long itemId);

    List<ItemFullDto> getAllByOwner(Long userId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

}
