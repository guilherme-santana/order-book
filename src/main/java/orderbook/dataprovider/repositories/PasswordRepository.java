package orderbook.dataprovider.repositories;

import orderbook.domain.models.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {

    @Query("SELECT cs FROM Password cs WHERE cs.customer.id = :customerId")
    Optional<Password> findPasswordByCustomerId(
            @Param("customerId") Long customerId
    );

}
