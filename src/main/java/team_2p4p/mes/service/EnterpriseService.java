package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Enterprise;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.repository.EnterpriseRepository;
import team_2p4p.mes.repository.ItemRepository;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnterpriseService {

    @Autowired
    private final EnterpriseRepository enterpriseRepository;

    public void saveEnterprise(Item item, String name, String deliveryTime){
        Enterprise enterprise = Enterprise.builder()
                .item(item)
                .enterpriseName(name)
                .deliveryTime(deliveryTime)
                .build();
        System.out.println(enterprise);
        enterpriseRepository.save(enterprise);
    }


}
