package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import team_2p4p.mes.dto.ObtainDTO;
import team_2p4p.mes.dto.ProductDto;
import team_2p4p.mes.entity.*;
import team_2p4p.mes.repository.ItemRepository;
import team_2p4p.mes.repository.ObtainRepository;
import team_2p4p.mes.repository.ProductRepository;
import team_2p4p.mes.repository.ProductionManagementRepository;
import team_2p4p.mes.util.calculator.CalcOrderMaterial;
import team_2p4p.mes.util.calculator.Calculator;
import team_2p4p.mes.util.calculator.MesAll;
import team_2p4p.mes.util.process.Factory;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ProductionManagementService {

    private final ProductionManagementRepository productionManagementRepository;
    private final ObtainService obtainService;
    private final ObtainRepository obtainRepository;
    private final CalcOrderMaterial calcOrderMaterial;
    private final LotLogService lotLogService;
    private final ItemRepository itemRepository;
    private ProductRepository productRepository;

    Calculator cal = new Calculator();
    public void productionManagement(ObtainDTO dto) {

        //등록 확정되면 yn 을 바꿈
        //1 ObtainDTO에서 받은 id로 mesAll을 다시 구해온다.
        //2 그 mesAll을 계산을 다시한다.
        //3 그 값을 dto에 넣는다
        //4 그 값을 entity로 변환한다.

        // 1 원료개량
        // 2 전처리
        // 3 액체제조시스템 1
        // 4 액체제조시스템 2
        // 5 파우치 충진기
        // 6 Stick 충진기
        // 7 검사
        // 8 포장

        obtainService.getConfirmList();

        System.out.println(Factory.getInstance());
        System.out.println("==== 생산계획");
        dto = obtainService.entityToDto(obtainRepository.findById(dto.getObtainId()).orElseThrow());
        // id로 해당 수주DTO를 찾아온다.
        MesAll mesAll = calcOrderMaterial.estimateDate(dto.getItemId(), Math.toIntExact(dto.getObtainAmount()), LocalDateTime.now());
        cal.obtain(mesAll);
        // mesAll에 db값을 꺼내서 해당 mesAll을 찾아온다.
        Obtain obtain = obtainService.dtoToEntity(dto);


        //1 원료개량
        ProductionManagement measurementPlan = ProductionManagement.builder()
                .obtain(obtain)
                .processAmount((long) mesAll.getMeasurementAmount())
                .processStartTime(mesAll.getInputMeasurementTime())
                .processFinishTime(mesAll.getOutputMeasurementTime())
                .process("원료개량")
                .build();

        productionManagementRepository.save(measurementPlan);


        //2 전처리
        for (int i = 0; i < mesAll.getPreProcessingCount(); i++) {
            ProductionManagement preProcessPlan = ProductionManagement.builder()
                    .obtain(obtain)
                    .processAmount((long) mesAll.getPreProcessingAmountList().get(i))
                    .processStartTime(mesAll.getInputPreProcessingTimeList().get(i))
                    .processFinishTime(mesAll.getOutputPreProcessingTimeList().get(i))
                    .process("전처리")
                    .build();
            productionManagementRepository.save(preProcessPlan);
        }


        // 3 액체제조 시스템1
        for (int i = 0; i < mesAll.getLiquidSystemCount1(); i++) {
            ProductionManagement liquidProcess1Plan = ProductionManagement.builder()
                    .obtain(obtain)
                    .processAmount((long) mesAll.getLiquidSystemInputAmountList1().get(i))
                    .processStartTime(mesAll.getLiquidSystemInputTimeList1().get(i))
                    .processFinishTime(mesAll.getLiquidSystemOutputTimeList1().get(i))
                    .process("액체제조 시스템 1")
                    .build();
            productionManagementRepository.save(liquidProcess1Plan);
        }


        // 4 액체제조 시스템2
        for (int i = 0; i < mesAll.getLiquidSystemCount2(); i++) {
            ProductionManagement liquidProcess2Plan = ProductionManagement.builder()
                    .obtain(obtain)
                    .processAmount((long) mesAll.getLiquidSystemInputAmountList2().get(i))
                    .processStartTime(mesAll.getLiquidSystemInputTimeList2().get(i))
                    .processFinishTime(mesAll.getLiquidSystemOutputTimeList2().get(i))
                    .process("액체제조 시스템 2")
                    .build();
            productionManagementRepository.save(liquidProcess2Plan);
        }

        // 5,6 충진
        if (mesAll.getItemId() <= 2) {
            for (int i = 0; i < mesAll.getFillPouchCount(); i++) {
                ProductionManagement fillPouchProcessPlan = ProductionManagement.builder()
                        .obtain(obtain)
                        .processAmount((long) mesAll.getFillPouchInputAmountList().get(i))
                        .processStartTime(mesAll.getFillPouchInputTimeList().get(i))
                        .processFinishTime(mesAll.getFillPouchOutputTimeList().get(i))
                        .process("즙 충진기")
                        .build();
                productionManagementRepository.save(fillPouchProcessPlan);
            }
        } else {
            for (int i = 0; i < mesAll.getFillStickCount(); i++) {
                ProductionManagement fillStickProcessPlan = ProductionManagement.builder()
                        .obtain(obtain)
                        .processAmount((long) mesAll.getFillStickInputAmountList().get(i))
                        .processStartTime(mesAll.getFillStickInputTimeList().get(i))
                        .processFinishTime(mesAll.getFillStickOutputTimeList().get(i))
                        .process("스틱 충진기")
                        .build();
                productionManagementRepository.save(fillStickProcessPlan);

            }
        }


            // 7 검사
            for (int i = 0; i < mesAll.getCheckCount(); i++) {
                ProductionManagement checkProcessPlan = ProductionManagement.builder()
                        .obtain(obtain)
                        .processAmount((long) mesAll.getCheckInputAmountList().get(i))
                        .processStartTime(mesAll.getCheckInputTimeList().get(i))
                        .processFinishTime(mesAll.getCheckOutputTimeList().get(i))
                        .process("검사")
                        .build();
                productionManagementRepository.save(checkProcessPlan);

            }


            // 8 포장 완제품에도 등록
            for (int i = 0; i < mesAll.getPackingCount(); i++) {
                ProductionManagement packingProcessPlan = ProductionManagement.builder()
                        .obtain(obtain)
                        .processAmount((long) mesAll.getPackingInputAmountList().get(i))
                        .processStartTime(mesAll.getPackingInputTimeList().get(i))
                        .processFinishTime(mesAll.getPackingOutputTimeList().get(i))
                        .process("포장")
                        .build();
                productionManagementRepository.save(packingProcessPlan);

            }


    }


    public void confirmAndAddProductionManagement(ObtainDTO dto){
        productionManagement(dto);
        lotLogService.recordLot(dto);
        obtainService.confirmObtain(dto); //생산일정을 짜고 t/f 등록
        obtainService.confirmAfterObtainCal();

    }

    public List<ProductionManagement> classification0(){
        List<ProductionManagement> productionManagementList = productionManagementRepository.findAll();

        List<ProductionManagement> filteredList = productionManagementList.stream()
                .filter(productionManagement -> productionManagement.getProcessStat() == 0)
                .collect(Collectors.toList());
    return filteredList;
    }

    public List<ProductionManagement> classification1(){
        List<ProductionManagement> productionManagementList = productionManagementRepository.findAll();

        List<ProductionManagement> filteredList = productionManagementList.stream()
                .filter(productionManagement -> productionManagement.getProcessStat() == 1)
                .collect(Collectors.toList());
        return filteredList;
    }

    public List<ProductionManagement> classification2(){
        List<ProductionManagement> productionManagementList = productionManagementRepository.findAll();

            List<ProductionManagement> filteredList = productionManagementList.stream()
                    .filter(productionManagement -> productionManagement.getProcessStat() == 2)
                    .collect(Collectors.toList());
            return filteredList;
        }

}
