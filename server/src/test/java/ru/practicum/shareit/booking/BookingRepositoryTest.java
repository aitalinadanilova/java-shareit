package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Owner").email("owner@mail.com").build());
        booker = userRepository.save(User.builder().name("Booker").email("booker@mail.com").build());
        item = itemRepository.save(Item.builder().name("Дрель").description("Ударная").available(true).owner(owner).build());
    }

    @Test
    void findAllByBookerIdAndEndBefore_shouldReturnPastBookings() {
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(3))
                .item(item).booker(booker).status(BookingStatus.APPROVED).build());

        List<Booking> result = bookingRepository.findAllByBookerIdAndEndBefore(
                booker.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfter_shouldReturnFutureBookings() {
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item).booker(booker).status(BookingStatus.APPROVED).build());

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartAfter(
                owner.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc_shouldReturnLastBooking() {
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(9))
                .item(item).booker(booker).status(BookingStatus.APPROVED).build());

        Booking lastBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(booker).status(BookingStatus.APPROVED).build());

        Optional<Booking> result = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(lastBooking.getId());
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusAndEndBefore_shouldReturnTrueIfFinished() {
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(2))
                .item(item).booker(booker).status(BookingStatus.APPROVED).build());

        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                booker.getId(), item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertThat(exists).isTrue();
    }

}
