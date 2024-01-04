package team_2p4p.mes.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import team_2p4p.mes.dto.ObtainDTO;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Obtain;
import team_2p4p.mes.service.CustomerService;
import team_2p4p.mes.service.ObtainService;

import java.time.LocalDateTime;

@SpringBootTest
@Log4j2
public class ObtainRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private ObtainRepository obtainRepository;

    @Autowired
    private ObtainService obtainService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

//    @Test
//    public  void test() {
//        log.info("hello");
//    }

    @Test
    void test2(){

        ObtainDTO obtainDTO = new ObtainDTO();

        obtainDTO.setItemName("양배추즙");
        obtainDTO.setObtainAmount(3000L);
        obtainDTO.setCustomerRequestDate(LocalDateTime.now().plusDays(20));
        obtainService.regObtain(obtainDTO);

    }


}
