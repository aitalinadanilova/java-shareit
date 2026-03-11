package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder().name("User1").email("u1@mail.com").build());
        user2 = userRepository.save(User.builder().name("User2").email("u2@mail.com").build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Request 1")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequestorId(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Request 1");
    }

    @Test
    void findAllByRequestorIdNot() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequestorIdNot(
                user2.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestor().getId()).isEqualTo(user1.getId());
    }

}
