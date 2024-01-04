package team_2p4p.mes.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team_2p4p.mes.entity.Send;

import java.util.List;

@SpringBootTest
public class SendTests {

    @Autowired
    private SendService sendService;


    @Test
    public void sendTest(){

        List<Send> sendList = sendService.getSendList();
    }

    @Test
    public void sendTest2(){
        System.out.println(sendService.getSendWaitList());

    }
}
