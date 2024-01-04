package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team_2p4p.mes.dto.ObtainDTO;
import team_2p4p.mes.dto.SearchDTO;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.Obtain;
import team_2p4p.mes.entity.ProductionManagement;
import team_2p4p.mes.repository.CustomerRepository;
import team_2p4p.mes.repository.ItemRepository;
import team_2p4p.mes.repository.ObtainRepository;
import team_2p4p.mes.util.calculator.CalcOrderMaterial;
import team_2p4p.mes.util.calculator.Calculator;
import team_2p4p.mes.util.calculator.MesAll;
import team_2p4p.mes.util.process.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class ObtainService {

    private final CalcOrderMaterial calcOrderMaterial;

    private final ObtainRepository obtainRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    Calculator cal = new Calculator();



    public void getConfirmList(){

        List<Obtain> list = obtainRepository.findByObtainStat(true);
        List<ObtainDTO> dtoList = new ArrayList<>();

        System.out.println("확정리스트 : " + list);
        Factory factory = Factory.getInstance();

        factory.clearList();

        for(int i = 0; i < list.size(); i++){

            dtoList.add(entityToDto(list.get(i)));
            MesAll mesAll = calcOrderMaterial.estimateDate(dtoList.get(i).getItemId(), Math.toIntExact(dtoList.get(i).getObtainAmount()), dtoList.get(i).getObtainStatDate());
            cal.obtain(mesAll);

            factory.getMeasurement().getConfirmList().add(mesAll);
            factory.getPreProcessing().getConfirmList().add(mesAll);
            factory.getLiquidSystem().getConfirmList().add(mesAll);
            factory.getFillPouchProcessing().getConfirmList().add(mesAll);
            factory.getFillStickProcessing().getConfirmList().add(mesAll);
            factory.getCheckProcessing().getConfirmList().add(mesAll);
            factory.getPacking().getConfirmList().add(mesAll);

            System.out.println("컨펌리스트 mesAll");
            System.out.println(mesAll);
        }
    }

    public void regObtain(ObtainDTO dto){
        Item item = itemService.findItem(dto.getItemName());
        getConfirmList();

        //제품명 저장
        System.out.println(item);
        dto.setItemId(item.getItemId());
        dto.setItem(item);
        System.out.println("수량:"+ dto.getObtainAmount());

        //고객사 저장
        Customer customer = customerService.customerFindbyItem(item);
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomer(customer);

        //지금 시각으로 수주일 저장
        LocalDateTime obtainDate = LocalDateTime.now();
        dto.setObtainDate(obtainDate);

        //수주 상태 저장
        dto.setObtainStat(false);

        //고객사 요청일 저장

        //예상납기일 계산
        MesAll obtainInfo = calcOrderMaterial.estimateDate(dto.getItemId(), Math.toIntExact(dto.getObtainAmount()), obtainDate);
        cal.obtain(obtainInfo);
        //예상납기일 저장
        dto.setExpectDate(obtainInfo.getEstimateDate());

        //dto를 엔티티로 변환
        System.out.println("dto 값:" + dto.createObtain());
        Obtain obtain = dto.createObtain();

        obtainRepository.save(obtain);
    }

    //여기서 부터 하면됨 db에서 값 들고와서 다시 계산하고 수주확정시키기
    public void confirmObtain(ObtainDTO dto){

        //수주확정시 다시계산해야 되고 dto에 확정시간 넣어줘야함
        getConfirmList();

        dto = entityToDto(obtainRepository.findById(dto.getObtainId()).orElseThrow());
        MesAll mesAll = calcOrderMaterial.estimateDate(dto.getItemId(), Math.toIntExact(dto.getObtainAmount()), LocalDateTime.now());
        cal.obtain(mesAll);


        dto.setExpectDate(mesAll.getEstimateDate());
        Obtain entity = dtoToEntity(dto);
        entity.updateStat();
        entity.updateConfirmTime();

        cal.confirmObtain(mesAll);
        obtainRepository.save(entity);
        calcOrderMaterial.test(dto.getItemId(), Math.toIntExact(dto.getObtainAmount()));

    }


    public void confirmAfterObtainCal(){
        // 확정후
        // 1. 수주계획의 시간들이 전부 새로 계산이 된다.
        // 2. mesAll.setTime()을 mesAll의 출하가능시간이 기준이된다.
        List<Obtain> entityList = obtainRepository.findByObtainStat(false); //false인 entity 리스트를 불러옴
        List<ObtainDTO> dtoList = new ArrayList<>();
        for(int i = 0; i < entityList.size(); i++){
            dtoList.add(entityToDto(entityList.get(i))); // entity를 dto로 변환 후 dto List에 넣어줌
            MesAll mesAll = calcOrderMaterial.estimateDate(dtoList.get(i).getItemId(), Math.toIntExact(dtoList.get(i).getObtainAmount()), LocalDateTime.now());
            cal.obtain(mesAll);
            dtoList.get(i).setExpectDate(mesAll.getEstimateDate());
            obtainRepository.save(dtoToEntity(dtoList.get(i)));

        }




    }

    public Obtain dtoToEntity(ObtainDTO dto){

        Obtain entity = Obtain.builder()
                .obtainId(dto.getObtainId())
                .item(itemRepository.findById(dto.getItemId()).orElseThrow())
                .customer(customerRepository.findById(dto.getCustomerId()).orElseThrow())
                .obtainAmount(dto.getObtainAmount())
                .obtainDate(dto.getObtainDate())
                .customerRequestDate(dto.getCustomerRequestDate())
                .expectDate(dto.getExpectDate())
                .obtainStat(dto.isObtainStat())
                .build();

        return entity;
    }

    public ObtainDTO entityToDto(Obtain entity){

        ObtainDTO dto = new ObtainDTO();
        dto.setObtainId(entity.getObtainId());
        dto.setItemId(entity.getItem().getItemId()); //객체에서 itemId 를 빼야됨
        dto.setCustomerId(entity.getCustomer().getCustomerId());
        dto.setObtainAmount(entity.getObtainAmount());
        dto.setObtainDate(entity.getObtainDate());
        dto.setCustomerRequestDate(entity.getCustomerRequestDate());
        dto.setExpectDate(entity.getExpectDate());
        dto.setObtainStat(entity.isObtainStat());
        dto.setObtainStatDate(entity.getObtainStatDate());

        return dto;
    }

    public List<Obtain> getObtainList(SearchDTO searchDTO){
        return obtainRepository.getObtainList(searchDTO);
    }

    public void deleteObtainByObtainId(Long obtainNum){
        obtainRepository.deleteById(obtainNum);
    }

    public List<Obtain> classification0(){
        List<Obtain> obtains = obtainRepository.findAll();

        List<Obtain> filteredList = obtains.stream()
                .filter(obtain -> !obtain.isObtainStat())
                .collect(Collectors.toList());
        return filteredList;
    }

    public List<Obtain> classification(){
        List<Obtain> obtains = obtainRepository.findAll();

        List<Obtain> filteredList = obtains.stream()
                .filter(obtain -> obtain.isObtainStat())
                .collect(Collectors.toList());
        return filteredList;
    }


}
