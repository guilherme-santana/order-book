package orderbook.dataprovider.repositories;

import orderbook.domain.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    @Query("SELECT cs FROM Wallet cs WHERE cs.customer.id = :customerId")
    Optional<Wallet> findByCustomerID(@Param("customerId") Long customerId);

}
