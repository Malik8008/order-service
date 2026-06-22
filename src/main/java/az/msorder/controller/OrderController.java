package az.msorder.controller;

import az.msorder.dto.GetOrderDto;
import az.msorder.dto.PostOrderDto;
import az.msorder.dto.PutOrderDto;
import az.msorder.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<GetOrderDto>> getAll(){
        return ResponseEntity.ok(orderService.getAll());
    }

    @PostMapping
    public ResponseEntity<GetOrderDto> create(@RequestBody PostOrderDto dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetOrderDto> update(@PathVariable Long id,
                              @RequestBody PutOrderDto putDto){
        return ResponseEntity.ok(orderService.update(id,putDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
