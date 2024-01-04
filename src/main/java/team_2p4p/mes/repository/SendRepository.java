package team_2p4p.mes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team_2p4p.mes.entity.Send;

import java.time.LocalDate;
import java.util.List;

public interface SendRepository extends JpaRepository<Send, Long> {
   // @Query(value = "select * from send where Date(obtain.expect_date) >= :today and send_stat = 0", nativeQuery = true)
    //List<Send> statZeroToOne(@Param("today") LocalDate today);
}
