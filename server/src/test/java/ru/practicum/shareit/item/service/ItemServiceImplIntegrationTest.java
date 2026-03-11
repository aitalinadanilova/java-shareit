package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getAllByOwner() {
        UserDto user = userService.create(UserDto.builder().name("Owner").email("owner@mail.com").build());
        itemService.create(user.getId(), ItemDto.builder().name("Item").description("Desc").available(true).build());

        List<ItemFullDto> items = itemService.getAllByOwner(user.getId(), 0, 10);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Item");
        assertThat(items.get(0).getComments()).isEmpty();
    }

}
