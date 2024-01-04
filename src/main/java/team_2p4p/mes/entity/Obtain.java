package team_2p4p.mes.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "obtain")
public class Obtain {

    @Id
    @Column(name="obtain_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long obtainId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "customer_id")
    private Customer customer;

    @Column(nullable = false, name="obtain_amount")
    private Long obtainAmount;

    @Column(nullable = false, name="obtain_date")
    private LocalDateTime obtainDate;

    @Column(name="customer_request_date")
    private LocalDateTime customerRequestDate;

    @Column(nullable = true, name="expect_date")
    private LocalDateTime expectDate;

    @Column(nullable = false, name="obtain_stat")
    private boolean obtainStat;

    @Column(name="obtain_stat_date")
    private LocalDateTime obtainStatDate;


    public void setCustomerId(){}

    public void updateStat(){
        this.obtainStat = true;
    }

    public void updateConfirmTime(){
        this.obtainStatDate = LocalDateTime.now();
    }
}
