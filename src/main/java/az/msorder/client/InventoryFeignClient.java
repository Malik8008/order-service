package az.msorder.client;

import az.msorder.dto.productDto.GetProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "ms-inventory",
        url = "http://localhost:8082"
)
public interface InventoryFeignClient {

    @PutMapping("/api/inventories/{productId}/reduce")
    GetProductDto reduceQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity);

    @GetMapping("/api/inventories/{productId}")
    GetProductDto getProductById(@PathVariable Long productId);
}
