package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Facility;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Process;
import team_2p4p.mes.repository.FacilityRepository;
import team_2p4p.mes.repository.ItemRepository;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityService {

    @Autowired
    private final FacilityRepository facilityRepository;

    public void saveFacility(String name, String capa, int ready, Item item1, Item item2, Item item3, Item item4, Item item5){

        Facility facility = Facility.builder()
                .facilityName(name)
                .facilityCapa(capa)
                .processReadytime(ready)
                .item1(item1)
                .item2(item2)
                .item3(item3)
                .item4(item4)
                .item4(item5)
                .build();
        System.out.println(facility);

        facilityRepository.save(facility);
    }

    public Facility findFacilityName(String name){
        return  facilityRepository.findByFacilityName(name);
    }

    public Facility findFacility(Long id){
        return  facilityRepository.findById(id)
                 .orElseThrow(() ->new IllegalArgumentException("설비가 존재하지 않습니다"));
    }


}
