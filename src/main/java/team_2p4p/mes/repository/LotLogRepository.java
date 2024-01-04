package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team_2p4p.mes.entity.LotLog;

import java.time.LocalDate;
import java.util.List;

public interface LotLogRepository extends JpaRepository<LotLog,Long> {
//    @Query(value = "select * from lot_log where Date(output_time) >= :today and lot_stat = 0", nativeQuery = true)
//    List<LotLog> statZeroToOne(@Param("today") LocalDate today);
}
