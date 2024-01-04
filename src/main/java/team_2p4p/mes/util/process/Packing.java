package team_2p4p.mes.util.process;

import lombok.Data;
import team_2p4p.mes.util.calculator.MesAll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Packing {
    public Packing(){
        confirmList = new ArrayList<>();
    }
    boolean packingStat = false; // 포장 진행중 유무
    LocalDateTime inputTime; // 포장 투입시간
    LocalDateTime outputTime; // 포장 완료시간
    List<MesAll> confirmList; // 포장 리스트
    double amount; // 투입량
    long itemId;// 투입물종류 1,2,3,4


}
