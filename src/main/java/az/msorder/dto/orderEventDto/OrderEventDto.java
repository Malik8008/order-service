package az.msorder.dto.orderEventDto;

public record OrderEventDto(
        Long orderId,
        Long productId,
        int quantity
) {
}
