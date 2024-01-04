package team_2p4p.mes.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "enterprise")
public class Enterprise {

    @Id
    @Column(name="enterprise_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enterpriseId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    private Item item;

    @Column(nullable = false, name="enterprise_name")
    private String enterpriseName;
    @Column(nullable = false, name="delivery_time")
    private String deliveryTime;
}
