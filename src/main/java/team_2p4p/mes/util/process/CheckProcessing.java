package team_2p4p.mes.util.process;

import lombok.Data;
import team_2p4p.mes.util.calculator.MesAll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CheckProcessing {
    public CheckProcessing(){
        confirmList = new ArrayList<>();
    }
    boolean fillProcessingStat = false; // check 진행중 유무
    LocalDateTime inputTime; // 검사 투입시간
    LocalDateTime outputTime; // 검사 완료시간
    List<MesAll> confirmList; // 검사 리스트
    double amount; // 투입량
    long itemId;// 투입물종류 1,2,3,4
}
