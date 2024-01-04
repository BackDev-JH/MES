package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Facility;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Process;
import team_2p4p.mes.entity.Routing;
import team_2p4p.mes.repository.ProcessRepository;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;

    public void saveProcess(String name, String content,Item item1, Item item2, Item item3, Item item4,Item item5, Facility facility1, Facility facility2, Facility facility3, Facility facility4){

        Process process = Process.builder()
                .processName(name)
                .processContent(content)
                .item1(item1)
                .item2(item2)
                .item3(item3)
                .item4(item4)
                .item5(item5)
                .facility1(facility1)
                .facility2(facility2)
                .facility3(facility3)
                .facility4(facility4)
                .build();
        System.out.println("프로세스: " +  process);

        processRepository.save(process);
    }

    public Process findProcess(Long id){
        return  processRepository.findById(id)
                .orElseThrow(() ->new IllegalArgumentException("공정이 존재하지 않습니다"));
    }

}
