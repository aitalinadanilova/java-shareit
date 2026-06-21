package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void createAndGetUserRequests() {
        UserDto user = userService.create(UserDto.builder()
                .name("Aitalina")
                .email("danilova@mail.com")
                .build());

        itemRequestService.create(user.getId(), ItemRequestDto.builder()
                .description("Нужен Miro для графиков")
                .build());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужен Miro для графиков");
        assertThat(result.get(0).getCreated()).isNotNull();
    }

}
