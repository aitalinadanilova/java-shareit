package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingOutDto create(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDto approve(Long userId, Long bookingId, Boolean approved) {
        checkUserExists(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Доступ к изменению статуса разрешен только владельцу вещи");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Нельзя изменить статус бронирования, которое не находится в ожидании");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto getById(Long userId, Long bookingId) {
        checkUserExists(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("Бронирование не найдено для пользователя " + userId);
        }

        return BookingMapper.toBookingOutDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllByBooker(Long userId, BookingState state) {
        checkUserExists(userId);

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                        userId, now, now, sortByStartDesc);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, now, sortByStartDesc);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, now, sortByStartDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllByBookerId(userId, sortByStartDesc);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllByOwner(Long userId, BookingState state) {
        checkUserExists(userId);

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                        userId, now, now, sortByStartDesc);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, sortByStartDesc);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, sortByStartDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllByItemOwnerId(userId, sortByStartDesc);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

}
