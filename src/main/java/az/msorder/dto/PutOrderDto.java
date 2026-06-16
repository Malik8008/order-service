package az.msorder.dto;

public record PutOrderDto (
        Long productId,
        int quantity
){
}
