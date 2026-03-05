package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null) existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existingItem.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemFullDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        ItemFullDto itemFullDto = ItemMapper.toItemFullDto(item, comments);

        if (item.getOwner().getId().equals(userId)) {
            setBookingDates(itemFullDto);
        }

        return itemFullDto;
    }

    @Override
    public List<ItemFullDto> getAllByOwner(Long userId) {
        checkUserExists(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, List<Comment>> commentsByItem = commentRepository.findAllByItem_IdIn(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());

                    ItemFullDto dto = ItemMapper.toItemFullDto(item, itemComments);

                    setBookingDates(dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        boolean canComment = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now);

        if (!canComment) {
            throw new ValidationException("Вы не можете оставить отзыв");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь не найден для комментария"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setBookingDates(ItemFullDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        itemDto.setLastBooking(bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(itemDto.getId(), BookingStatus.APPROVED, now)
                .map(b -> new BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null));

        itemDto.setNextBooking(bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), BookingStatus.APPROVED, now)
                .map(b -> new BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

}
