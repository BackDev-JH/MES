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
@Table(name = "send")
public class Send {

    @Id
    @Column(name="send_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sendId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "obtain_id")
    private Obtain obtain;

    @Column(nullable = false, name="send_product_num")
    private Long sendProductNum;

    @Column(nullable = false, name="send_stat")
    private boolean sendStat;


    public void update() {
        this.sendId = getSendId();
        this.item = getItem();
        this.obtain = getObtain();
        this.sendProductNum = getSendProductNum();
        this.sendStat = true;
    }
}
