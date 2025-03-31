package orderbook.dataprovider.repositories;

import orderbook.domain.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT cs FROM Customer cs WHERE cs.document = :document")
    Optional<Customer> findCustomerByDocument(
            @Param("document") Integer document
    );

    @Query("SELECT cs FROM Customer cs WHERE cs.name = :name")
    Optional<Customer> findCustomerByName(
            @Param("name") String name
    );

}
