package org.example.data.jdbc.repo;

import org.example.data.jdbc.domain.Order;
import org.springframework.data.repository.CrudRepository;

/**
 * @author renc
 */
public interface OrderRepository extends CrudRepository<Order, Integer> {
}
