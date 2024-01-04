package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team_2p4p.mes.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    void deleteByItemCode(String cp);

    Item findByItemName(String name);
}
