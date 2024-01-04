package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Process;
import team_2p4p.mes.entity.Routing;
import team_2p4p.mes.repository.ItemRepository;
import team_2p4p.mes.repository.RoutingRepository;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutingService {

    private final RoutingRepository routingRepository;

    public void saveRouting(Item item, Process process1, Process process2, Process process3, Process process4, Process process5, Process process6, Process process7){
        Routing routing = Routing.builder()
                .item(item)
                .process1(process1)
                .process2(process2)
                .process3(process3)
                .process4(process4)
                .process5(process5)
                .process6(process6)
                .process7(process7)
                .build();
        routingRepository.save(routing);
    }


}
