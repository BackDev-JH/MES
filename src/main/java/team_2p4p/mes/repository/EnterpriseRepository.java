package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team_2p4p.mes.entity.Enterprise;
import team_2p4p.mes.entity.OrderMaterial;

import java.util.List;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

//    void deleteByItemCode(String cp);
//
//    Item findByItemName(String name);
    Enterprise findByItemItemId(Long itemId);
}
