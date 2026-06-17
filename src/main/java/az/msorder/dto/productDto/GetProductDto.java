package az.msorder.dto.productDto;

import lombok.Data;

@Data
public class GetProductDto {
    Long id;
    String name;
    int quantity;
}
