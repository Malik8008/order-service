package az.msorder.dto.productDto;

public record GetProductDto(
        Long id,
        String name,
        int quantity
) {
}
