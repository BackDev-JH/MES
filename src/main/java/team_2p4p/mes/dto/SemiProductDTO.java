package team_2p4p.mes.dto;

import lombok.*;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.LotLog;
import team_2p4p.mes.entity.Process;

import java.time.LocalDateTime;


@Getter
@Setter
public class SemiProductDTO {

    private Long semiProductId;

    private Item item;

    private Process process;

    private LotLog lotLog;

    private Long semiProductStock; //수량

    private LocalDateTime semiProductDate;  //발생일

    private LocalDateTime semiProductExpectDate; //투입예정일

    private boolean semiStat;
}
