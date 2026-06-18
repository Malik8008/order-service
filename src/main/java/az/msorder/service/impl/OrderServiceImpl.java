package az.msorder.service.impl;

import az.msorder.client.InventoryFeignClient;
import az.msorder.configuration.RabbitMQConfig;
import az.msorder.dto.GetOrderDto;
import az.msorder.dto.PostOrderDto;
import az.msorder.dto.orderEventDto.OrderEventDto;
import az.msorder.entity.Order;
import az.msorder.exception.IdNotFoundException;
import az.msorder.exception.NoEnoughException;
import az.msorder.repository.OrderRepository;
import az.msorder.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final InventoryFeignClient inventoryFeignClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public GetOrderDto create(PostOrderDto dto) {

        try {
            inventoryFeignClient.reduceQuantity(dto.productId(), dto.quantity());
        } catch (FeignException ex) {
            throw new NoEnoughException("Not enough stock");
        }

        Order order = new Order();
        order.setProductId(dto.productId());
        order.setQuantity(dto.quantity());
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        OrderEventDto event = new OrderEventDto(
                saved.getId(),
                saved.getProductId(),
                saved.getQuantity()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        return modelMapper.map(saved, GetOrderDto.class);
    }

    @Override
    public GetOrderDto getById(Long id) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IdNotFoundException("Order not found"));
        return modelMapper.map(order, GetOrderDto.class);
    }
}
