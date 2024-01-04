package team_2p4p.mes.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team_2p4p.mes.dto.LotLogDTO;

import java.util.List;

@SpringBootTest
public class findLotTest {

    @Autowired
    private LotLogService lotLogService;

    @Test
    public void findPLot(){

        LotLogDTO lotLogDTO = new LotLogDTO();
        lotLogDTO.setLotLogId(23L);
        List<String> list = lotLogService.findPLot(lotLogDTO);
        System.out.println(list);
    }
}
