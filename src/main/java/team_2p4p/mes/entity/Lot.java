package team_2p4p.mes.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "lot")
public class Lot {

    @Id
    @Column(name="lot_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id1", referencedColumnName = "lot_log_id")
    private LotLog lotLog1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id2", referencedColumnName = "lot_log_id")
    private LotLog LotLog2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id3", referencedColumnName = "lot_log_id")
    private LotLog lotLog3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id4", referencedColumnName = "lot_log_id")
    private LotLog LotLog4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id5", referencedColumnName = "lot_log_id")
    private LotLog LotLog5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id6", referencedColumnName = "lot_log_id")
    private LotLog lotLog6;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_log_id7", referencedColumnName = "lot_log_id")
    private LotLog LotLog7;

    @Column(nullable = false, name="num")
    private Long num;
}
