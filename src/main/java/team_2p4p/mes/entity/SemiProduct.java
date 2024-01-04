package team_2p4p.mes.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "semi_product")
public class SemiProduct {

    @Id
    @Column(name="semi_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long semiProductId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "process_id")
    private Process process;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "lot_log_id")
    private LotLog lotLog;

    @Column(nullable = false, name="semi_product_stock")
    private Long semiProductStock;

    @Column(nullable = false, name="semi_product_date")
    private LocalDateTime semiProductDate;

    @Column(nullable = false, name="semi_product_expect_date")
    private LocalDateTime semiProductExpectDate;

    @Column(nullable = false, name="semi_stat")
    private boolean semiStat;
}
