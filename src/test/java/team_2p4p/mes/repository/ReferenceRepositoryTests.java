package team_2p4p.mes.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team_2p4p.mes.entity.Facility;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Process;
import team_2p4p.mes.service.*;

import java.util.List;

@SpringBootTest
public class ReferenceRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BomService bomService;

    @Autowired
    private RoutingService routingService;
    @Test
    public void ItemInsert(){
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
    }

    @Test
    public void removeDb(){
        init();
    }
    public void init(){
        ItemInsert();
        facility();
        customer();
        enterprise();
        process();
        bom();
        routing();
    }

    @Test
    public void facility(){
    Item item = itemService.findItem("양배추");
//        System.out.println(item.getItemName());
        facilityService.saveFacility("세척탱크, 절단기", "1ton/h", 20,itemService.findItem("양배추"),itemService.findItem("흑마늘"), null, null, null);
        facilityService.saveFacility("추출기", "2000L", 20, itemService.findItem("양배추"),itemService.findItem("흑마늘"), itemService.findItem("정제수"), null, null);
        facilityService.saveFacility("추출기", "2000L", 20, itemService.findItem("양배추"),itemService.findItem("흑마늘"), itemService.findItem("정제수"), null, null);
        facilityService.saveFacility("저장탱크", "2000L", 20, itemService.findItem("석류농축액"),itemService.findItem("매실농축액"), itemService.findItem("정제수"), itemService.findItem("콜라겐"), null);
        facilityService.saveFacility("저장탱크", "2000L", 20, itemService.findItem("석류농축액"),itemService.findItem("매실농축액"), itemService.findItem("정제수"), itemService.findItem("콜라겐"), null);
        facilityService.saveFacility("파우치포장 1열", "1750ea/1h",20,itemService.findItem("포장지"),itemService.findItem("양배추 추출액"),itemService.findItem("흑마늘 추출액"),null, null);
        facilityService.saveFacility("파우치포장 2열", "1750ea/1h",20,itemService.findItem("포장지"),itemService.findItem("양배추 추출액"),itemService.findItem("흑마늘 추출액"),null, null);
        facilityService.saveFacility("스틱충진기 1열", "1500ea/1h",20,itemService.findItem("스틱"),itemService.findItem("석류젤리"),itemService.findItem("매실젤리"),null, null);
        facilityService.saveFacility("스틱충진기 2열", "1500ea/1h",20,itemService.findItem("스틱"),itemService.findItem("석류젤리"),itemService.findItem("매실젤리"),null, null);
        facilityService.saveFacility("금속검출기", "5000/1h",10,null,itemService.findItem("양배추 추출액"),itemService.findItem("흑마늘 추출액"),null, null);
        facilityService.saveFacility("포장", "200box/1h",20,itemService.findItem("박스"),itemService.findItem("양배추즙"),itemService.findItem("흑마늘즙"),itemService.findItem("석류젤리 스틱"),itemService.findItem("매실젤리 스틱"));
    }

    @Test
    public void process(){
        Item item = itemService.findItem("양배추");

        processService.saveProcess("원료계량", "원재료 계량 및 공정 투입", itemService.findItem("양배추"),itemService.findItem("흑마늘"),itemService.findItem("석류농축액") , itemService.findItem("매실농축액"),null,null,null,null,null);
        processService.saveProcess("전처리", "세척 또는 분쇄가 필요한 원재료 작업", itemService.findItem("양배추"),itemService.findItem("흑마늘"), null, null, null, facilityService.findFacility(1L),null,null,null);
        processService.saveProcess("추출(가열)", "일정한 온도와 압력으로 추출물을 추출", itemService.findItem("양배추"),itemService.findItem("흑마늘"), itemService.findItem("정제수"), null,null,facilityService.findFacility(2L),facilityService.findFacility(3L),null,null);
        processService.saveProcess("혼합 및 살균", "필요에 따라 혼합 및 저장 가능", itemService.findItem("석류농축액"),itemService.findItem("매실농축액"), itemService.findItem("정제수"), itemService.findItem("콜라겐"),null,facilityService.findFacility(4L),facilityService.findFacility(5L),null,null);
        processService.saveProcess("충진(제품생산)", "자동화 설비를 통해 포장지에 용액을 투입하고 포장함(밀봉)", itemService.findItem("포장지"),itemService.findItem("석류젤리"),itemService.findItem("매실젤리"),null,null, facilityService.findFacility(6L),facilityService.findFacility(7L),facilityService.findFacility(8L),facilityService.findFacility(9L));
        processService.saveProcess("검사", "금속 검출", itemService.findItem("양배추 추출액"),itemService.findItem("흑마늘 추출액"),itemService.findItem("석류젤리"),itemService.findItem("매실젤리"),null, facilityService.findFacility(10L),null,null,null);
        processService.saveProcess("포장", "박스 포장", itemService.findItem("포장지"),itemService.findItem("양배추즙"),itemService.findItem("흑마늘즙"),itemService.findItem("석류젤리 스틱"),itemService.findItem("매실젤리 스틱"), facilityService.findFacility(10L),null,null,null);
    }

    @Test
    public void enterprise(){
        enterpriseService.saveEnterprise(itemService.findItem("양배추"), "에이농장","2Day // 12:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItem("흑마늘"), "에이농장","2Day // 12:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItem("석류농축액"), "성욱농협","3Day // 15:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItem("매실농축액"), "성욱농협","3Day // 15:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItem("콜라겐"), "성욱농협","3Day // 15:00 이전 주문");
        enterpriseService.saveEnterprise(itemService.findItem("포장지"), "불고미포장","0 // 창고");
        enterpriseService.saveEnterprise(itemService.findItem("스틱"), "불고미포장","0 // 창고");

    }
    @Test
    public void customer(){
        customerService.saveCustomer("창원시 성산구", "쿠팡", "태영","010-0000-0000",itemService.findItem("양배추즙"), itemService.findItem("흑마늘즙"));
        customerService.saveCustomer("창원시 의창구", "11번가", "성욱","010-0000-0000",itemService.findItem("석류 젤리스틱"), itemService.findItem("매실 젤리스틱"));
    }

    @Test
    public void bom(){
        bomService.saveBom(itemService.findItem("양배추즙"), itemService.findItem("양배추"),"40ml",itemService.findItem("정제수"),"40ml",itemService.findItem("포장지"),"1ea",null,null);
        bomService.saveBom(itemService.findItem("흑마늘즙"), itemService.findItem("흑마늘"),"20ml",itemService.findItem("정제수"),"60ml",itemService.findItem("포장지"),"1ea",null,null);
        bomService.saveBom(itemService.findItem("석류 젤리스틱"), itemService.findItem("석류농축액"),"5ml",itemService.findItem("콜라겐"),"2ml",itemService.findItem("정제수"),"8ml",itemService.findItem("포장지"),"1ea");
        bomService.saveBom(itemService.findItem("매실 젤리스틱"), itemService.findItem("매실농축액"),"5ml",itemService.findItem("콜라겐"),"2ml",itemService.findItem("정제수"),"8ml",itemService.findItem("포장지"),"1ea");
    }
    @Test
    public void routing(){
        routingService.saveRouting(itemService.findItem("양배추즙"), processService.findProcess(1L),processService.findProcess(2L),processService.findProcess(3L),processService.findProcess(4L),processService.findProcess(5L),processService.findProcess(6L),processService.findProcess(7L));
        routingService.saveRouting(itemService.findItem("흑마늘즙"), processService.findProcess(1L),processService.findProcess(2L),processService.findProcess(3L),processService.findProcess(4L),processService.findProcess(5L),processService.findProcess(6L),processService.findProcess(7L));
        routingService.saveRouting(itemService.findItem("석류 젤리스틱"), processService.findProcess(1L),null,null,processService.findProcess(4L),processService.findProcess(5L),processService.findProcess(6L),processService.findProcess(7L));
        routingService.saveRouting(itemService.findItem("매실 젤리스틱"), processService.findProcess(1L),null,null,processService.findProcess(4L),processService.findProcess(5L),processService.findProcess(6L),processService.findProcess(7L));
    }

    @Test
    public void test2() {
        itemRepository.deleteByItemCode("CP");
    }

    @Test
    public void findAll() {
        List<Item> list = itemRepository.findAll();
        System.out.println(list);
    }
}
