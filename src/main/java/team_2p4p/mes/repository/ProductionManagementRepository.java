package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team_2p4p.mes.entity.ProductionManagement;

import java.time.LocalDate;
import java.util.List;

public interface ProductionManagementRepository extends JpaRepository<ProductionManagement,Long> {
//    @Query(value = "select * from production_management where Date(process_start_time) >= :today and process_stat = 0", nativeQuery = true)
//    List<ProductionManagement> statZeroToOne(@Param("today") LocalDate today);
//
//    @Query(value = "select * from production_management where Date(process_finish_time) >= :today process_stat = 1", nativeQuery = true)
//    List<ProductionManagement> statOneToTwo(@Param("today") LocalDate today);
}
