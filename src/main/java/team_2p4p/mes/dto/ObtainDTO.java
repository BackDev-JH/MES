package team_2p4p.mes.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Obtain;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class ObtainDTO {

    //수주Id
    private Long obtainId;

    //제품 번호
    private Long itemId;

    private Item item;

    @NotBlank(message = "제품 이름은 필수 입력 값입니다.")
    //제품 이름
    private String itemName;


    //고객 번호
    private Long customerId;

    private Customer customer;

    @NotBlank(message = "수량은 필수 입력 값입니다.")
    //수량
    private Long obtainAmount;

    //수주일
    private LocalDateTime obtainDate;

    @NotBlank(message = "납기일은 필수 입력 값입니다.")

    //납기일
    private LocalDateTime customerRequestDate;

    //예상납기일
    private LocalDateTime expectDate;

    //확정상태
    private boolean obtainStat;

    //확정일
    private LocalDateTime obtainStatDate;

    private static ModelMapper modelMapper = new ModelMapper();

    public Obtain createObtain() {
        Obtain obtain = modelMapper.map(this, Obtain.class);
        obtain.setItem(item);
        obtain.setCustomer(customer);
        return obtain;
    }

}
