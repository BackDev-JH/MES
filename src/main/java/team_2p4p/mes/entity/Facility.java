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
@Table(name = "facility")
public class Facility {

    @Id
    @Column(name="facility_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    @Column(name="facility_name")
    private String facilityName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material1", referencedColumnName = "item_id")
    private Item item1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material2", referencedColumnName = "item_id")
    private Item item2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material3", referencedColumnName = "item_id")
    private Item item3;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material4", referencedColumnName = "item_id")
    private Item item4;

    @Column(name="facility_capa")
    private String facilityCapa;

    @Column(name="process_redytime")
    private int processReadytime;
}
