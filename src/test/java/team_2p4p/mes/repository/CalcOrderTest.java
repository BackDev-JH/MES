package team_2p4p.mes.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Product;
import team_2p4p.mes.service.*;
import team_2p4p.mes.util.calculator.CalcOrderMaterial;
import team_2p4p.mes.util.calculator.MesAll;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@TestPropertySource(locations = "classpath:application-test.properties")
public class CalcOrderTest {

    static MesAll mesAll;

    @Autowired
    private CalcOrderMaterial calcOrderMaterial;


    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private ItemService itemService;
    @Test
    public void findAll() {
        itemService.saveItem("CP-001", "양배추즙", "완제품");
        itemService.saveItem("CP-002", "흑마늘즙", "완제품" );
        itemService.saveItem("CP-003", "석류 젤리스틱", "완제품" );
        itemService.saveItem("CP-004", "매실 젤리스틱", "완제품");

        itemService.saveItem("SP-001", "양배추 추출액", "반제품");
        itemService.saveItem("SP-002", "흑마늘 추출액", "반제품");
        itemService.saveItem("SP-003", "석류젤리", "반제품");
        itemService.saveItem("SP-004", "매실젤리", "반제품");

        itemService.saveItem("OP-001", "양배추", "원자재");
        itemService.saveItem("OP-002", "흑마늘", "원자재");
        itemService.saveItem("OP-003", "석류농축액", "원자재");
        itemService.saveItem("OP-004", "매실농축액", "원자재");
        itemService.saveItem("OP-005", "정제수", "원자재");
        itemService.saveItem("OP-006", "콜라겐", "원자재");
        itemService.saveItem("OP-007", "포장지", "원자재");
        itemService.saveItem("OP-008", "스틱", "원자재");
        itemService.saveItem("OP-009", "박스", "원자재");

        enterpriseService.saveEnterprise(itemService.findItemById(9L), "에이농장","2Day // 12:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItemById(10L), "에이농장","2Day // 12:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItemById(11L), "성욱농협","3Day // 15:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItemById(12L), "성욱농협","3Day // 15:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItemById(14L), "성욱농협","3Day // 15:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItemById(15L), "불고미포장","0 // 창고");
        enterpriseService.saveEnterprise(itemService.findItemById(16L), "불고미포장","0 // 창고");
        enterpriseService.saveEnterprise(itemService.findItemById(17L), "불고미포장","0 // 창고");


        Product product = new Product();
        product.setProductId(1L);
        product.setItem(itemService.findItemById(1L));
        product.setProductStock(1000L);
        product.setMakeDate(LocalDateTime.now());
        product.setExportStat(false);
        productRepository.save(product);

        calcOrderMaterial.test(1L, 1500);
        calcOrderMaterial.test(2L, 1320);
        calcOrderMaterial.test(3L, 1320);
        calcOrderMaterial.test(2L, 1600);
        calcOrderMaterial.test(4L, 1325);
        calcOrderMaterial.test(1L, 1250);
        calcOrderMaterial.test(1L, 2500);
        calcOrderMaterial.test(3L, 160);
        calcOrderMaterial.test(1L, 1250);
//
        calcOrderMaterial.morningOrderMaterialSchedule();
        calcOrderMaterial.afternoonOrderMaterialSchedule();

        calcOrderMaterial.inputMaterialSchedule();
        MesAll mesAll = new MesAll();
        mesAll.setItemId(9L);
        mesAll.setOrderId(1L);

    }


    @Test
    public void materialStatTest() {
        MesAll mesAll = new MesAll();
        mesAll.setItemId(9L);
        mesAll.setOrderId(1L);
        calcOrderMaterial.useMaterial(mesAll, 3000L);
    }
}
