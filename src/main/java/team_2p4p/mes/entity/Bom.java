package team_2p4p.mes.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "bom")
public class Bom {

    @Id
    @Column(name="bom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bomId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product", referencedColumnName = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material1", referencedColumnName = "item_id")
    private Item item1;

    @Column(name="material1_amount")
    private String material1Amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "material2", referencedColumnName = "item_id")
    private Item item2;

    @Column( name="material2_amount")
    private String material2Amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "material3", referencedColumnName = "item_id")
    private Item item3;

    @Column(name="material3_amount")
    private String material3Amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material4", referencedColumnName = "item_id")
    private Item item4;

    @Column( name="material4_amount")
    private String material4Amount;
}
