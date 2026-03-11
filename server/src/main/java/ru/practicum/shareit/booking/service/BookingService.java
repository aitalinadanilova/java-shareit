package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingOutDto create(Long userId, BookingDto bookingDto);

    BookingOutDto approve(Long userId, Long bookingId, Boolean approved);

    BookingOutDto getById(Long userId, Long bookingId);

    List<BookingOutDto> getAllByBooker(Long userId, BookingState state);

    List<BookingOutDto> getAllByOwner(Long userId, BookingState state);
}
