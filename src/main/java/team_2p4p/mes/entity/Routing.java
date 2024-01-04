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
@Table(name = "routing")
public class Routing {

    @Id
    @Column(name="routing_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process1", referencedColumnName = "process_id")
    private Process process1;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process2", referencedColumnName = "process_id")
    private Process process2;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process3", referencedColumnName = "process_id")
    private Process process3;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process4", referencedColumnName = "process_id")
    private Process process4;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process5", referencedColumnName = "process_id")
    private Process process5;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process6", referencedColumnName = "process_id")
    private Process process6;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process7", referencedColumnName = "process_id")
    private Process process7;

}
