package orderbook.dataprovider.repositories;

import orderbook.domain.models.Order;
import orderbook.enuns.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query(value = "SELECT * FROM order_book.ordens WHERE order_status NOT IN ('CANCELED', 'EXECUTED')", nativeQuery = true)
    List<Order> findByOpenOrders();

}
