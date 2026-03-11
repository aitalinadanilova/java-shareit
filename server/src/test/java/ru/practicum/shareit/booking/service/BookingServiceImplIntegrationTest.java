package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createAndGetAllByBooker() {
        UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@m.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@m.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder().name("Item").description("D").available(true).build());

        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingService.create(booker.getId(), bookingDto);

        List<BookingOutDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Item");
    }

}
