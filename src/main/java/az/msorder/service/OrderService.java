package az.msorder.service;

import az.msorder.dto.GetOrderDto;
import az.msorder.dto.PostOrderDto;
import az.msorder.dto.PutOrderDto;

import java.util.List;

public interface OrderService {
    GetOrderDto create(PostOrderDto postDto);
    GetOrderDto getById(Long id);
    List<GetOrderDto> getAll();
    GetOrderDto update(Long id, PutOrderDto putDto);
    void delete(Long id);
}
