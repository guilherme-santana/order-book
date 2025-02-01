package orderbook.dataprovider.repositories;

import orderbook.domain.models.Asset;
import orderbook.domain.models.Order;
import orderbook.enuns.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o WHERE o.orderStatus NOT IN ('CANCELED', 'EXECUTED')")
    List<Order> findByOpenOrders();

    @Query("SELECT o FROM Order o " +
            "WHERE o.orderStatus NOT IN ('CANCELED', 'EXECUTED') " +
            "and o.customer.id != :customerId " +
            "and o.orderType != :orderType " +
            "and o.price = :price " +
            "and o.asset.id = :assetId"
    )
    List<Order> findByOpenValidForMatchOrders(
            @Param("customerId") Long customerId,
            @Param("orderType") OrderType orderType,
            @Param("price") BigDecimal price,
            @Param("assetId") Long assetId
            );

}
