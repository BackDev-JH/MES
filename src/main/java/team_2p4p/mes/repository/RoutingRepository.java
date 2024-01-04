package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Routing;

public interface RoutingRepository extends JpaRepository<Routing, Long> {

}
