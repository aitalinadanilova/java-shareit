package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createAndFindAll() {
        UserDto userDto = UserDto.builder()
                .name("Integration User")
                .email("integration@mail.com")
                .build();

        userService.create(userDto);
        List<UserDto> users = userService.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users).extracting(UserDto::getEmail).contains("integration@mail.com");
    }

}
