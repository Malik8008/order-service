package az.msorder.dto.orderEventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class OrderEventDto{
    Long orderId;
    Long productId;
    int quantity;
}
