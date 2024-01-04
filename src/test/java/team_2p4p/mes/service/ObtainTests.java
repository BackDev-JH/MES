package team_2p4p.mes.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team_2p4p.mes.dto.ObtainDTO;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.repository.CustomerRepository;
import team_2p4p.mes.repository.ItemRepository;


import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
public class ObtainTests {

    @Autowired
    private ObtainService obtainService;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ItemRepository itemRepository;


// 양배추즙 흑마늘즙 석류 젤리스틱 매실 젤리스틱
    @Test void regObtainTest(){

        ObtainDTO obtainDTO = new ObtainDTO();
        obtainDTO.setItemName("석류 젤리스틱");
        obtainDTO.setObtainAmount(3000L);
        obtainDTO.setCustomerRequestDate(LocalDateTime.now().plusDays(20));

        obtainService.regObtain(obtainDTO);

    }
}
