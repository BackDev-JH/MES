package com.kim.service;

import com.kim.dto.ProceDto;
import com.kim.dto.ProductionDto;
import com.kim.entity.Mat;
import com.kim.entity.Orders;
import com.kim.entity.Proce;
import com.kim.entity.Production;
import com.kim.repository.ProceRepository;
import com.kim.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductionService {


    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Autowired
    private final ProductionRepository productionRepository;
    @Autowired
    private final ProceRepository proceRepository;
    @Autowired
    private final ProceService proceService;

    public Production saveProduction(Production production) { return productionRepository.save(production); }
    //작업 시작시간에 제약사항 주는 메소드
    public static LocalDateTime workTimeStart(LocalDateTime startTime) {

        LocalTime morningStart = LocalTime.of(9, 0);
        LocalTime morningEnd = LocalTime.of(12, 0);

        LocalTime lunchStart = LocalTime.of(12, 0);
        LocalTime lunchEnd = LocalTime.of(13, 0);

        LocalTime afternoonStart = LocalTime.of(13, 0);
        LocalTime afternoonEnd = LocalTime.of(18, 0);


        if (startTime.getDayOfWeek() == DayOfWeek.FRIDAY && startTime.toLocalTime().isAfter(LocalTime.of(18, 0))) {
            startTime = startTime.plusDays(3).with(LocalTime.of(9, 0)); // 월요일 아침 9시

        } else {
            if (startTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
                startTime = startTime.plusDays(2).with(LocalTime.of(9, 0)); // 토요일 오후 1시부터 작업 시작
            } else if (startTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
                startTime = startTime.plusDays(1).with(LocalTime.of(9, 0)); // 일요일 오후 1시부터 작업 시작
            } else {

                if (startTime.toLocalTime().isBefore(morningStart) ) {
                    startTime = startTime.with(morningStart); // 오전 9시부터 작업 시작
                } else if(startTime.toLocalTime().isAfter(afternoonEnd)){
                    startTime = startTime.plusDays(1).with(morningStart);
                } else if (startTime.toLocalTime().isAfter(lunchStart) && startTime.toLocalTime().isBefore(afternoonStart)) {
                    startTime = startTime.with(afternoonStart); // 오후 1시부터 작업 시작
                } else if (startTime.toLocalTime().isAfter(afternoonEnd)) {
                    if (startTime.getDayOfWeek() == DayOfWeek.FRIDAY) {
                        startTime = startTime.plusDays(3).with(LocalTime.of(9, 0)); // 월요일 아침 9시
                    } else {
                        startTime = startTime.plusDays(1).with(morningStart); // 다음 날 오전 9시부터 작업 시작
                    }
                }
            }
        }

        return startTime;
    }


    //양배추 계산
    public LocalDateTime calCab(int box, LocalDateTime startTime, Long id) throws Exception {

        int totalpau = 30 * box;
//        List<Mat> mat = matService.findMat("양배추");
        double remainCab = 0;
//        for(int i = 0; i < mat.size(); i++){
//            remainCab += mat.get(i).getMatNum();
//        }



        boolean isStick = false;

        //필요한 즙 무게
        double cabageJu = Math.ceil(totalpau * 80);

        //수율을 반영하기위해 투입되는 양배추와 물을 합친 무게
        double requiredCabWat = Math.ceil(totalpau * 80 / 8 * 10);

        //실제 필요한 양배추 양
        double requiredCab = requiredCabWat / 2;

        //양배추의 최소 주문수량 = 1,000,000g = 1ton
        double offerCab = Math.ceil(requiredCab / 1000000) * 1000000;

        // 잔여 양배추 양
        // 재고로 저장
        remainCab = offerCab - requiredCab + remainCab;

        System.out.println();
        System.out.println("주문 박스 수량: " + box + " box");
        System.out.println("필요한 양배추 파우치 총량: " + totalpau + " ea");
        System.out.println("양배추 추출액 필요량: " + cabageJu + " ml");
        System.out.println("80%의 수율을 반영한 필요투입량: " + requiredCabWat / 1000 + " kg");
        System.out.println("필요한 양배추양: " + requiredCab / 1000 + " kg");
        System.out.println("발주해야하는 양배추양: " + offerCab / 1000 + " kg");
        System.out.println("잔여 양배추양 : " + remainCab / 1000 + " kg");
        System.out.println();



        //계량 저장
        ProductionDto productionDto1 = new ProductionDto();
        productionDto1.setOrderId(id);
        productionDto1.setStartTime(startTime);
        productionDto1.setMatId(1L); //추후 수정
        productionDto1.setLotId("계량-"+id);

        LocalDateTime weightEnd = calWeigh(startTime);

        productionDto1.setEndTime(weightEnd);
        productionDto1.setMatInput((int)requiredCab);
        Production production1 = Production.createProduction(productionDto1);
        saveProduction(production1);

        ProceDto proceDto1 = proceService.getProceDtl(1L);
        proceDto1.setEndTime(weightEnd);
        proceService.updateProce(proceDto1);


        //세척 저장

        ProductionDto productionDto2 = new ProductionDto();
        productionDto2.setOrderId(id);
        productionDto2.setStartTime(workTimeStart(weightEnd));
        if(productionDto2.getStartTime().isBefore(proceService.getProceDtl(2L).getEndTime())){
            productionDto2.setStartTime(workTimeStart(proceService.getProceDtl(2L).getEndTime()));
        }

        productionDto2.setMatId(1L); //추후 수정
        productionDto2.setLotId("세척-"+id);

        LocalDateTime washEnd = calWash(weightEnd, requiredCab);

        productionDto2.setEndTime(washEnd);
        productionDto2.setMatInput((int)requiredCab);
        Production production2 = Production.createProduction(productionDto2);
        saveProduction(production2);

        ProceDto proceDto2 = proceService.getProceDtl(2L);
        proceDto2.setEndTime(washEnd);
        proceService.updateProce(proceDto2);



        //추출 저장

        ProductionDto productionDto3 = new ProductionDto();
        productionDto3.setOrderId(id);
        productionDto3.setStartTime(workTimeStart(washEnd));
        if(productionDto3.getStartTime().isBefore(proceService.getProceDtl(3L).getEndTime())){
            productionDto3.setStartTime(workTimeStart(proceService.getProceDtl(3L).getEndTime()));
        }
        productionDto3.setMatId(1L); //추후 수정
        productionDto3.setLotId("추출-"+id);

        LocalDateTime extractEnd = calExtract(washEnd, requiredCab);

        productionDto3.setEndTime(extractEnd);
        productionDto3.setMatInput((int)requiredCab);
        Production production3 = Production.createProduction(productionDto3);
        saveProduction(production3);

        ProceDto proceDto3 = proceService.getProceDtl(3L);
        proceDto3.setEndTime(extractEnd);
        proceService.updateProce(proceDto3);

        //혼합 저장
        ProductionDto productionDto4 = new ProductionDto();
        productionDto4.setOrderId(id);
        productionDto4.setStartTime(workTimeStart(extractEnd));
        if(productionDto4.getStartTime().isBefore(proceService.getProceDtl(4L).getEndTime())){
            productionDto4.setStartTime(workTimeStart(proceService.getProceDtl(4L).getEndTime()));
        }
        productionDto4.setMatId(1L); //추후 수정
        productionDto4.setLotId("혼합-"+id);

        LocalDateTime blendEnd = calBlend(extractEnd, isStick);

        productionDto4.setEndTime(blendEnd);
        productionDto4.setMatInput((int)requiredCab);
        Production production4 = Production.createProduction(productionDto4);
        saveProduction(production4);

        ProceDto proceDto4 = proceService.getProceDtl(4L);
        proceDto4.setEndTime(blendEnd);
        proceService.updateProce(proceDto4);


        //충진 저장

        ProductionDto productionDto5 = new ProductionDto();
        productionDto5.setOrderId(id);
        productionDto5.setStartTime(workTimeStart(extractEnd));
        if(productionDto5.getStartTime().isBefore(proceService.getProceDtl(5L).getEndTime())){
            productionDto5.setStartTime(workTimeStart(proceService.getProceDtl(5L).getEndTime()));
        }
        productionDto5.setMatId(1L); //추후 수정
        productionDto5.setLotId("충진-"+id);

        LocalDateTime packingEnd = calPacking(blendEnd, totalpau, isStick);

        productionDto5.setEndTime(packingEnd);
        productionDto5.setMatInput(totalpau);
        Production production5 = Production.createProduction(productionDto5);
        saveProduction(production5);

        ProceDto proceDto5 = proceService.getProceDtl(5L);
        proceDto5.setEndTime(packingEnd);
        proceService.updateProce(proceDto5);


        //검사 저장

        ProductionDto productionDto6 = new ProductionDto();
        productionDto6.setOrderId(id);
        productionDto6.setStartTime(workTimeStart(extractEnd));
        if(productionDto6.getStartTime().isBefore(proceService.getProceDtl(7L).getEndTime())){
            productionDto6.setStartTime(workTimeStart(proceService.getProceDtl(7L).getEndTime()));
        }
        productionDto6.setMatId(1L); //추후 수정
        productionDto6.setLotId("검사-"+id);

        LocalDateTime testEnd = calTesting(packingEnd, totalpau);

        productionDto6.setEndTime(testEnd);
        productionDto6.setMatInput(totalpau);
        Production production6 = Production.createProduction(productionDto6);
        saveProduction(production6);

        ProceDto proceDto7 = proceService.getProceDtl(7L);
        proceDto7.setEndTime(testEnd);
        proceService.updateProce(proceDto7);


        //냉각 저장
        ProductionDto productionDto7 = new ProductionDto();
        productionDto7.setOrderId(id);
        productionDto7.setStartTime(workTimeStart(extractEnd));
        if(productionDto7.getStartTime().isBefore(proceService.getProceDtl(8L).getEndTime())){
            productionDto7.setStartTime(workTimeStart(proceService.getProceDtl(8L).getEndTime()));
        }
        productionDto7.setMatId(1L); //추후 수정
        productionDto7.setLotId("냉각-"+id);

        LocalDateTime coolingEnd = calCooling(testEnd);

        productionDto7.setEndTime(coolingEnd);
        productionDto7.setMatInput((int)requiredCab);
        Production production7 = Production.createProduction(productionDto7);
        saveProduction(production7);

        ProceDto proceDto8 = proceService.getProceDtl(8L);
        proceDto8.setEndTime(coolingEnd);
        proceService.updateProce(proceDto8);


        //포장 저장

        ProductionDto productionDto8 = new ProductionDto();
        productionDto8.setOrderId(id);
        productionDto8.setStartTime(workTimeStart(extractEnd));
        if(productionDto8.getStartTime().isBefore(proceService.getProceDtl(9L).getEndTime())){
            productionDto8.setStartTime(workTimeStart(proceService.getProceDtl(9L).getEndTime()));
        }
        productionDto8.setMatId(1L); //추후 수정
        productionDto8.setLotId("포장-"+id);
        LocalDateTime boxingEnd = calBoxing(coolingEnd, box);

        productionDto8.setEndTime(boxingEnd);
        productionDto8.setMatInput(box);
        Production production8 = Production.createProduction(productionDto8);
        saveProduction(production8);

        ProceDto proceDto9 = proceService.getProceDtl(9L);
        proceDto9.setEndTime(boxingEnd);
        proceService.updateProce(proceDto9);


        return workTimeStart(boxingEnd);

    }


    //흑마늘 계산
    public LocalDateTime calGal(int box, LocalDateTime startTime,Long id) throws Exception {

        int totalpau = 30 * box;
        double remainGal = 0;
        boolean isStick = false;


        //필요한 흑마늘 추출액 무게
        double galicJu = Math.ceil(totalpau * 20);

        //수율을 반영하기 위해 추출기에 투입되는 흑마늘과 물을 합친 무게
        double requiredGalWat = Math.ceil(totalpau * 20 / 6 * 10);

        //실제 필요한 흑마늘의 양
        double requiredGal = requiredGalWat / 4;

        //흑마늘의 최소 주문수량 = 10,000g
        double offerGal = Math.ceil(requiredGal / 10000) * 10000;

        //잔여 흑마늘 양
        //재고로 저장해야함
        remainGal = offerGal - requiredGal + remainGal;

        System.out.println();
        System.out.println("주문 박스 수량: " + box + " box");
        System.out.println("필요한 흑마늘 파우치 총량: " + totalpau + " ea");
        System.out.println("흑마늘 추출액 필요량: " + galicJu + " ml");
        System.out.println("60%의 수율을 반영한 필요투입량: " + requiredGalWat / 1000 + " kg");
        System.out.println("필요한 흑마늘양: " + requiredGal / 1000 + " kg");
        System.out.println("발주해야하는 흑마늘양: " + offerGal / 1000 + " kg");
        System.out.println("잔여 흑마늘양 : " + remainGal / 1000 + " kg");
        System.out.println();



        //계량 저장
        ProductionDto productionDto1 = new ProductionDto();
        productionDto1.setOrderId(id);
        productionDto1.setStartTime(startTime);
        productionDto1.setMatId(1L); //추후 수정
        productionDto1.setLotId("계량-"+id);

        LocalDateTime weightEnd = calWeigh(startTime);

        productionDto1.setEndTime(weightEnd);
        productionDto1.setMatInput((int)requiredGal);
        Production production1 = Production.createProduction(productionDto1);
        saveProduction(production1);

        ProceDto proceDto1 = proceService.getProceDtl(1L);
        proceDto1.setEndTime(weightEnd);
        proceService.updateProce(proceDto1);


        //세척 저장

        ProductionDto productionDto2 = new ProductionDto();
        productionDto2.setOrderId(id);
        productionDto2.setStartTime(workTimeStart(weightEnd));
        if(productionDto2.getStartTime().isBefore(proceService.getProceDtl(2L).getEndTime())){
            productionDto2.setStartTime(workTimeStart(proceService.getProceDtl(2L).getEndTime()));
        }

        productionDto2.setMatId(1L); //추후 수정
        productionDto2.setLotId("세척-"+id);

        LocalDateTime washEnd = calWash(weightEnd, requiredGal);

        productionDto2.setEndTime(washEnd);
        productionDto2.setMatInput((int)requiredGal);
        Production production2 = Production.createProduction(productionDto2);
        saveProduction(production2);

        ProceDto proceDto2 = proceService.getProceDtl(2L);
        proceDto2.setEndTime(washEnd);
        proceService.updateProce(proceDto2);



        //추출 저장

        ProductionDto productionDto3 = new ProductionDto();
        productionDto3.setOrderId(id);
        productionDto3.setStartTime(workTimeStart(washEnd));
        if(productionDto3.getStartTime().isBefore(proceService.getProceDtl(3L).getEndTime())){
            productionDto3.setStartTime(workTimeStart(proceService.getProceDtl(3L).getEndTime()));
        }
        productionDto3.setMatId(1L); //추후 수정
        productionDto3.setLotId("추출-"+id);

        LocalDateTime extractEnd = calExtract(washEnd, requiredGal);

        productionDto3.setEndTime(extractEnd);
        productionDto3.setMatInput((int)requiredGal);
        Production production3 = Production.createProduction(productionDto3);
        saveProduction(production3);

        ProceDto proceDto3 = proceService.getProceDtl(3L);
        proceDto3.setEndTime(extractEnd);
        proceService.updateProce(proceDto3);

        //혼합 저장
        ProductionDto productionDto4 = new ProductionDto();
        productionDto4.setOrderId(id);
        productionDto4.setStartTime(workTimeStart(extractEnd));
        if(productionDto4.getStartTime().isBefore(proceService.getProceDtl(4L).getEndTime())){
            productionDto4.setStartTime(workTimeStart(proceService.getProceDtl(4L).getEndTime()));
        }
        productionDto4.setMatId(1L); //추후 수정
        productionDto4.setLotId("혼합-"+id);

        LocalDateTime blendEnd = calBlend(extractEnd, isStick);

        productionDto4.setEndTime(blendEnd);
        productionDto4.setMatInput((int)requiredGal);
        Production production4 = Production.createProduction(productionDto4);
        saveProduction(production4);

        ProceDto proceDto4 = proceService.getProceDtl(4L);
        proceDto4.setEndTime(blendEnd);
        proceService.updateProce(proceDto4);


        //충진 저장

        ProductionDto productionDto5 = new ProductionDto();
        productionDto5.setOrderId(id);
        productionDto5.setStartTime(workTimeStart(extractEnd));
        if(productionDto5.getStartTime().isBefore(proceService.getProceDtl(5L).getEndTime())){
            productionDto5.setStartTime(workTimeStart(proceService.getProceDtl(5L).getEndTime()));
        }
        productionDto5.setMatId(1L); //추후 수정
        productionDto5.setLotId("충진-"+id);

        LocalDateTime packingEnd = calPacking(blendEnd, totalpau, isStick);

        productionDto5.setEndTime(packingEnd);
        productionDto5.setMatInput(totalpau);
        Production production5 = Production.createProduction(productionDto5);
        saveProduction(production5);

        ProceDto proceDto5 = proceService.getProceDtl(5L);
        proceDto5.setEndTime(packingEnd);
        proceService.updateProce(proceDto5);


        //검사 저장

        ProductionDto productionDto6 = new ProductionDto();
        productionDto6.setOrderId(id);
        productionDto6.setStartTime(workTimeStart(extractEnd));
        if(productionDto6.getStartTime().isBefore(proceService.getProceDtl(7L).getEndTime())){
            productionDto6.setStartTime(workTimeStart(proceService.getProceDtl(7L).getEndTime()));
        }
        productionDto6.setMatId(1L); //추후 수정
        productionDto6.setLotId("검사-"+id);

        LocalDateTime testEnd = calTesting(packingEnd, totalpau);

        productionDto6.setEndTime(testEnd);
        productionDto6.setMatInput(totalpau);
        Production production6 = Production.createProduction(productionDto6);
        saveProduction(production6);

        ProceDto proceDto7 = proceService.getProceDtl(7L);
        proceDto7.setEndTime(testEnd);
        proceService.updateProce(proceDto7);


        //냉각 저장
        ProductionDto productionDto7 = new ProductionDto();
        productionDto7.setOrderId(id);
        productionDto7.setStartTime(workTimeStart(extractEnd));
        if(productionDto7.getStartTime().isBefore(proceService.getProceDtl(8L).getEndTime())){
            productionDto7.setStartTime(workTimeStart(proceService.getProceDtl(8L).getEndTime()));
        }
        productionDto7.setMatId(1L); //추후 수정
        productionDto7.setLotId("냉각-"+id);

        LocalDateTime coolingEnd = calCooling(testEnd);

        productionDto7.setEndTime(coolingEnd);
        productionDto7.setMatInput((int)requiredGal);
        Production production7 = Production.createProduction(productionDto7);
        saveProduction(production7);

        ProceDto proceDto8 = proceService.getProceDtl(8L);
        proceDto8.setEndTime(coolingEnd);
        proceService.updateProce(proceDto8);


        //포장 저장

        ProductionDto productionDto8 = new ProductionDto();
        productionDto8.setOrderId(id);
        productionDto8.setStartTime(workTimeStart(extractEnd));
        if(productionDto8.getStartTime().isBefore(proceService.getProceDtl(9L).getEndTime())){
            productionDto8.setStartTime(workTimeStart(proceService.getProceDtl(9L).getEndTime()));
        }
        productionDto8.setMatId(1L); //추후 수정
        productionDto8.setLotId("포장-"+id);
        LocalDateTime boxingEnd = calBoxing(coolingEnd, box);

        productionDto8.setEndTime(boxingEnd);
        productionDto8.setMatInput(box);
        Production production8 = Production.createProduction(productionDto8);
        saveProduction(production8);

        ProceDto proceDto9 = proceService.getProceDtl(9L);
        proceDto9.setEndTime(boxingEnd);
        proceService.updateProce(proceDto9);


        return workTimeStart(boxingEnd);



    }


    //석류 계산
    public  LocalDateTime calPom(int box, LocalDateTime startTime,Long id) throws Exception {

        int totalpau = 25 * box;
        double remainPom = 0;
        double remainCol = 0;
        boolean isStick = true;


        //필요한 석류 추출액 무게
        double pomJu = Math.ceil(totalpau * 5);

        //필요한 콜라겐 무게
        double colJu = Math.ceil(totalpau * 2);

        //혼합기에 들어가는 석류농축액과 콜라겐과 물을 합친 무게
        double requiredPomColWat = Math.ceil(pomJu + colJu + totalpau * 8);

        //석류농축액의 최소 주문수량 = 5,000g
        double offerPom = Math.ceil(pomJu / 5000) * 5000;

        //콜라겐의 최소 주문수량 = 5,000g
        double offerCol = Math.ceil(colJu / 5000) * 5000;

        //잔여 석류농축액 양
        //재고로 저장
        remainPom = offerPom - pomJu + remainPom;

        //잔여 콜라겐 양
        //재고로 저장
        remainCol = offerCol - colJu + remainCol;

        System.out.println();
        System.out.println("주문 박스 수량: " + box + " box");
        System.out.println("필요한 석류 파우치 총량: " + totalpau + " ea");
        System.out.println("석류 농축액 필요량: " + pomJu + " ml");
        System.out.println("콜라겐 필요량: " + colJu + " ml");
        System.out.println("석류농축액 + 콜라겐 + 정제수 투입량: " + requiredPomColWat + " g");
        System.out.println("발주해야하는 석류 농축액양: " + offerPom / 1000 + " kg");
        System.out.println("발주해야하는 콜라겐양: " + offerCol / 1000 + " kg");
        System.out.println("잔여 석류농축액양 : " + remainPom + " g");
        System.out.println("잔여 콜라겐양 : " + remainCol + " g");
        System.out.println();


        //추출 저장
        ProductionDto productionDto3 = new ProductionDto();
        productionDto3.setOrderId(id);
        productionDto3.setStartTime(workTimeStart(startTime));
        if(productionDto3.getStartTime().isBefore(proceService.getProceDtl(3L).getEndTime())){
            productionDto3.setStartTime(workTimeStart(proceService.getProceDtl(3L).getEndTime()));
        }
        productionDto3.setMatId(1L); //추후 수정
        productionDto3.setLotId("추출-"+id);

        LocalDateTime extractEnd = calWeigh(startTime);

        productionDto3.setEndTime(extractEnd);
        productionDto3.setMatInput((int)pomJu);
        Production production3 = Production.createProduction(productionDto3);
        saveProduction(production3);

        ProceDto proceDto3 = proceService.getProceDtl(3L);
        proceDto3.setEndTime(extractEnd);
        proceService.updateProce(proceDto3);



        //혼합 저장
        ProductionDto productionDto4 = new ProductionDto();
        productionDto4.setOrderId(id);
        productionDto4.setStartTime(workTimeStart(extractEnd));
        if(productionDto4.getStartTime().isBefore(proceService.getProceDtl(4L).getEndTime())){
            productionDto4.setStartTime(workTimeStart(proceService.getProceDtl(4L).getEndTime()));
        }
        productionDto4.setMatId(1L); //추후 수정
        productionDto4.setLotId("혼합-"+id);

        LocalDateTime blendEnd = calBlend(extractEnd, isStick);

        productionDto4.setEndTime(blendEnd);
        productionDto4.setMatInput((int)pomJu);
        Production production4 = Production.createProduction(productionDto4);
        saveProduction(production4);

        ProceDto proceDto4 = proceService.getProceDtl(4L);
        proceDto4.setEndTime(blendEnd);
        proceService.updateProce(proceDto4);



        ProductionDto productionDto5 = new ProductionDto();
        productionDto5.setOrderId(id);
        productionDto5.setStartTime(workTimeStart(extractEnd));
        if(productionDto5.getStartTime().isBefore(proceService.getProceDtl(6L).getEndTime())){
            productionDto5.setStartTime(workTimeStart(proceService.getProceDtl(6L).getEndTime()));
        }
        productionDto5.setMatId(1L); //추후 수정
        productionDto5.setLotId("충진-"+id);

        LocalDateTime packingEnd = calPacking(blendEnd, totalpau, isStick);

        productionDto5.setEndTime(packingEnd);
        productionDto5.setMatInput(totalpau);
        Production production5 = Production.createProduction(productionDto5);
        saveProduction(production5);

        ProceDto proceDto5 = proceService.getProceDtl(6L);
        proceDto5.setEndTime(packingEnd);
        proceService.updateProce(proceDto5);


        ProductionDto productionDto6 = new ProductionDto();
        productionDto6.setOrderId(id);
        productionDto6.setStartTime(workTimeStart(extractEnd));
        if(productionDto6.getStartTime().isBefore(proceService.getProceDtl(7L).getEndTime())){
            productionDto6.setStartTime(workTimeStart(proceService.getProceDtl(7L).getEndTime()));
        }
        productionDto6.setMatId(1L); //추후 수정
        productionDto6.setLotId("검사-"+id);

        LocalDateTime testEnd = calTesting(packingEnd, totalpau);

        productionDto6.setEndTime(testEnd);
        productionDto6.setMatInput(totalpau);
        Production production6 = Production.createProduction(productionDto6);
        saveProduction(production6);

        ProceDto proceDto7 = proceService.getProceDtl(7L);
        proceDto7.setEndTime(testEnd);
        proceService.updateProce(proceDto7);


        //냉각 저장
        ProductionDto productionDto7 = new ProductionDto();
        productionDto7.setOrderId(id);
        productionDto7.setStartTime(workTimeStart(extractEnd));
        if(productionDto7.getStartTime().isBefore(proceService.getProceDtl(8L).getEndTime())){
            productionDto7.setStartTime(workTimeStart(proceService.getProceDtl(8L).getEndTime()));
        }
        productionDto7.setMatId(1L); //추후 수정
        productionDto7.setLotId("냉각-"+id);

        LocalDateTime coolingEnd = calCooling(testEnd);

        productionDto7.setEndTime(coolingEnd);

        Production production7 = Production.createProduction(productionDto7);
        saveProduction(production7);

        ProceDto proceDto8 = proceService.getProceDtl(8L);
        proceDto8.setEndTime(coolingEnd);
        proceService.updateProce(proceDto8);


        //포장 저장

        ProductionDto productionDto8 = new ProductionDto();
        productionDto8.setOrderId(id);
        productionDto8.setStartTime(workTimeStart(extractEnd));
        if(productionDto8.getStartTime().isBefore(proceService.getProceDtl(9L).getEndTime())){
            productionDto8.setStartTime(workTimeStart(proceService.getProceDtl(9L).getEndTime()));
        }
        productionDto8.setMatId(1L); //추후 수정
        productionDto8.setLotId("포장-"+id);
        LocalDateTime boxingEnd = calBoxing(coolingEnd, box);

        productionDto8.setEndTime(boxingEnd);
        productionDto8.setMatInput(box);
        Production production8 = Production.createProduction(productionDto8);
        saveProduction(production8);

        ProceDto proceDto9 = proceService.getProceDtl(9L);
        proceDto9.setEndTime(boxingEnd);
        proceService.updateProce(proceDto9);


        return workTimeStart(boxingEnd);

    }


    //매실 계산
    public LocalDateTime calPlu(int box, LocalDateTime startTime,Long id) throws Exception {

        int totalpau = 25 * box;
        double remainPlu = 0;
        double remainCol = 0;
        boolean isStick = true;


        //필요한 매실 추출액 무게
        double pluJu = Math.ceil(totalpau * 5);

        //필요한 콜라겐 무게
        double colJu = Math.ceil(totalpau * 2);

        //혼합기에 들어가는 매실농축액과 콜라겐과 물을 합친 무게
        double requiredPluColWat = Math.ceil(pluJu + colJu + totalpau * 8);

        //매실농축액의 최소 주문수량 = 5,000g
        double offerPlu = Math.ceil(pluJu / 5000) * 5000;

        //콜라겐의 최소 주문수량 = 5,000g
        double offerCol = Math.ceil(colJu / 5000) * 5000;

        //잔여 매실농축액 양
        //재고로 저장
        remainPlu = offerPlu - pluJu + remainPlu;

        //잔여 콜라겐 양
        //재고로 저장
        remainCol = offerCol - colJu + remainCol;

        System.out.println();
        System.out.println("주문 박스 수량: " + box + " box");
        System.out.println("필요한 매실 파우치 총량: " + totalpau + " ea");
        System.out.println("매실 농축액 필요량: " + pluJu + " ml");
        System.out.println("콜라겐 필요량: " + colJu + " ml");
        System.out.println("매실농축액 + 콜라겐 + 정제수 투입량: " + requiredPluColWat + " g");
        System.out.println("발주해야하는 매실 농축액양: " + offerPlu / 1000 + " kg");
        System.out.println("발주해야하는 콜라겐양: " + offerCol / 1000 + " kg");
        System.out.println("잔여 매실농축액양 : " + remainPlu + " g");
        System.out.println("잔여 콜라겐양 : " + remainCol + " g");
        System.out.println();

        //추출 저장
        ProductionDto productionDto3 = new ProductionDto();
        productionDto3.setOrderId(id);
        productionDto3.setStartTime(workTimeStart(startTime));
        if(productionDto3.getStartTime().isBefore(proceService.getProceDtl(3L).getEndTime())){
            productionDto3.setStartTime(workTimeStart(proceService.getProceDtl(3L).getEndTime()));
        }
        productionDto3.setMatId(1L); //추후 수정
        productionDto3.setLotId("추출-"+id);

        LocalDateTime extractEnd = calWeigh(startTime);

        productionDto3.setEndTime(extractEnd);
        productionDto3.setMatInput((int)pluJu);
        Production production3 = Production.createProduction(productionDto3);
        saveProduction(production3);

        ProceDto proceDto3 = proceService.getProceDtl(3L);
        proceDto3.setEndTime(extractEnd);
        proceService.updateProce(proceDto3);



        //혼합 저장
        ProductionDto productionDto4 = new ProductionDto();
        productionDto4.setOrderId(id);
        productionDto4.setStartTime(workTimeStart(extractEnd));
        if(productionDto4.getStartTime().isBefore(proceService.getProceDtl(4L).getEndTime())){
            productionDto4.setStartTime(workTimeStart(proceService.getProceDtl(4L).getEndTime()));
        }
        productionDto4.setMatId(1L); //추후 수정
        productionDto4.setLotId("혼합-"+id);

        LocalDateTime blendEnd = calBlend(extractEnd, isStick);

        productionDto4.setEndTime(blendEnd);
        productionDto4.setMatInput((int)pluJu);
        Production production4 = Production.createProduction(productionDto4);
        saveProduction(production4);

        ProceDto proceDto4 = proceService.getProceDtl(4L);
        proceDto4.setEndTime(blendEnd);
        proceService.updateProce(proceDto4);



        ProductionDto productionDto5 = new ProductionDto();
        productionDto5.setOrderId(id);
        productionDto5.setStartTime(workTimeStart(extractEnd));
        if(productionDto5.getStartTime().isBefore(proceService.getProceDtl(6L).getEndTime())){
            productionDto5.setStartTime(workTimeStart(proceService.getProceDtl(6L).getEndTime()));
        }
        productionDto5.setMatId(1L); //추후 수정
        productionDto5.setLotId("충진-"+id);

        LocalDateTime packingEnd = calPacking(blendEnd, totalpau, isStick);

        productionDto5.setEndTime(packingEnd);
        productionDto5.setMatInput(totalpau);
        Production production5 = Production.createProduction(productionDto5);
        saveProduction(production5);

        ProceDto proceDto5 = proceService.getProceDtl(6L);
        proceDto5.setEndTime(packingEnd);
        proceService.updateProce(proceDto5);


        ProductionDto productionDto6 = new ProductionDto();
        productionDto6.setOrderId(id);
        productionDto6.setStartTime(workTimeStart(extractEnd));
        if(productionDto6.getStartTime().isBefore(proceService.getProceDtl(7L).getEndTime())){
            productionDto6.setStartTime(workTimeStart(proceService.getProceDtl(7L).getEndTime()));
        }
        productionDto6.setMatId(1L); //추후 수정
        productionDto6.setLotId("검사-"+id);

        LocalDateTime testEnd = calTesting(packingEnd, totalpau);

        productionDto6.setEndTime(testEnd);
        productionDto6.setMatInput(totalpau);
        Production production6 = Production.createProduction(productionDto6);
        saveProduction(production6);

        ProceDto proceDto7 = proceService.getProceDtl(7L);
        proceDto7.setEndTime(testEnd);
        proceService.updateProce(proceDto7);


        //냉각 저장
        ProductionDto productionDto7 = new ProductionDto();
        productionDto7.setOrderId(id);
        productionDto7.setStartTime(workTimeStart(extractEnd));
        if(productionDto7.getStartTime().isBefore(proceService.getProceDtl(8L).getEndTime())){
            productionDto7.setStartTime(workTimeStart(proceService.getProceDtl(8L).getEndTime()));
        }
        productionDto7.setMatId(1L); //추후 수정
        productionDto7.setLotId("냉각-"+id);

        LocalDateTime coolingEnd = calCooling(testEnd);

        productionDto7.setEndTime(coolingEnd);

        Production production7 = Production.createProduction(productionDto7);
        saveProduction(production7);

        ProceDto proceDto8 = proceService.getProceDtl(8L);
        proceDto8.setEndTime(coolingEnd);
        proceService.updateProce(proceDto8);


        //포장 저장

        ProductionDto productionDto8 = new ProductionDto();
        productionDto8.setOrderId(id);
        productionDto8.setStartTime(workTimeStart(extractEnd));
        if(productionDto8.getStartTime().isBefore(proceService.getProceDtl(9L).getEndTime())){
            productionDto8.setStartTime(workTimeStart(proceService.getProceDtl(9L).getEndTime()));
        }
        productionDto8.setMatId(1L); //추후 수정
        productionDto8.setLotId("포장-"+id);
        LocalDateTime boxingEnd = calBoxing(coolingEnd, box);

        productionDto8.setEndTime(boxingEnd);
        productionDto8.setMatInput(box);
        Production production8 = Production.createProduction(productionDto8);
        saveProduction(production8);

        ProceDto proceDto9 = proceService.getProceDtl(9L);
        proceDto9.setEndTime(boxingEnd);
        proceService.updateProce(proceDto9);


        return workTimeStart(boxingEnd);



    }


    //원료계량 weight
    public static LocalDateTime calWeigh(LocalDateTime startTime) {

        LocalDateTime ibTime = startTime;

        // 원료계량 시작 = weightStart = 자재투입시작시간
        LocalDateTime weightStart = ibTime;
        weightStart = workTimeStart(weightStart);
        System.out.println("원료계량 시작: " + weightStart.format(formatter));

        LocalDateTime weightEnd = weightStart.plusMinutes(50);
        System.out.println("원료계량 끝 : " + weightEnd.format(formatter));
        System.out.println();

        return weightEnd;

    }


    //전처리 wash
    public static LocalDateTime calWash(LocalDateTime weightEnd, double requiredMat) {

        LocalDateTime washStart = weightEnd;
        washStart = workTimeStart(washStart);
        System.out.println("전처리 시작: " + washStart.format(formatter));


        LocalDateTime washEnd = washStart.plusMinutes(20).plusMinutes((long) Math.ceil(requiredMat / 1000 * 60 / 1000));
        System.out.println("전처리 끝: " + washEnd.format(formatter));
        System.out.println();


        return washEnd;
    }


    //추출 extract
    public static LocalDateTime calExtract(LocalDateTime washEnd, double requiredMat) {

        LocalDateTime extractStart = washEnd;
        extractStart = workTimeStart(extractStart);
        System.out.println("추출 시작: " + extractStart.format(formatter));

        LocalDateTime extractEnd = extractStart.plusMinutes(60).plusHours((long) Math.ceil(((requiredMat / 1000 * 24) / 1000) * 2));
        System.out.println("추출 끝: " + extractEnd.format(formatter));
        System.out.println();

        return extractEnd;
    }


    // 혼합 및 살균 blend
    public static LocalDateTime calBlend(LocalDateTime extractEnd, boolean isStick) {


        LocalDateTime blendEnd = null;

        if (!isStick) { //즙일 때
            LocalDateTime blendStart = extractEnd;
            System.out.println("혼합 및 살균 시작 : " + blendStart.format(formatter));

            blendEnd = blendStart.plusHours(24);
            System.out.println("혼합 및 살균 끝 : " + blendEnd.format(formatter));
            System.out.println();

        } else {
            LocalDateTime blendStart = extractEnd;
            blendStart = workTimeStart(blendStart);
            System.out.println("혼합 및 살균 시작 : " + blendStart.format(formatter));

            blendEnd = blendStart.plusMinutes(20).plusHours(8);
            System.out.println("혼합 및 살균 끝 : " + blendEnd.format(formatter));
            System.out.println();

        }


        return blendEnd;
    }


    //충진 packing
    public static LocalDateTime calPacking(LocalDateTime blendEnd, int totalpau, boolean isStick) {


        LocalDateTime packingStart = blendEnd;
        packingStart = workTimeStart(packingStart);
        System.out.println("충진 시작: " + packingStart.format(formatter));

        LocalDateTime packingEnd = null;

        if (!isStick) {
            packingEnd = packingStart.plusMinutes(20).plusHours((long) Math.ceil(Math.ceil(totalpau * 60) / 3500 / 60));
        } else {
            packingEnd = packingStart.plusMinutes(20).plusHours((long) Math.ceil(Math.ceil(totalpau * 60) / 3000 / 60));
        }

        System.out.println("충진 끝: " + packingEnd.format(formatter));
        System.out.println();


        return packingEnd;
    }


    // 검사 testing
    public static LocalDateTime calTesting(LocalDateTime packingEnd, int totalpau) {


        LocalDateTime testStart = packingEnd;
        testStart = workTimeStart(testStart);
        System.out.println("검사 시작: " + testStart.format(formatter));

        LocalDateTime testEnd = testStart.plusMinutes(10).plusHours((long) Math.ceil(Math.ceil(totalpau * 60) / 5000) / 60);
        System.out.println("검사 끝: " + testEnd.format(formatter));
        System.out.println();


        return testEnd;
    }


    // 냉각 cooling
    public static LocalDateTime calCooling(LocalDateTime testEnd) {


        LocalDateTime coolingStart = testEnd;
        //제약사항 없음
        System.out.println("냉각 시작: " + coolingStart.format(formatter));

        LocalDateTime coolingEnd = coolingStart.plusDays(1).with(LocalTime.of(9, 0));
        System.out.println("냉각 끝: " + coolingEnd.format(formatter));
        System.out.println();

        return coolingEnd;
    }


    // 포장 boxing
    public static LocalDateTime calBoxing(LocalDateTime coolingEnd, int box) {


        LocalDateTime boxingStart = coolingEnd;
        boxingStart = workTimeStart(boxingStart);
        System.out.println("포장 시작: " + boxingStart.format(formatter));

        int workCapacity = 20; // 한 명의 인부가 1시간 동안 작업할 수 있는 box 수
        int workTimePerSec = 18; // 작업 시간 (초)
        double requiredTime = (double) Math.ceil((box * workTimePerSec) / 60); // 소요 시간 계산
        LocalTime lunchStart = LocalTime.of(12, 0);
        LocalTime lunchEnd = LocalTime.of(13, 0);

        LocalDateTime boxingEnd = boxingStart.plusMinutes(20);
        boxingEnd = boxingEnd.plusMinutes((long) requiredTime);

        boxingEnd = boxingTimeSet(boxingEnd);


        LocalDateTime passdays = boxingStart;

        while (passdays.toLocalDate().isBefore(boxingEnd.toLocalDate())) {

            passdays = passdays.plusDays(1);
            if (passdays.getDayOfWeek() == DayOfWeek.SATURDAY || passdays.getDayOfWeek() == DayOfWeek.SUNDAY) {

            } else {
                boxingEnd = boxingEnd.plusHours(1);
            }
        }


        boxingEnd = boxingTimeSet(boxingEnd);
        boxingEnd = boxingTimeSet(boxingEnd);
        if (!boxingEnd.toLocalTime().isBefore(lunchStart)) {
            boxingEnd = boxingEnd.plusHours(1);
        }
        System.out.println("포장 끝: " + boxingEnd.format(formatter));
        System.out.println();


        return boxingEnd;
    }

    //test
    public static LocalDateTime boxingTimeSet(LocalDateTime boxingEnd) {

        LocalTime morningStart = LocalTime.of(9, 0);

        LocalTime lunchStart = LocalTime.of(12, 0);

        LocalTime afternoonStart = LocalTime.of(13, 0);
        LocalTime afternoonEnd = LocalTime.of(18, 0);

        if (boxingEnd.getDayOfWeek() == DayOfWeek.FRIDAY && boxingEnd.toLocalTime().isAfter(afternoonEnd)) {
            boxingEnd = boxingEnd.plusDays(2).plusHours(15); // 월요일 아침 9시

        } else if (boxingEnd.getDayOfWeek() == DayOfWeek.SATURDAY) {
            boxingEnd = boxingEnd.plusDays(2).plusHours(15);

        } else if (boxingEnd.getDayOfWeek() == DayOfWeek.SUNDAY) {
            boxingEnd = boxingEnd.plusDays(2).plusHours(15);

        } else if (boxingEnd.toLocalTime().isAfter(afternoonEnd)) {
            boxingEnd = boxingEnd.plusHours(15); // 오전 9시부터 작업 시작

        } else if (boxingEnd.toLocalTime().isBefore(morningStart)) {
            boxingEnd = boxingEnd.plusHours(15); // 오전 9시부터 작업 시작

        } else if (boxingEnd.toLocalTime().isAfter(lunchStart) && boxingEnd.toLocalTime().isBefore(afternoonStart)) {
            boxingEnd = boxingEnd.plusHours(1); // 오후 1시부터 작업 시작

        } else if (boxingEnd.toLocalTime().isAfter(afternoonEnd)) {
            boxingEnd = boxingEnd.plusDays(1).with(morningStart); // 다음 날 오전 9시부터 작업 시작
        }

        return boxingEnd;
    }

}