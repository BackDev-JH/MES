package team_2p4p.mes.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderMaterialDTO {

    //발주 Id
    private Long orderId;
    //제품번호
    private Long itemId;
    //수량
    private Long orderItemAmount;
    //발주날짜
    private LocalDateTime orderDate;
    //입고 예정일
    private LocalDateTime importExpectDate;
    //업체 Id
    private Long enterpriseId;

    private boolean orderStat;
}
