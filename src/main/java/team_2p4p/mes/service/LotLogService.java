package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import team_2p4p.mes.dto.*;
import team_2p4p.mes.entity.*;
import team_2p4p.mes.repository.*;
import team_2p4p.mes.util.calculator.CalcOrderMaterial;
import team_2p4p.mes.util.calculator.Calculator;
import team_2p4p.mes.util.calculator.MesAll;
import team_2p4p.mes.util.process.LiquidSystem;
import team_2p4p.mes.util.process.PreProcessing;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class LotLogService {

    private final LotLogRepository lotLogRepository;
    private final ObtainService obtainService;
    private final ObtainRepository obtainRepository;
    private final CalcOrderMaterial calcOrderMaterial;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final SendRepository sendRepository;
    Calculator cal = new Calculator();

    public void recordLot(ObtainDTO dto){

        obtainService.getConfirmList();
        System.out.println("==== 로트적기");

        dto = obtainService.entityToDto(obtainRepository.findById(dto.getObtainId()).orElseThrow());
        // id로 해당 수주DTO를 찾아온다.
        MesAll mesAll = calcOrderMaterial.estimateDate(dto.getItemId(), Math.toIntExact(dto.getObtainAmount()), LocalDateTime.now());
        //calcOrderMaterial.test(dto.getItemId(), Math.toIntExact(dto.getObtainAmount()));
        //위의 주석은 실제 실행할때 풀어야됨 계속 발주를 넣어서 확인이 어려워서 일단 주석
        cal.obtain(mesAll);
        // mesAll에 db값을 꺼내서 해당 mesAll을 찾아온다.

        String dateString = "";

        LotLog lotLog;

        // 입고 로트
        //itemid inputkind 안들어갔음
        LotLogDTO orderLot = new LotLogDTO();

        orderLot.setProcess("입고");
        orderLot.setItem(itemService.findItemById(dto.getItemId()));
        String wareHouseKind = (orderLot.getItem().getItemId()==1)?"양배추(kg)":(orderLot.getItem().getItemId()==2)?"흑마늘(kg)":(orderLot.getItem().getItemId()==3)?"석류농축액(kg)":"매실농축액(kg)";
        orderLot.setInputKind(wareHouseKind);
        orderLot.setItemId(orderLot.getItem().getItemId());
        orderLot.setLotStat(false);
        orderLot.setInputTime(mesAll.getTime());
        orderLot.setOutputTime(mesAll.getInputMeasurementTime().minusMinutes(20));
        dateString = (orderLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
        orderLot.setLot("WH-"+dateString+"-"+(int)(mesAll.getAmount()));
        lotLog = dtoToEntity(orderLot);
        long warehouseId = lotLogRepository.save(lotLog).getLotLogId();




        //1 원료개량 로트
        LotLogDTO measurementLot = new LotLogDTO();
        measurementLot.setProcess("원료계량");
        measurementLot.setItem(itemService.findItemById(dto.getItemId()));
        String measurementKind = (orderLot.getItem().getItemId()==1)?"양배추(kg)":(orderLot.getItem().getItemId()==2)?"흑마늘(kg)":(orderLot.getItem().getItemId()==3)?"석류농축액(kg)":"매실농축액(kg)";
        measurementLot.setInputKind(measurementKind);
        measurementLot.setLotStat(false);
        measurementLot.setInputTime(mesAll.getInputMeasurementTime());
        measurementLot.setOutputTime(mesAll.getOutputMeasurementTime());
        dateString = (measurementLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
        measurementLot.setLot("MS-"+dateString+"-"+(int)mesAll.getMeasurementAmount());
        measurementLot.setLotPLogId1(warehouseId);
        lotLog = dtoToEntity(measurementLot);
        long measurementId = lotLogRepository.save(lotLog).getLotLogId();


        List<Long> preProcessingLotIdList = new ArrayList<>();
        //2 전처리 로트
        String preInputKind = (orderLot.getItem().getItemId()==1)?"양배추(kg)":(orderLot.getItem().getItemId()==2)?"흑마늘(kg)":(orderLot.getItem().getItemId()==3)?"석류농축액":"매실농축액";
        for (int i = 0; i < mesAll.getPreProcessingCount(); i++) {
            LotLogDTO preProcessLot = new LotLogDTO();
            preProcessLot.setProcess("전처리");
            preProcessLot.setItem(itemService.findItemById(dto.getItemId()));
            preProcessLot.setInputKind(preInputKind);
            preProcessLot.setLotStat(false);
            preProcessLot.setInputTime(mesAll.getInputPreProcessingTimeList().get(i));
            preProcessLot.setOutputTime(mesAll.getOutputPreProcessingTimeList().get(i));
            dateString = (preProcessLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
            preProcessLot.setLot("PP-"+dateString+"-"+mesAll.getPreProcessingAmountList().get(i));
            preProcessLot.setLotPLogId1(measurementId);
            lotLog = dtoToEntity(preProcessLot);
            preProcessingLotIdList.add(lotLogRepository.save(lotLog).getLotLogId());
        }

        //액체제조기1 로트
        List<Long> liquid1LotIdList = new ArrayList<>();
        String liquid1Kind = (orderLot.getItem().getItemId()==1)?"양배추 추출액(L)":(orderLot.getItem().getItemId()==2)?"흑마늘 추출액(L)":(orderLot.getItem().getItemId()==3)?"석류농축액(L)":"매실농축액(L)";

        int liquidJ1 = 0;
        //3 액체제조 시스템1 로트
        for (int i = 0; i < mesAll.getLiquidSystemCount1(); i++) {
            LotLogDTO liquid1Lot = new LotLogDTO();
            liquid1Lot.setProcess("액체제조기1");
            liquid1Lot.setItem(itemService.findItemById(dto.getItemId()));
            liquid1Lot.setInputKind(liquid1Kind);
            liquid1Lot.setLotStat(false);
            liquid1Lot.setInputTime(mesAll.getLiquidSystemInputTimeList1().get(i));
            liquid1Lot.setOutputTime(mesAll.getLiquidSystemOutputTimeList1().get(i));
            dateString = (liquid1Lot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
            liquid1Lot.setLot("L1-"+dateString+"-"+(int)mesAll.getLiquidSystemOutputAmountList1().get(i));

            if(mesAll.getItemId() == 1){

                if(mesAll.getWhereList().get(0) == 1){
                    //첫번째가 기계 1로 들어갔을때
                    liquid1Lot.setLotPLogId1(preProcessingLotIdList.get(liquidJ1));
                    liquidJ1 += 2;
                }else{
                    liquid1Lot.setLotPLogId1(preProcessingLotIdList.get(liquidJ1+1));
                    liquidJ1 += 2;
                }

            } else if (mesAll.getItemId() == 2) {
                liquid1Lot.setLotPLogId1(preProcessingLotIdList.get(liquidJ1++));
            } else {
                liquid1Lot.setLotPLogId1(measurementId);
            }

            lotLog = dtoToEntity(liquid1Lot);
            liquid1LotIdList.add(lotLogRepository.save(lotLog).getLotLogId());
        }


        //액체제조기2 로트
        List<Long> liquid2LotIdList = new ArrayList<>();
        String liquid2Kind = (orderLot.getItem().getItemId()==1)?"양배추 추출액(L)":(orderLot.getItem().getItemId()==2)?"흑마늘 추출액(L)":(orderLot.getItem().getItemId()==3)?"석류농축액(L)":"매실농축액(L)";

        int liquidJ2 = 0;
        //3 액체제조 시스템2 로트
        for (int i = 0; i < mesAll.getLiquidSystemCount2(); i++) {
            System.out.println("액체2");
            System.out.println(mesAll.getLiquidSystemCount2());
            System.out.println(mesAll.getWhereList());
            LotLogDTO liquid2Lot = new LotLogDTO();
            liquid2Lot.setProcess("액체제조기2");
            liquid2Lot.setItem(itemService.findItemById(dto.getItemId()));
            liquid2Lot.setInputKind(liquid2Kind);
            liquid2Lot.setLotStat(false);
            liquid2Lot.setInputTime(mesAll.getLiquidSystemInputTimeList2().get(i));
            liquid2Lot.setOutputTime(mesAll.getLiquidSystemOutputTimeList2().get(i));
            dateString = (liquid2Lot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
            liquid2Lot.setLot("L2-"+dateString+"-"+(int)mesAll.getLiquidSystemOutputAmountList2().get(i));

            if(mesAll.getItemId() == 1){

                if(mesAll.getWhereList().get(0) == 1){
                    liquid2Lot.setLotPLogId1(preProcessingLotIdList.get(liquidJ2+1));
                    liquidJ2 += 2;
                }else{
                    liquid2Lot.setLotPLogId1(preProcessingLotIdList.get(liquidJ2));
                    liquidJ2 += 2;
                }
            }else if(mesAll.getItemId() == 2){

                    liquid2Lot.setLotPLogId1(preProcessingLotIdList.get(liquidJ2++));

            }else{
                liquid2Lot.setLotPLogId1(measurementId);

            }

            lotLog = dtoToEntity(liquid2Lot);
            liquid2LotIdList.add(lotLogRepository.save(lotLog).getLotLogId());
        }


        List<Long> fillPouchLotIdList = new ArrayList<>();
        List<Long> fillStickLotIdList = new ArrayList<>();


        String fillKind = (orderLot.getItem().getItemId()==1)?"양배추즙(포)":(orderLot.getItem().getItemId()==2)?"흑마늘즙(포)":(orderLot.getItem().getItemId()==3)?"석류스틱(포)":"매실스틱(포)";
        int fillLiquid1Idx = 0;
        int fillLiquid2Idx = 0;
        //충진 로트
        if (mesAll.getItemId() <= 2){
            for (int i = 0; i < mesAll.getFillPouchCount(); i++) {
                LotLogDTO fillPouchLot = new LotLogDTO();
                fillPouchLot.setProcess("즙 충진기");
                fillPouchLot.setItem(itemService.findItemById(dto.getItemId()));
                fillPouchLot.setInputKind(fillKind);
                fillPouchLot.setLotStat(false);
                fillPouchLot.setInputTime(mesAll.getFillPouchInputTimeList().get(i));
                fillPouchLot.setOutputTime(mesAll.getFillPouchOutputTimeList().get(i));
                dateString = (fillPouchLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
                fillPouchLot.setLot("FP-"+dateString+"-"+(int)mesAll.getFillPouchOutputAmountList().get(i));

                if(mesAll.getWhereList().get(i) == 1){
                    fillPouchLot.setLotPLogId1(liquid1LotIdList.get(fillLiquid1Idx++));
                }else{
                    fillPouchLot.setLotPLogId1(liquid2LotIdList.get(fillLiquid2Idx++));
                }
                lotLog = dtoToEntity(fillPouchLot);
                fillPouchLotIdList.add(lotLogRepository.save(lotLog).getLotLogId());
            }
        }else{
            for (int i = 0; i < mesAll.getFillStickCount(); i++) {
                LotLogDTO fillStickLot = new LotLogDTO();
                fillStickLot.setProcess("스틱 충진기");
                fillStickLot.setItem(itemService.findItemById(dto.getItemId()));
                fillStickLot.setInputKind(fillKind);
                fillStickLot.setLotStat(false);
                fillStickLot.setInputTime(mesAll.getFillStickInputTimeList().get(i));
                fillStickLot.setOutputTime(mesAll.getFillStickOutputTimeList().get(i));
                dateString = (fillStickLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
                fillStickLot.setLot("FS-"+dateString+"-"+(int)mesAll.getFillStickOutputAmountList().get(i));
                if(mesAll.getWhereList().get(i) == 1){
                    fillStickLot.setLotPLogId1(liquid1LotIdList.get(fillLiquid1Idx++));

                }else{
                    fillStickLot.setLotPLogId1(liquid2LotIdList.get(fillLiquid2Idx++));
                }
                lotLog = dtoToEntity(fillStickLot);
                fillStickLotIdList.add(lotLogRepository.save(lotLog).getLotLogId());
           }
        }



        List<Long> checkLotIdList = new ArrayList<>();
        String checkKind = (orderLot.getItem().getItemId()==1)?"양배추즙(포)":(orderLot.getItem().getItemId()==2)?"흑마늘즙(포)":(orderLot.getItem().getItemId()==3)?"석류스틱(포)":"매실스틱(포)";

        //  검사로트
        for (int i = 0; i < mesAll.getCheckCount(); i++) {

            LotLogDTO checkLot = new LotLogDTO();
            checkLot.setProcess("검사");
            checkLot.setItem(itemService.findItemById(dto.getItemId()));
            checkLot.setInputKind(checkKind);
            checkLot.setLotStat(false);
            checkLot.setInputTime(mesAll.getCheckInputTimeList().get(i));
            checkLot.setOutputTime(mesAll.getCheckOutputTimeList().get(i));
            dateString = (checkLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
            checkLot.setLot("CK-"+dateString+"-"+(int)mesAll.getCheckOutputAmountList().get(i));
            if(mesAll.getItemId()<=2){
                //파우치 부모 로트 가져오기
                checkLot.setLotPLogId1(fillPouchLotIdList.get(i));
            }else{
                //stick 부모 로트 가져오기
                checkLot.setLotPLogId1(fillStickLotIdList.get(i));
            }

            lotLog = dtoToEntity(checkLot);
            checkLotIdList.add(lotLogRepository.save(lotLog).getLotLogId());
        }


        List<Long> packingLotIdList = new ArrayList<>();
        String packingKind = (orderLot.getItem().getItemId()==1)?"양배추즙(box) ":(orderLot.getItem().getItemId()==2)?"흑마늘즙(box)":(orderLot.getItem().getItemId()==3)?"석류스틱(box)":"매실스틱(box)";
        int packingAllAmount = 0;
        // 포장로트
        for (int i = 0; i < mesAll.getPackingCount(); i++) {
            packingAllAmount += (int)mesAll.getPackingOutputAmountList().get(i);
            LotLogDTO packingLot = new LotLogDTO();
            packingLot.setProcess("포장");
            packingLot.setItem(itemService.findItemById(dto.getItemId()));
            packingLot.setInputKind(packingKind);
            packingLot.setLotStat(false);
            packingLot.setInputTime(mesAll.getPackingInputTimeList().get(i));
            packingLot.setOutputTime(mesAll.getPackingOutputTimeList().get(i));
            dateString = (packingLot.getOutputTime().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))).substring(2);
            packingLot.setLot("PK-"+dateString+"-"+(int)mesAll.getPackingOutputAmountList().get(i));
            packingLot.setLotPLogId1(checkLotIdList.get(i));
            lotLog = dtoToEntity(packingLot);
            packingLotIdList.add(lotLogRepository.save(lotLog).getLotLogId());

            // product 등록
            ProductDto productDto = new ProductDto();
            productDto.setProductStock(Long.valueOf(mesAll.getPackingOutputAmountList().get(i)));
            productDto.setItemId(mesAll.getItemId());
            productDto.setExportStat(false);
            productDto.setMakeDate(mesAll.getPackingOutputTimeList().get(i));

            Product product = Product.builder()
                    .item(itemRepository.findById(mesAll.getItemId()).orElseThrow())
                    .productStock(productDto.getProductStock())
                    .makeDate(productDto.getMakeDate())
                    .exportStat(productDto.isExportStat())
                    .lotLogId(lotLogRepository.findById(packingLotIdList.get(i)).orElseThrow())
                    .build();
            productRepository.save(product);

        }

        // send 등록
        SendDTO sendDTO = new SendDTO();
        sendDTO.setItemId(mesAll.getItemId());
        sendDTO.setObtainDTO(dto);
        sendDTO.setItem(itemService.findItemById(sendDTO.getItemId()));
        sendDTO.setSendStat(false);
        sendDTO.setSendProductNum(dto.getObtainAmount());

        Send send = Send.builder()
                .item(sendDTO.getItem())
                .obtain(obtainService.dtoToEntity(sendDTO.getObtainDTO()))
                .sendStat(sendDTO.isSendStat())
                .sendProductNum(sendDTO.getSendProductNum())
                .build();

        sendRepository.save(send);

        int productIdx = packingAllAmount - Math.toIntExact(dto.getObtainAmount());

    }


    public List<String> findPLot(LotLogDTO lotLogDTO){
        List<String> pList = new ArrayList<>();
        long id = lotLogDTO.getLotLogId();

        LotLog lotLog = lotLogRepository.findById(id).orElseThrow();
        pList.add(lotLog.getLot());

        while(true){

            if(lotLog.getLotPLogId1() == 0){
                break;
            }
            lotLog = lotLogRepository.findById(lotLog.getLotPLogId1()).orElseThrow();
            pList.add(lotLog.getLot());
        }

        return pList;
    }




    public LotLog dtoToEntity(LotLogDTO dto){

        LotLog entity = LotLog.builder()
                .lotLogId(dto.getLotLogId())
                .process(dto.getProcess())
                .item(dto.getItem())
                .inputKind(dto.getInputKind())
                .lotStat(dto.isLotStat())
                .inputTime(dto.getInputTime())
                .outputTime(dto.getOutputTime())
                .lot(dto.getLot())
                .lotPLogId1(dto.getLotPLogId1())
                .lotPLogId2(dto.getLotPLogId2())
                .build();

        return entity;
    }

    public LotLogDTO entityToDto(LotLog entity){

        LotLogDTO dto = new LotLogDTO();
        dto.setLotLogId(entity.getLotLogId());
        dto.setProcess(entity.getProcess()); //객체에서 itemId 를 빼야됨
        dto.setItem(entity.getItem());
        dto.setInputKind(entity.getInputKind());
        dto.setLotStat(entity.isLotStat());
        dto.setInputTime(entity.getInputTime());
        dto.setOutputTime(entity.getOutputTime());
        dto.setLot(entity.getLot());
        dto.setLotPLogId1(entity.getLotPLogId1());
        dto.setLotPLogId2(entity.getLotPLogId2());

        return dto;
    }
}


