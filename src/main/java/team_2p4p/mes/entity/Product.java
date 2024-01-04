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
@Table(name = "product")
public class Product {

    @Id
    @Column(name="product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false, name="product_stock")
    private Long productStock;

    @Column(nullable = false, name="make_date")
    private LocalDateTime makeDate;

    @Column(nullable = false, name="export_stat")
    private boolean exportStat;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn( name = "lot_log_id")
    private LotLog lotLogId;

    public void update() {
        this.productId = getProductId();
        this.item = getItem();
        this.productStock = getProductStock();
        this.makeDate = getMakeDate();
        this.exportStat = true;
        this.lotLogId = getLotLogId();
    }
}
