package orderbook.dataprovider.repositories;

import orderbook.domain.models.CustomerStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerStockRepository extends JpaRepository<CustomerStock,Long> {

    @Query("SELECT cs FROM CustomerStock cs WHERE cs.customer.id = :customerId and cs.amount != 0")
    Optional<List<CustomerStock>> findByCustomerID(@Param("customerId") Long customerId);

    @Query("SELECT cs FROM CustomerStock cs WHERE cs.customer.id = :customerId and cs.asset.id = :assetId")
    Optional<CustomerStock> findByCustomerIdAndAssetId(
            @Param("customerId") Long customerId,
            @Param("assetId") Long assetId
    );

}
