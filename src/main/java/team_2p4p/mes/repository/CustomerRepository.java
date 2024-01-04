package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Query("SELECT c FROM Customer c WHERE c.item1 = :item OR c.item2 = :item")
    Customer findByItem1OrItem2(@Param("item") Item item);
}
