package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void search() {
        User owner = userRepository.save(User.builder().name("User").email("user@mail.com").build());
        itemRepository.save(Item.builder().name("Дрель").description("Ударная").available(true).owner(owner).build());
        itemRepository.save(Item.builder().name("Отвертка").description("Крестовая").available(true).owner(owner).build());

        List<Item> result = itemRepository.search("дРеЛь", PageRequest.of(0, 10));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

}
