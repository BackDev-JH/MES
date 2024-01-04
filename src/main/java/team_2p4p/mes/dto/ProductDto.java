package team_2p4p.mes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDto {
    private Long productId;
    private Long itemId;
    private Long productStock;
    private LocalDateTime makeDate;
    private boolean exportStat;
    private Long lotId;
}
