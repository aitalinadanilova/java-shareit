package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingOutDto> json;

    @Test
    void testBookingOutDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 3, 20, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 21, 12, 0);

        BookingOutDto dto = BookingOutDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingOutDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").contains("2026-03-20T12:00");
        assertThat(result).extractingJsonPathStringValue("$.end").contains("2026-03-21T12:00");
    }

}
