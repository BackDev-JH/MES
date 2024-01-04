package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Bom;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.repository.BomRepository;
import team_2p4p.mes.repository.ItemRepository;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BomService {

    private final BomRepository bomRepository;

    public void saveBom(Item item, Item item1, String material1Amount,  Item item2, String material2Amount,  Item item3, String material3Amount,  Item item4, String material4Amount){
        Bom bom = Bom.builder()
                .item(item)
                .item1(item1)
                .material1Amount(material1Amount)
                .item2(item2)
                .material2Amount(material2Amount)
                .item1(item3)
                .material1Amount(material3Amount)
                .item1(item4)
                .material1Amount(material4Amount)
                .build();
        System.out.println(bom);
        bomRepository.save(bom);
    }

}
