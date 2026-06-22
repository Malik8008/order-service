package az.msorder.service.impl;

import az.msorder.client.InventoryFeignClient;
import az.msorder.configuration.RabbitMQConfig;
import az.msorder.dto.GetOrderDto;
import az.msorder.dto.PostOrderDto;
import az.msorder.dto.PutOrderDto;
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
import java.util.List;

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
            inventoryFeignClient.getProductById(dto.productId());
        } catch (FeignException e) {
            throw new IdNotFoundException("Product with id: " + dto.productId() + " not found.");
        }

        try {
            inventoryFeignClient.reduceQuantity(dto.productId(), dto.quantity());
        } catch (FeignException ex) {
            throw new NoEnoughException("Not enough stock");
        }

        Order order = new Order();
        order.setProductId(dto.productId());
        order.setIsDeleted(false);
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

    @Override
    public List<GetOrderDto> getAll() {
        return orderRepository.findAllByIsDeletedFalse()
                .stream().map(or -> modelMapper.map(or, GetOrderDto.class)).toList();
    }

    @Override
    public GetOrderDto update(Long id, PutOrderDto putDto) {

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IdNotFoundException("Order with id: " + id + " not found"));

        inventoryFeignClient.getProductById(putDto.productId());

        Long oldProductId = order.getProductId();
        int oldQuantity = order.getQuantity();

        if (!oldProductId.equals(putDto.productId())) {

            try {
                inventoryFeignClient.increaseQuantity(oldProductId, oldQuantity);

                inventoryFeignClient.reduceQuantity(
                        putDto.productId(),
                        putDto.quantity()
                );

            } catch (FeignException e) {
                throw new NoEnoughException("Stock operation failed");
            }

        } else {

            int diff = putDto.quantity() - oldQuantity;

            try {
                if (diff > 0) {
                    inventoryFeignClient.reduceQuantity(putDto.productId(), diff);
                } else if (diff < 0) {
                    inventoryFeignClient.increaseQuantity(putDto.productId(), Math.abs(diff));
                }
            } catch (FeignException e) {
                throw new NoEnoughException("Not enough stock");
            }
        }

        order.setProductId(putDto.productId());
        order.setQuantity(putDto.quantity());

        Order saved = orderRepository.save(order);

        return modelMapper.map(saved, GetOrderDto.class);
    }

    @Override
    public void delete(Long id) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IdNotFoundException("Order not found"));
        order.setIsDeleted(true);
        orderRepository.save(order);
    }
}
