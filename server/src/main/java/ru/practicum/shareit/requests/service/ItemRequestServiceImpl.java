package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequestMapper;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        ItemRequest request = ItemRequestMapper.toItemRequest(dto, user);
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(request), null);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        checkUserExists(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorId(
                userId, Sort.by(Sort.Direction.DESC, "created"));

        return addItemsToRequests(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        checkUserExists(userId);

        PageRequest pageRequest = PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId, pageRequest);

        return addItemsToRequests(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        checkUserExists(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));

        List<Item> items = itemRepository.findAllByRequest_Id(requestId);

        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    private List<ItemRequestDto> addItemsToRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequest_IdIn(requestIds)
                .stream()
                .collect(groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(
                        request,
                        itemsByRequest.getOrDefault(request.getId(), List.of())))
                .collect(toList());
    }

}
