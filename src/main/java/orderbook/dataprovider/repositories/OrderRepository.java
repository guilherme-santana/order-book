package orderbook.dataprovider.repositories;

import orderbook.domain.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o WHERE o.orderStatus NOT IN ('CANCELED', 'EXECUTED')")
    List<Order> findByOpenOrders();

}
