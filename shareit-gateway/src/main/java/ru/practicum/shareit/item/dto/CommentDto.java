package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    @Size(max = 2000, message = "Текст отзыва не должен превышать 2000 символов")
    private String text;

    private String authorName;

    private LocalDateTime created;

}
