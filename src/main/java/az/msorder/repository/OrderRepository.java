package az.msorder.repository;

import az.msorder.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByIdAndIsDeletedFalse(Long id);
    List<Order> findAllByIsDeletedFalse();
}
