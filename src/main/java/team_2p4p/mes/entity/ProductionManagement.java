package team_2p4p.mes.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = {"obtain","process"})
@Table(name = "production_management")
public class ProductionManagement {

    @Id
    @Column(name="production_management_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productionManagementId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "obtain_id", referencedColumnName = "obtain_id")
    private Obtain obtain;

    @Column(nullable = false, name="process")
    private String process;

    @Column(nullable = false, name="process_start_time")
    private LocalDateTime processStartTime;

    @Column(nullable = false, name="process_finish_time")
    private LocalDateTime processFinishTime;

    @Column(nullable = false, name="process_amount")
    private Long processAmount;

    @Column(nullable = false, name="process_stat")
    private int processStat;

    @Builder
    public ProductionManagement(Obtain obtain,String process,LocalDateTime processStartTime,LocalDateTime processFinishTime,Long processAmount){
        this.obtain = obtain;
        this.process = process;
        this.processStartTime = processStartTime;
        this.processFinishTime = processFinishTime;
        this.processAmount = processAmount;
        this.processStat = 0;
    }

    public void update(int stat) {
        this.productionManagementId = getProductionManagementId();
        this.obtain = getObtain();
        this.process = getProcess();
        this.processStartTime = getProcessStartTime();
        this.processFinishTime = getProcessFinishTime();
        this.processAmount = getProcessAmount();
        this.processStat = stat;
    }
}
