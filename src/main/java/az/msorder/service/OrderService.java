package az.msorder.service;

import az.msorder.dto.GetOrderDto;
import az.msorder.dto.PostOrderDto;

public interface OrderService {
    GetOrderDto create(PostOrderDto postDto);
    GetOrderDto getById(Long id);
}
