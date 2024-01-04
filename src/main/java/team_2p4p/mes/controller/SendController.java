package team_2p4p.mes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team_2p4p.mes.entity.Send;
import team_2p4p.mes.service.SendService;

import java.util.List;

@RestController
@RequestMapping("juicyfresh/")
@RequiredArgsConstructor
public class SendController {

    private final SendService sendService;

    @GetMapping("past")
        public List<Send> sendPast() {
            return sendService.getSendList();
        }


    @GetMapping("wait")
    public List<Send> sendWait(){
        return sendService.getSendWaitList();
    }

}
