package az.msorder.controller;

import az.msorder.dto.GetOrderDto;
import az.msorder.dto.PostOrderDto;
import az.msorder.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    @PostMapping
    public GetOrderDto create(@RequestBody PostOrderDto dto) {
        return orderService.create(dto);
    }

    @GetMapping("/{id}")
    public GetOrderDto getById(@PathVariable Long id) {
        return orderService.getById(id);
    }
}
