package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team_2p4p.mes.entity.Facility;
import team_2p4p.mes.entity.Process;

public interface ProcessRepository extends JpaRepository<Process, Long> {

}
