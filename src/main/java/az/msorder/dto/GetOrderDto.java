package az.msorder.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class GetOrderDto {
    Long id;
    Long productId;
    int quantity;
    String status;
    LocalDateTime createdAt;
}
