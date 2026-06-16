package az.msorder.dto;

import java.time.LocalDateTime;

public record GetOrderDto(
        Long id,
        Long productId,
        int quantity,
        String status,
        LocalDateTime createdAt
) {
}
