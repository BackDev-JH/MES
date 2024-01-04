package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.ProductionManagement;
import team_2p4p.mes.entity.Send;
import team_2p4p.mes.repository.SendRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class SendService {

    private final SendRepository sendRepository;

    public List<Send> getSendList(){
            List<Send> sendList = sendRepository.findAll();

            List<Send> filteredList = sendList.stream()
                    .filter(send -> send.isSendStat() == true)
                    .collect(Collectors.toList());
            return filteredList;
    }


    public List<Send> getSendWaitList(){
            List<Send> sendList = sendRepository.findAll();

            List<Send> filteredList = sendList.stream()
                    .filter(send -> send.isSendStat() == false)
                    .collect(Collectors.toList());
            return filteredList;
    }






}
