package team_2p4p.mes.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.LotLog;
import team_2p4p.mes.entity.Obtain;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class LotLogDTO {


    // 수주 Id
    private Long lotLogId;
    // 제품 번호
    private Item item;
    private Long itemId;
    // 공정
    private String process;
    // 투입물 종류
    private String inputKind;
    // 로트 상태
    private boolean lotStat;
    // 인풋 시간
    private LocalDateTime inputTime;
    // 아웃풋 시간
    private LocalDateTime  outputTime;
    // 로트
    private String lot;
    private String orderId;
    //부모 로그이력
    private long lotPLogId1;
    private long lotPLogId2;

}
