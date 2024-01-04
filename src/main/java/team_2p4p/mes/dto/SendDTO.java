package team_2p4p.mes.dto;

import lombok.Getter;
import lombok.Setter;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Obtain;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
public class SendDTO {

    private Long sendId;

    private Item item;
    private Long itemId;

    private ObtainDTO obtainDTO;
    private String obtainId;

    private Long sendProductNum;

    private boolean sendStat;

}
