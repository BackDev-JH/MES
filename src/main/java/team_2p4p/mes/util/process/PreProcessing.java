package team_2p4p.mes.util.process;

import lombok.Data;
import team_2p4p.mes.util.calculator.MesAll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PreProcessing {
    //전처리

    public PreProcessing(){
        confirmList = new ArrayList<>();
    }
    boolean preProcessingStat = false; // 전처리 진행중 유무
    LocalDateTime inputTime; // 전처리 투입시간
    LocalDateTime outputTime; // 전처리 완료 시간
    List<MesAll> confirmList; //확정 리스트
    double amount; // 투입량
    long itemId;// 투입물종류 1,2,3,4

}
