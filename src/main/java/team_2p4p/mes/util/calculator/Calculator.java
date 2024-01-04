package team_2p4p.mes.util.calculator;

import net.bytebuddy.asm.Advice;
import org.hibernate.loader.collection.SubselectOneToManyLoader;
import org.thymeleaf.spring5.processor.SpringInputCheckboxFieldTagProcessor;
import team_2p4p.mes.util.process.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculator {

    int measuremntLeadTime = 20;
    int preprocessingLeadTime = 20;
    int fillProcessingLeadTime = 20;
    int checkProcessingLeadTime = 10;
    static int packingProcessingLeadTime = 20;
    Factory factory = Factory.getInstance();


    public void obtain(MesAll mesAll){

        materialMeasurement(mesAll);

        if(mesAll.getItemId()<=2){
            preProcessing(mesAll);
        }

        operateLiquidSystem(mesAll);

        if(mesAll.getItemId()<=2){
            fillPouchProcessing(mesAll);
        }else{
            fillStickProcessing(mesAll);
        }

        CheckProcessing(mesAll);
        packingPrecessing(mesAll);
        mesAll.setEstimateDate(mesAll.getPackingOutputTimeList().get(mesAll.getPackingOutputTimeList().size()-1)); //예상납품일
    }

    public void confirmObtain(MesAll mesAll){

        factory.getMeasurement().getConfirmList().add(mesAll);
        factory.getPreProcessing().getConfirmList().add(mesAll);
        factory.getLiquidSystem().getConfirmList().add(mesAll);

        if(mesAll.getItemId() == 1 || mesAll.getItemId() == 2){
            factory.getFillPouchProcessing().getConfirmList().add(mesAll);
        }else {
            factory.getFillStickProcessing().getConfirmList().add(mesAll);
        }

        factory.getCheckProcessing().getConfirmList().add(mesAll);
        factory.getPacking().getConfirmList().add(mesAll);
    }


    //원료계량
    MesAll materialMeasurement(MesAll mesAll) {
        Factory factory = Factory.getInstance();

        mesAll.setMeasurementAmount(mesAll.getAmount());
        if (factory.getMeasurement().getConfirmList().isEmpty()) {
            //리스트가 비었을떄
            mesAll.setInputMeasurementTime(inputTimeCheck(measuremntLeadTime,mesAll.getTime()));
            mesAll.setOutputMeasurementTime(mesAll.getInputMeasurementTime().plusMinutes(30)); // mesAll에 종료시간
        } else {
            //리스트에 무언가가 있을때 마지막 시간을 꺼내온다
            LocalDateTime lastTime = factory.getMeasurement().getConfirmList().get(factory.getMeasurement().getConfirmList().size() - 1).getOutputMeasurementTime();
            if(lastTime.isAfter(mesAll.getTime())){
                mesAll.setInputMeasurementTime(inputTimeCheck(measuremntLeadTime,lastTime));
            }else{
                mesAll.setInputMeasurementTime(inputTimeCheck(measuremntLeadTime,mesAll.getTime()));
            }
            mesAll.setOutputMeasurementTime(mesAll.getInputMeasurementTime().plusMinutes(30)); // mesAll에 종료시간
        }
        return mesAll;
    }

    //전처리
    MesAll preProcessing(MesAll mesAll) {
        int preProcessingCount = (int)Math.ceil(mesAll.getMeasurementAmount()/1000.0) == 0 ? 1 : (int)Math.ceil(mesAll.getMeasurementAmount()/1000.0);
        int preProcessingLastAmount = (int) mesAll.getMeasurementAmount()%1000==0?1000:(int) mesAll.getMeasurementAmount()%1000;

        mesAll.setPreProcessingCount(preProcessingCount);

        for(int i = 0; i < preProcessingCount; i++){

            if(mesAll.getItemId() == 1){
                mesAll.getPreProcessingAmountList().add(1000);
            }else{
                if(i == preProcessingCount-1){
                    mesAll.getPreProcessingAmountList().add(preProcessingLastAmount);
                }else{
                    mesAll.getPreProcessingAmountList().add(1000);
                }

            }
            if(i == 0){

                if (factory.getPreProcessing().getConfirmList().isEmpty()) {
                    //전처리 확정리스트가 비었을때
                    mesAll.getInputPreProcessingTimeList().add(inputTimeCheck(preprocessingLeadTime,mesAll.getOutputMeasurementTime()));
                    //시간계산 1ton/hour  1분당 16.667kg 가능 1kg당 3.6초 걸림

                } else {
                    //전처리 확정리스트에 대기목록이 있을때

                    LocalDateTime lastTime;

                    if(factory.getPreProcessing().getConfirmList().get(factory.getPreProcessing().getConfirmList().size() - 1).getItemId() <= 2){
                        List<LocalDateTime> list = factory.getPreProcessing().getConfirmList().get(factory.getPreProcessing().getConfirmList().size() - 1).getOutputPreProcessingTimeList();
                        lastTime = list.get(list.size()-1);
                    }else{
                        int j = factory.getPreProcessing().getConfirmList().size() - 1;
                        while (true){

                            if(factory.getPreProcessing().getConfirmList().get(j).getItemId() <= 2){
                                List<LocalDateTime> list  = factory.getPreProcessing().getConfirmList().get(j).getOutputPreProcessingTimeList();
                                lastTime = list.get(list.size()-1);
                                break;
                            }
                            j--;
                        }
                    }


                    if(lastTime.isAfter(mesAll.getOutputMeasurementTime())){
                        mesAll.getInputPreProcessingTimeList().add(inputTimeCheck(preprocessingLeadTime,lastTime));
                    }else{
                        mesAll.getInputPreProcessingTimeList().add(inputTimeCheck(preprocessingLeadTime,mesAll.getOutputMeasurementTime()));
                    }
                    //시간계산 1ton/hour  1분당 16.667kg 가능 1kg당 3.6초 걸림
                }
                mesAll.getOutputPreProcessingTimeList().add(mesAll.getInputPreProcessingTimeList().get(i).plusSeconds((long) (3.6 * mesAll.getPreProcessingAmountList().get(i))));

            }else{// i != 0

                //여기서 부터는 시간의 기준이 직전 전처리 끝난 시간 기준으로 리드타임을 부여하면서 작업을 해야된다.
                //고려 해야되는 것은? 직전 전처리가 9시부터 17:40분에 끝났을때 투입(점심시간 아니면) else 일때는 요일 고려해서 다음날 투입(일요일 제외)
                LocalDateTime preEndTime = mesAll.getOutputPreProcessingTimeList().get(i-1);
                mesAll.getInputPreProcessingTimeList().add(inputTimeCheck(preprocessingLeadTime,preEndTime));
                //시간계산 1ton/hour  1분당 16.667kg 가능 1kg당 3.6초 걸림
                mesAll.getOutputPreProcessingTimeList().add(mesAll.getInputPreProcessingTimeList().get(i).plusSeconds((long) (3.6 * mesAll.getPreProcessingAmountList().get(i))));
            } // i != 0
        } //

        return mesAll;
    }

    // 액체제조 시스템에서 언제 어떻게 들어가는지 체크
    // 이걸 이게 끝나면 mesAll에 inputTime이 체크가 된다.



    //액체제조
    MesAll operateLiquidSystem(MesAll mesAll){
        int amount;
        int leadTime = (mesAll.getItemId()>=3)?20:60;
        if(mesAll.getItemId() == 1){
            //양배추일때
            mesAll.setTotalLiquidSystemCount(mesAll.getPreProcessingCount()); //양배추는 전처리회수랑 똑같음
            amount = 1000;

            for(int i = 0; i < mesAll.getTotalLiquidSystemCount(); i++){
                liquidExcpect(mesAll,factory.getLiquidSystem(),leadTime,amount,i);
            }
        }else if(mesAll.getItemId() == 2){
            //흑마늘일때

            mesAll.setTotalLiquidSystemCount((int)Math.ceil(mesAll.getMeasurementAmount()/500.0));

            //투입개수 보정 마지막은 500이 아님
            for(int i = 0; i < mesAll.getTotalLiquidSystemCount(); i++){

                if(i == mesAll.getTotalLiquidSystemCount() -1){
                    //마지막일때는 개수보정
                    amount = (mesAll.getMeasurementAmount()%500==0)?500:(int)(mesAll.getMeasurementAmount() % 500);
                }else {
                    amount = 500;
                }

                liquidExcpect(mesAll,factory.getLiquidSystem(),leadTime,amount,i);
            }

        }else{
            //스틱일때

            mesAll.setTotalLiquidSystemCount((int)Math.ceil(mesAll.getMeasurementAmount()/650.0));
            //마지막 개수 보정

            for(int i = 0; i < mesAll.getTotalLiquidSystemCount(); i++){

                if(i == mesAll.getTotalLiquidSystemCount() -1){
                    //마지막일때는 개수보정
                    amount = (mesAll.getMeasurementAmount()%650==0)?650:(int)(mesAll.getMeasurementAmount() % 650);
                }else {
                    amount = 650;
                }

                liquidExcpect(mesAll,factory.getLiquidSystem(),leadTime,amount,i);
            }

        }

        return mesAll;

    }


    //즙충진
    MesAll fillPouchProcessing(MesAll mesAll) {

        mesAll.setFillPouchCount(mesAll.getTotalLiquidSystemCount());
        //전공정 output을 ml로 변환
        int ml = 0;
        //1개 생산되는데 걸리는 시간
        long fillTime = 0;

        if(factory.getFillPouchProcessing().getConfirmList().isEmpty()){
            // 충진기가 비었을때
            for(int i = 0; i < mesAll.getFillPouchCount(); i++){
                ml = mesAll.getLiquidSystemOutputAmountList().get(i) * 1000;
                //1개 생산되는데 걸리는 시간(나노초)
                fillTime = (ml/80) * (long) (1.02855 * 1_000_000_000);
                // mesAll에 시작시간 저장
                if(i == 0){
                    //첫 투입일때는 전공전 끝난시간 기준으로 투입

                    mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                    mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                    mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                    mesAll.getFillPouchOutputAmountList().add(ml/80);
                }else{
                    //n번째 투입일때는 시간 비교
                    if(mesAll.getLiquidSystemOutputTimeList().get(i).isAfter( mesAll.getFillPouchOutputTimeList().get(i-1))){
                        // 액체제조 시스템이 늦게 끝나면 액체제조 시스템 끝난 시간이 기준이된다.
                        mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                        mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillPouchOutputAmountList().add(ml/80);
                    }else{
                        //액체제조시스템이 먼저끝나면(충진이 끝나는 시간이 기준이 된다.)
                        mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getFillPouchOutputTimeList().get(i-1)));
                        mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillPouchOutputAmountList().add(ml/80);
                    }
                }
            }
        }else{
            //충진기의 스케줄이 있을때
            LocalDateTime lastTime;
            int j = factory.getFillPouchProcessing().getConfirmList().size() - 1;

            while (true){
                if(factory.getFillPouchProcessing().getConfirmList().get(j).getFillPouchCount()!=0){
                    List<LocalDateTime> list = factory.getFillPouchProcessing().getConfirmList().get(j).getFillPouchOutputTimeList();
                    lastTime = list.get(list.size()-1);
                    break;
                }
                j--;
            }

            for(int i = 0; i < mesAll.getFillPouchCount(); i++){
                ml = mesAll.getLiquidSystemOutputAmountList().get(i) * 1000;
                //1개 생산되는데 걸리는 시간(나노초)
                fillTime = (ml/80) * (long) (1.02855 * 1_000_000_000);
                if(i == 0){
                    // 스케줄이 끝나는 시간과 전공전 끝나는 시간을 비교

                    if(mesAll.getLiquidSystemOutputTimeList().get(i).isAfter(lastTime)){
                        // 이번 수주의 액체제조가 스케줄 리스트의 맨마지막 끝나는 시간보다 늦게 끝나면?
                        // 이번 수주의 액체제조가 기준이 된다.
                        mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                        mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillPouchOutputAmountList().add(ml/80);
                    }else{
                        //스케줄이 더 늦게 끝난다면?
                        mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, lastTime));
                        mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillPouchOutputAmountList().add(ml/80);
                    }
                }else{
                    //두번째 투입부터는 전 투입이 끝난시간 기준으로 투입
                    if(mesAll.getLiquidSystemOutputTimeList().get(i).isAfter(mesAll.getFillPouchOutputTimeList().get(mesAll.getFillPouchOutputTimeList().size()-1))){
                        //액체제조가 더 늦게 끝나면?
                        mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                        mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillPouchOutputAmountList().add(ml/80);
                    }else{
                        //i -1번째 충진이 늦게 끝나면
                        mesAll.getFillPouchInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getFillPouchOutputTimeList().get(mesAll.getFillPouchOutputTimeList().size()-1)));
                        mesAll.getFillPouchOutputTimeList().add(mesAll.getFillPouchInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillPouchInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillPouchOutputAmountList().add(ml/80);
                    }
                }
            }
        }

        return mesAll;
    }



    //스틱충진
    MesAll fillStickProcessing(MesAll mesAll) {

        mesAll.setFillStickCount(mesAll.getTotalLiquidSystemCount());
        //전공정 output을 ml로 변환
        int ml = 0;
        //1개 생산되는데 걸리는 시간
        long fillTime = 0;

        if(factory.getFillStickProcessing().getConfirmList().isEmpty()){
            // 충진기가 비었을때
            for(int i = 0; i < mesAll.getFillStickCount(); i++){
                ml = mesAll.getLiquidSystemOutputAmountList().get(i) * 1000;
                //1개 생산되는데 걸리는 시간(나노초)
                fillTime = (ml/15) * (long) (1.2 * 1_000_000_000);
                // mesAll에 시작시간 저장
                if(i == 0){
                    //첫 투입일때는 전공전 끝난시간 기준으로 투입

                    mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                    mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                    mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                    mesAll.getFillStickOutputAmountList().add(ml/15);
                }else{
                    //n번째 투입일때는 시간 비교
                    if(mesAll.getLiquidSystemOutputTimeList().get(i).isAfter( mesAll.getFillStickOutputTimeList().get(i-1))){
                        // 액체제조 시스템이 늦게 끝나면 액체제조 시스템 끝난 시간이 기준이된다.
                        mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                        mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillStickOutputAmountList().add(ml/15);
                    }else{
                        //액체제조시스템이 먼저끝나면(충진이 끝나는 시간이 기준이 된다.)
                        mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getFillStickOutputTimeList().get(i-1)));
                        mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillStickOutputAmountList().add(ml/15);
                    }
                }
            }
        }else{
            //충진기의 스케줄이 있을때

            LocalDateTime lastTime;
            int j = factory.getFillStickProcessing().getConfirmList().size() - 1;

            while (true){
                System.out.println("여기!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                if(j == -1){
                    lastTime = LocalDateTime.now();
                    break;
                }
                if(factory.getFillStickProcessing().getConfirmList().get(j).getFillStickCount()!=0){

                    List<LocalDateTime> list = factory.getFillStickProcessing().getConfirmList().get(j).getFillStickOutputTimeList();
                    lastTime = list.get(list.size()-1);
                    break;
                }

                j--;
            }

            for(int i = 0; i < mesAll.getFillStickCount(); i++){
                ml = mesAll.getLiquidSystemOutputAmountList().get(i) * 1000;
                //1개 생산되는데 걸리는 시간(나노초)
                fillTime = (ml/15) * (long) (1.2  * 1_000_000_000);
                if(i == 0){
                    // 스케줄이 끝나는 시간과 전공전 끝나는 시간을 비교
                    if(mesAll.getLiquidSystemOutputTimeList().get(i).isAfter(lastTime)){
                        //액체제조가 더 늦게 끝나면?
                        mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                        mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillStickOutputAmountList().add(ml/15);
                    }else{
                        //스케줄이 더 늦게 끝난다면?
                        mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, lastTime));
                        mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillStickOutputAmountList().add(ml/15);
                    }
                }else {
                    if(mesAll.getLiquidSystemOutputTimeList().get(i).isAfter(mesAll.getFillStickOutputTimeList().get(i-1))){
                        // 액체제조 시스템이 늦게 끝나면 액체제조 시스템 끝난 시간이 기준이된다.
                        mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getLiquidSystemOutputTimeList().get(i)));
                        mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillStickOutputAmountList().add(ml/15);
                    }else{
                        //액체제조시스템이 먼저끝나면(충진이 끝나는 시간이 기준이 된다.)
                        mesAll.getFillStickInputTimeList().add(inputTimeCheck(fillProcessingLeadTime, mesAll.getFillStickOutputTimeList().get(i-1)));
                        mesAll.getFillStickOutputTimeList().add(mesAll.getFillStickInputTimeList().get(i).plusNanos(fillTime));
                        mesAll.getFillStickInputAmountList().add(mesAll.getLiquidSystemOutputAmountList().get(i));
                        mesAll.getFillStickOutputAmountList().add(ml/15);
                    }
                }
            }
        }
        return mesAll;
    }


    // 검사
    MesAll CheckProcessing(MesAll mesAll) {
        // 0.72초당 1개씩 생산
        if (factory.getCheckProcessing().getConfirmList().isEmpty()) {
            //검사공정이 비었을때
            if (mesAll.getItemId() <= 2) {
                // 즙일때
                mesAll.setCheckCount(mesAll.getFillPouchCount());
                for (int i = 0; i < mesAll.getCheckCount(); i++) {
                    mesAll.getCheckInputAmountList().add(mesAll.getFillPouchOutputAmountList().get(i));
                    mesAll.getCheckOutputAmountList().add(mesAll.getCheckInputAmountList().get(i));

                    if (i == 0) {
                        mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillPouchOutputTimeList().get(i)));

                    } else {
                        if (mesAll.getFillPouchOutputTimeList().get(i).isAfter(mesAll.getCheckOutputTimeList().get(i - 1))) {
                            // 충진이 늦게 끝나면 충진시간이 기준이된다.
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillPouchOutputTimeList().get(i)));
                        } else {
                            //아니면 (검사가 끝나는 시간이 기준이 된다.)
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getCheckInputTimeList().get(i - 1)));
                        }
                    }
                    LocalDateTime outputTime = mesAll.getCheckInputTimeList().get(i).plusSeconds((long) (0.72 * mesAll.getCheckInputAmountList().get(i)));
                    mesAll.getCheckOutputTimeList().add(outputTime);
                }

            } else {
                // 스틱일떄
                mesAll.setCheckCount(mesAll.getFillStickCount());

                for (int i = 0; i < mesAll.getCheckCount(); i++) {
                    mesAll.getCheckInputAmountList().add(mesAll.getFillStickOutputAmountList().get(i));
                    mesAll.getCheckOutputAmountList().add(mesAll.getCheckInputAmountList().get(i));

                    if (i == 0) {
                        mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillStickOutputTimeList().get(i)));

                    } else {
                        if (mesAll.getFillStickOutputTimeList().get(i).isAfter(mesAll.getCheckOutputTimeList().get(i - 1))) {
                            // 충진이 늦게 끝나면 충진시간이 기준이된다.
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillStickOutputTimeList().get(i)));
                        } else {
                            //아니면 (검사가 끝나는 시간이 기준이 된다.)
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getCheckInputTimeList().get(i - 1)));
                        }
                    }
                    LocalDateTime outputTime = mesAll.getCheckInputTimeList().get(i).plusSeconds((long) (0.72 * mesAll.getCheckInputAmountList().get(i)));
                    mesAll.getCheckOutputTimeList().add(outputTime);
                }
            }
        } else {
            //검사 스케줄이 있을때
            if(mesAll.getItemId() <= 2){
                // 즙일때
                mesAll.setCheckCount(mesAll.getFillPouchCount());
                List<LocalDateTime> timeList = factory.getCheckProcessing().getConfirmList().get(factory.getCheckProcessing().getConfirmList().size()-1).getCheckOutputTimeList();
                LocalDateTime lastTime = timeList.get(timeList.size()-1); //직전 수주의 검사끝난시간
                
                for(int i = 0; i < mesAll.getCheckCount(); i++){

                    mesAll.getCheckInputAmountList().add(mesAll.getFillPouchOutputAmountList().get(i));
                    mesAll.getCheckOutputAmountList().add(mesAll.getCheckInputAmountList().get(i));

                    if(i == 0){
                        //첫번째 투입
                        if(mesAll.getFillPouchOutputTimeList().get(i).isAfter(lastTime)){
                            //이전공정 끝이 스케줄 보다 늦으면 시작시간은 전공정의 끝나는 시간이 기준
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillPouchOutputTimeList().get(i)));
                        }else{
                            //아닐때 시작시간의 기준은 스케줄이 끝나는 시간
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, lastTime));
                        }
                    }else{
                        //n번째 투입
                        if(mesAll.getFillPouchOutputTimeList().get(i).isAfter(mesAll.getCheckOutputTimeList().get(i-1))){
                            //이전공정 끝이 검사보다 늦으면 시작시간은 전공정의 끝나는 시간이 기준
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillPouchOutputTimeList().get(i)));
                        }else{
                            //아닐때 시작시간의 기준은 수케줄이 끝나는 시간
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getCheckOutputTimeList().get(i-1)));
                        }
                    }
                    LocalDateTime outputTime = mesAll.getCheckInputTimeList().get(i).plusSeconds((long) (0.72 * mesAll.getCheckInputAmountList().get(i)));
                    mesAll.getCheckOutputTimeList().add(outputTime);
                }


            }else{
                // 스틱일때 (검사 스케줄이 있을때)
                mesAll.setCheckCount(mesAll.getFillStickCount());

                List<LocalDateTime> timeList = factory.getCheckProcessing().getConfirmList().get(factory.getCheckProcessing().getConfirmList().size()-1).getCheckOutputTimeList();
                LocalDateTime lastTime = timeList.get(timeList.size()-1);

                for(int i = 0; i < mesAll.getCheckCount(); i++){

                    mesAll.getCheckInputAmountList().add(mesAll.getFillStickOutputAmountList().get(i));
                    mesAll.getCheckOutputAmountList().add(mesAll.getCheckInputAmountList().get(i));
                    if(i == 0){
                        //첫번째 투입
                        if(mesAll.getFillStickOutputTimeList().get(i).isAfter(lastTime)){
                            //이전공정 끝이 스케줄 보다 늦으면 시작시간은 전공정의 끝나는 시간이 기준
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillStickOutputTimeList().get(i)));
                        }else{
                            //아닐때 시작시간의 기준은 수케줄이 끝나는 시간
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, lastTime));
                        }
                    }else{
                        //n번째 투입
                        if(mesAll.getFillStickOutputTimeList().get(i).isAfter(mesAll.getCheckOutputTimeList().get(i-1))){
                            //이전공정 끝이 검사보다 늦으면 시작시간은 전공정의 끝나는 시간이 기준
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getFillStickOutputTimeList().get(i)));
                        }else{
                            //아닐때 시작시간의 기준은 수케줄이 끝나는 시간
                            mesAll.getCheckInputTimeList().add(inputTimeCheck(checkProcessingLeadTime, mesAll.getCheckOutputTimeList().get(i-1)));
                        }
                    }
                    LocalDateTime outputTime = mesAll.getCheckInputTimeList().get(i).plusSeconds((long) (0.72 * mesAll.getCheckInputAmountList().get(i)));
                    mesAll.getCheckOutputTimeList().add(outputTime);
                }
            }
        }
        return mesAll;
    }



    //포장
    MesAll packingPrecessing(MesAll mesAll){

        int ea = mesAll.getItemId() <= 2?30:25;
        int box = 0;

        mesAll.setPackingCount(mesAll.getCheckCount());
        if(factory.getPacking().getConfirmList().isEmpty()) {
            for(int i = 0; i < mesAll.getPackingCount(); i++){
                box = mesAll.getCheckOutputAmountList().get(i)/ea;

                mesAll.getPackingInputAmountList().add(mesAll.getCheckOutputAmountList().get(i));
                mesAll.getPackingOutputAmountList().add(box);

                if(i == 0){
                    mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,mesAll.getCheckOutputTimeList().get(i)));
                }else{
                    if(mesAll.getCheckOutputTimeList().get(i).isAfter(mesAll.getPackingOutputTimeList().get(i-1))){
                        //검사가 포장보다 늦게 끝나면 기준시간은 검사가 끝나는 시간
                        mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,mesAll.getCheckOutputTimeList().get(i)));
                    }else{
                        //아니면 기준시간은 포장이 끝나는 시간
                        mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,mesAll.getPackingOutputTimeList().get(i-1)));
                    }
                }
                mesAll.getPackingOutputTimeList().add(start(mesAll.getPackingInputTimeList().get(i),box));
            }
        }else{
            // 스케줄이 있을때
            for(int i = 0; i < mesAll.getPackingCount(); i++){
                box = mesAll.getCheckOutputAmountList().get(i)/ea;

                mesAll.getPackingInputAmountList().add(mesAll.getCheckOutputAmountList().get(i));
                mesAll.getPackingOutputAmountList().add(box);

                if(i == 0){
                    // 스케줄 끝나는 시간이랑 전공정 끝나는 시간 비교
                    List<LocalDateTime> timeList = factory.getPacking().getConfirmList().get(factory.getPacking().getConfirmList().size()-1).getPackingOutputTimeList();
                    LocalDateTime lastTime = timeList.get(timeList.size()-1);

                    if(lastTime.isAfter(mesAll.getCheckOutputTimeList().get(i))){
                        //스케줄의 마지막이 전공정 끝나는 시간보다 미래면? 스케줄의 끝나는 시간이 기준
                        mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,lastTime));
                    }else{
                        //아니면 전공정이 끝나는 시간이 기준
                        mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,mesAll.getCheckOutputTimeList().get(i)));
                    }
                }else {
                    //i > 0
                    if(mesAll.getCheckOutputTimeList().get(i).isAfter(mesAll.getPackingOutputTimeList().get(i-1))){
                        //검사가 포장보다 늦게 끝나면 기준시간은 검사가 끝나는 시간
                        mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,mesAll.getCheckOutputTimeList().get(i)));
                    }else{
                        //아니면 기준시간은 포장이 끝나는 시간
                        mesAll.getPackingInputTimeList().add(inputTimeCheck(packingProcessingLeadTime,mesAll.getPackingOutputTimeList().get(i-1)));
                    }
                }
                mesAll.getPackingOutputTimeList().add(start(mesAll.getPackingInputTimeList().get(i),box));
            }
        }

        return mesAll;
    }


    LocalDateTime start(LocalDateTime time, int box) {
        if(time.getHour() < 12) {
            return morning(time, box);
        }
        else {
            time = time.plusMinutes(packingProcessingLeadTime);
            return afternoon(time, box);
        }
    }

    LocalDateTime morning(LocalDateTime time, int box) {
        time = time.plusMinutes(packingProcessingLeadTime);
        int possibleSeconds = ((12 - time.getHour()) * 60 - time.getMinute()) * 60 - time.getSecond() - 1;
        int possibleBox = possibleSeconds / 18;
        if(box > possibleBox) {
            box -= possibleBox;
            time = time.withHour(13).withMinute(0).withSecond(0).withNano(0);
            return afternoon(time, box);
        }
        else {
            time = time.plusSeconds(18 * box);
            box = 0;
            return time;
        }
    }

    LocalDateTime afternoon(LocalDateTime time, int box) {
        int possibleSeconds = ((18 - time.getHour()) * 60 - time.getMinute()) * 60 - time.getSecond() - 1;
        int possibleBox = possibleSeconds / 18;
        if(box > possibleBox) {
            box -= possibleBox;
            time = checkWeekend(time);
            return morning(time, box);
        }
        else {
            time = time.plusSeconds(18 * box);
            box = 0;
            return time;
        }
    }

    static LocalDateTime checkWeekend(LocalDateTime time) {
        if(time.getDayOfWeek().getValue() == 5) {
            time = time.plusDays(3);
        }
        else {
            time = time.plusDays(1);
        }
        time = time.withHour(9).withMinute(packingProcessingLeadTime).withSecond(0).withNano(0);
        return time;
    }




    //점심시간 체크
    LocalDateTime lunchCheck(int leadTime, LocalDateTime inputPossible){

        LocalDateTime outputTime;
        LocalTime localTime = inputPossible.toLocalTime();
        LocalTime lunchStartTime = LocalTime.of(12, 00).minusMinutes(leadTime); // 시작 시간: 12시 0분이지만 리드타임을 고려해서 11시 40분으로 계산
        LocalTime lunchEndTime = LocalTime.of(13, 0); // 종료 시간: 13시 0분

        if (localTime.isAfter(lunchStartTime) && localTime.isBefore(lunchEndTime)) {
            // 리턴받은 시간이 점심시간이라면
            outputTime = inputPossible.withHour(13).withMinute(0).withSecond(0).withNano(0).plusMinutes(leadTime); // mesAll에 투입시간

        }else{
            //점심시간이 아닐때
            outputTime = inputPossible.plusMinutes(leadTime); // mesAll에 투입시간
        }
        return outputTime;
    }


    //전공정 끝난 시간을 넣으면 투입할 시간이 나오는 메서드
    LocalDateTime inputTimeCheck(int leadTime, LocalDateTime inputPossible){
        LocalDateTime outputTime;
        LocalTime localTime = inputPossible.toLocalTime();
        LocalTime startTime = LocalTime.of(9, 00); // 업무시간
        LocalTime endTime = LocalTime.of(18, 0).minusMinutes(leadTime); // 종료 시간: 18시 00분 - 리드타임

        if(localTime.isAfter(startTime) && localTime.isBefore(endTime)){
            //9시부터 18:00 - 리드타임에 종료 되었을때
            outputTime = lunchCheck(leadTime,inputPossible);
        }else{
            //전공정이 9시부터 17 40분과 사이에 종료되지 않았을때
            if(inputPossible.getDayOfWeek().getValue() == 5){
                //금요일 이면
                if(inputPossible.getHour() < 9){
                    outputTime = inputPossible.plusDays(2).withHour(9).withMinute(0).plusMinutes(leadTime).withSecond(0).withNano(0);
                }else{
                    outputTime = inputPossible.plusDays(3).withHour(9).withMinute(0).plusMinutes(leadTime).withSecond(0).withNano(0);
                }
            }else{
                if(inputPossible.getHour() < 9){
                    outputTime = inputPossible.plusDays(0).withHour(9).withMinute(0).plusMinutes(leadTime).withSecond(0).withNano(0);
                }else{
                    outputTime = inputPossible.plusDays(1).withHour(9).withMinute(0).plusMinutes(leadTime).withSecond(0).withNano(0);
                }
            }
        }
        return outputTime;
    }






    int getLiquidSystemOutputAmount(int input,MesAll mesAll){
        int output = 0;

        if(mesAll.getItemId() == 1){
            output = (int)(input * 1.6); //1000 넣으면 1600 나옴
        }else if (mesAll.getItemId() == 2){
            output = (int)(input * 2.4); //500넣으면 1200
        }else{
            output = input * 3;
        }

        return output;
    }



    void inputLiquidMachine1(MesAll mesAll,int leadTime, int i, int amount, LocalDateTime inputTIme){
        mesAll.getWhereList().add(1);
        mesAll.setLiquidSystemCount1(mesAll.getLiquidSystemCount1()+1); //회수 + 1
        mesAll.getLiquidSystemInputAmountList1().add(amount);
        mesAll.getLiquidSystemOutputAmountList1().add(getLiquidSystemOutputAmount(amount,mesAll));
        mesAll.getLiquidSystemInputTimeList1().add(inputTimeCheck(leadTime,inputTIme));

        if(mesAll.getItemId() == 1){
            mesAll.getLiquidSystemOutputTimeList1().add(mesAll.getLiquidSystemInputTimeList1().get(mesAll.getLiquidSystemInputTimeList1().size()-1).plusHours(72));
        }else if(mesAll.getItemId() == 2){
            mesAll.getLiquidSystemOutputTimeList1().add(mesAll.getLiquidSystemInputTimeList1().get(mesAll.getLiquidSystemInputTimeList1().size()-1).plusHours(24).plusSeconds((amount/10) * 3456L));
        }else{
            mesAll.getLiquidSystemOutputTimeList1().add(mesAll.getLiquidSystemInputTimeList1().get(mesAll.getLiquidSystemInputTimeList1().size()-1).plusHours(8));
        }

        mesAll.getLiquidSystemInputTimeList().add(inputTimeCheck(leadTime,inputTIme));
        mesAll.getLiquidSystemOutputTimeList().add(mesAll.getLiquidSystemOutputTimeList1().get(mesAll.getLiquidSystemOutputTimeList1().size()-1));
        mesAll.getLiquidSystemInputAmountList().add(amount);
        mesAll.getLiquidSystemOutputAmountList().add(getLiquidSystemOutputAmount(amount,mesAll));
    }

    void inputLiquidMachine2(MesAll mesAll,int leadTime, int i, int amount, LocalDateTime inputTIme){
        mesAll.getWhereList().add(2);
        mesAll.setLiquidSystemCount2(mesAll.getLiquidSystemCount2()+1); //회수 + 1
        mesAll.getLiquidSystemInputAmountList2().add(amount);
        mesAll.getLiquidSystemOutputAmountList2().add(getLiquidSystemOutputAmount(amount,mesAll));
        mesAll.getLiquidSystemInputTimeList2().add(inputTimeCheck(leadTime,inputTIme));

        if(mesAll.getItemId() == 1){
            mesAll.getLiquidSystemOutputTimeList2().add(mesAll.getLiquidSystemInputTimeList2().get(mesAll.getLiquidSystemInputTimeList2().size()-1).plusHours(72));
        }else if(mesAll.getItemId() == 2){
            mesAll.getLiquidSystemOutputTimeList2().add(mesAll.getLiquidSystemInputTimeList2().get(mesAll.getLiquidSystemInputTimeList2().size()-1).plusHours(24).plusSeconds((amount/10) * 3456L));
        }else{
            mesAll.getLiquidSystemOutputTimeList2().add(mesAll.getLiquidSystemInputTimeList2().get(mesAll.getLiquidSystemInputTimeList2().size()-1).plusHours(8));
        }

        mesAll.getLiquidSystemInputTimeList().add(inputTimeCheck(leadTime,inputTIme));
        mesAll.getLiquidSystemOutputTimeList().add(mesAll.getLiquidSystemOutputTimeList2().get(mesAll.getLiquidSystemOutputTimeList2().size()-1));
        mesAll.getLiquidSystemInputAmountList().add(amount);
        mesAll.getLiquidSystemOutputAmountList().add(getLiquidSystemOutputAmount(amount,mesAll));

    }


    void liquidExcpect(MesAll mesAll, LiquidSystem liquidSystem, int leadTime, int amount,int i){
        if(mesAll.getItemId() > 2){
            //스틱일때
            if(i == 0){
                //첫번째 투입일때
                if(liquidSystem.getConfirmList().isEmpty()){
                    //액체제조 1과 2가 전부 비었을때 1에 투입
                    inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                }else {
                    // 확정 스케줄이 있을때
                    if(liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-1).getLiquidSystemCount1()==0){
                        // 기계 1이 비었으면 기계1투입(직전 수주에서 없기때문에 그전의 수주에서 가져와야된다) (직전 수주에서 기계 2의 스케줄이 있는상황)

                        System.out.println("1로 들어옴");
                        //여기서 시간은 확정리스트 마지막
                        if(liquidSystem.getConfirmList().size() >= 2){

                            LocalDateTime lastTime = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2).getLiquidSystemOutputTimeList1().get(liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2).getLiquidSystemOutputTimeList1().size()-1);

                            if(lastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine1(mesAll,leadTime,i,amount,lastTime);

                            }else{
                                inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                            }
                        }else{
                            inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                        }

                    }else if (liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-1).getLiquidSystemCount2()==0) {

                        // 기계 2가 비었으면 기계2투입 (기계 1은 스케줄이 있는상황)
                        // 직전 확정에서 2가 없으면 그전 확정도 따져봐야함
                        System.out.println("2로 들어옴");
                        if(liquidSystem.getConfirmList().size() >= 2){

                            MesAll tmpMesAll = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2);
                            LocalDateTime lastTime = tmpMesAll.getLiquidSystemOutputTimeList2().get(tmpMesAll.getLiquidSystemOutputTimeList2().size()-1);

                            if(lastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine1(mesAll,leadTime,i,amount,lastTime);

                            }else{
                                inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                            }
                        }else{
                            inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                        }


                    }else{
                        //confirmList의 시간을 확인하고 기계1과 1계2의 마지막 시간을 찾아온다.
                        System.out.println("3으로 들어옴");
                        List<LocalDateTime> machine1List = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size() - 1).getLiquidSystemOutputTimeList1(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                        List<LocalDateTime> machine2List = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size() - 1).getLiquidSystemOutputTimeList2(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                        LocalDateTime machine1LastTime = machine1List.get(machine1List.size()-1);//기계 1 끝나는시간
                        LocalDateTime machine2LastTime = machine2List.get(machine2List.size()-1); //기계 2 끝나는 시간

                        System.out.println("머신 1라스트" + machine1LastTime);
                        System.out.println("머신 2라스트" + machine2LastTime);

                        if(machine1LastTime.isBefore(machine2LastTime)) {
                            // 기계1이 먼저끝날때
                            if(machine1LastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine1(mesAll,leadTime,i,amount,machine1LastTime);
                            }else{
                                inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                            }
                        }else{
                            //기계2가 먼저끝날때
                            if(machine2LastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine2(mesAll,leadTime,i,amount,machine2LastTime);
                            }else{
                                inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                            }
                        }
                    }
                }
            }else if (i == 1){
                // 첫번째 투입이 아닌 것의 투입 끝나는 시간을 체크해서 input하면 된다.

                if(mesAll.getLiquidSystemCount1() ==  1){
                    // 첫번째 투입이 기계 1일떄
                    if(liquidSystem.getConfirmList().isEmpty()){
                        //액체제조 1과 2가 전부 비어서 첫번째가 자동으로1로 들어가서 2로 들어갈때
                        inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                    }else{

                            // 기계 2 스케줄 완료 후 투입
                            List<LocalDateTime> machine2List = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size() - 1).getLiquidSystemOutputTimeList2(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                            LocalDateTime machine2LastTime = machine2List.get(machine2List.size()-1); //기계 2 끝나는 시간

                            if(machine2LastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine2(mesAll,leadTime,i,amount,machine2LastTime);
                            }else{
                                inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                            }

                        //}
                    }

                }else{
                    // 첫번째 투입이 기계 2일때

                        // 기계 1 스케줄 완료 후 투입
                        List<LocalDateTime> machine1List = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size() - 1).getLiquidSystemOutputTimeList1(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                        LocalDateTime machine1LastTime = machine1List.get(machine1List.size()-1); //기계 1 끝나는 시간

                        if(machine1LastTime.isAfter(mesAll.getOutputMeasurementTime())){
                            inputLiquidMachine1(mesAll,leadTime,i,amount,machine1LastTime);
                        }else{
                            inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputMeasurementTime());
                        }
                }
            }else{
                // - 2 의 끝나는 시간을 input 타임으로 잡으면 된다.
                if(mesAll.getWhereList().get(mesAll.getWhereList().size()-1) == 1){
                    //직전 결과의 투입이 기계1일때
                    inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getLiquidSystemOutputTimeList2().get(mesAll.getLiquidSystemOutputTimeList2().size()-1));
                }else{
                    //직전 결과의 투입이 기계2일때
                    inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getLiquidSystemOutputTimeList1().get(mesAll.getLiquidSystemOutputTimeList1().size()-1));
                }
            }
        }else{
            // 양배추나 흑마늘일때
            if(i == 0){
                //첫번째 투입일때
                System.out.println("양배추나 흑마늘일때");
                if(liquidSystem.getConfirmList().isEmpty()){
                    //액체제조 1과 2가 전부 비었을때 1에 투입
                    System.out.println("--- 둘다 비었을때");
                    inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                }else {
                    // 확정 스케줄이 있을때
                    if(liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-1).getLiquidSystemCount1()==0){
                        // 기계 1이 비었으면 기계1투입 (기계 2는 스케줄이 있는상황)
                        System.out.println("--- 기계1이 비었을때");

                        if(liquidSystem.getConfirmList().size() >= 2){

                            LocalDateTime lastTime = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2).getLiquidSystemOutputTimeList1().get(liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2).getLiquidSystemOutputTimeList1().size()-1);

                            if(lastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine1(mesAll,leadTime,i,amount,lastTime);

                            }else{
                                inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                            }
                        }else{
                            inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                        }


                    }else if (liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-1).getLiquidSystemCount2()==0){
                        // 기계 2가 비었으면 기계2투입 (기계 1은 스케줄이 있는상황)
                        // 기계 2는 그 전의 수주정보에서 가져와야 된다.
                        System.out.println("--- 기계2가 비었을때");
                        System.out.println(liquidSystem.getConfirmList());

                        inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                        if(liquidSystem.getConfirmList().size() >= 2){

                            LocalDateTime lastTime = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2).getLiquidSystemOutputTimeList2().get(liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size()-2).getLiquidSystemOutputTimeList2().size()-1);

                            if(lastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine1(mesAll,leadTime,i,amount,lastTime);

                            }else{
                                inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                            }
                        }else{
                            inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                        }

                    }else{
                        //confirmList의 시간을 확인하고 기계1과 1계2의 마지막 시간을 찾아온다.
                        List<LocalDateTime> machine1List = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size() - 1).getLiquidSystemOutputTimeList1(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                        List<LocalDateTime> machine2List = liquidSystem.getConfirmList().get(liquidSystem.getConfirmList().size() - 1).getLiquidSystemOutputTimeList2(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                        LocalDateTime machine1LastTime = machine1List.get(machine1List.size()-1);//기계 1 끝나는시간
                        LocalDateTime machine2LastTime = machine2List.get(machine2List.size()-1); //기계 2 끝나는 시간
                        System.out.println("--- 둘다 안비었을때");
                        if(machine1LastTime.isBefore(machine2LastTime)) {
                            // 기계1이 먼저끝날때
                            if(machine1LastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine1(mesAll,leadTime,i,amount,machine1LastTime);
                            }else{
                                inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                            }

                        }else{
                            //기계2가 먼저끝날때
                            if(machine2LastTime.isAfter(mesAll.getOutputMeasurementTime())){
                                inputLiquidMachine2(mesAll,leadTime,i,amount,machine2LastTime);
                            }else{
                                inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));

                            }
                        }
                    }
                }
            }else if (i == 1){
                // 첫번째 투입이 아닌 것의 투입 끝나는 시간을 체크해서 input하면 된다.

                if(mesAll.getLiquidSystemCount1() ==  1){
                    // 첫번째 투입이 기계 1일떄
                    if(liquidSystem.getConfirmList().isEmpty()){
                        //액체제조 1과 2가 전부 비어서 첫번째가 자동으로1로 들어가서 2로 들어갈때
                        if(mesAll.getItemId() == 2){
                            inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i-1));
                        }else{
                            inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getOutputPreProcessingTimeList().get(i));
                        }
                    }else{
                            //첫번째 기계가 투입 1임 그런데 기계 2가 끝났는지 확인해야될때

                        LocalDateTime machine2LastTime; //기계 2 끝나는 시간
                        int j = liquidSystem.getConfirmList().size() - 1;

                        while(true){

                            if(liquidSystem.getConfirmList().get(j).getLiquidSystemCount2()!=0){
                                List<LocalDateTime> machine2List = liquidSystem.getConfirmList().get(j).getLiquidSystemOutputTimeList2(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                                machine2LastTime = machine2List.get(machine2List.size()-1);
                                break;
                            }
                            j--;
                        }


                            if(mesAll.getItemId() == 2){
                                //흑마늘 일때
                                if(machine2LastTime.isAfter(mesAll.getOutputPreProcessingTimeList().get(i-1))){
                                    inputLiquidMachine2(mesAll,leadTime,i,amount,machine2LastTime);
                                }else {
                                    inputLiquidMachine2(mesAll, leadTime, i, amount, mesAll.getOutputPreProcessingTimeList().get(i-1));
                                }
                            }else{
                                //양배추일때
                                if(machine2LastTime.isAfter(mesAll.getOutputPreProcessingTimeList().get(i))){
                                    inputLiquidMachine2(mesAll,leadTime,i,amount,machine2LastTime);
                                }else {
                                    inputLiquidMachine2(mesAll, leadTime, i, amount, mesAll.getOutputPreProcessingTimeList().get(i));
                                }
                            }
                        }
                }else {
                    // 첫번째 투입이 기계 2일때
                    // 기계 1 스케줄 완료 후 투입

                    LocalDateTime machine1LastTime;
                    int j = liquidSystem.getConfirmList().size() - 1;

                    while(true){

                        if(liquidSystem.getConfirmList().get(j).getLiquidSystemCount2()!=0){
                            List<LocalDateTime> machine1List = liquidSystem.getConfirmList().get(j).getLiquidSystemOutputTimeList1(); //액체제조 확정리스트 맨마지막의 아웃풋 타임리스트
                            machine1LastTime = machine1List.get(machine1List.size()-1);
                            break;
                        }
                        j--;
                    }

                    // 기계 1이 스케줄 완료 비교 후 기계1투입
                    if (mesAll.getItemId() == 2) {
                        if (machine1LastTime.isAfter(mesAll.getOutputPreProcessingTimeList().get(i - 1))) {
                            inputLiquidMachine1(mesAll, leadTime, i, amount, machine1LastTime);
                        } else {
                            inputLiquidMachine1(mesAll, leadTime, i, amount, mesAll.getOutputPreProcessingTimeList().get(i - 1));
                        }
                    } else {
                        if (machine1LastTime.isAfter(mesAll.getOutputPreProcessingTimeList().get(i))) {
                            inputLiquidMachine1(mesAll, leadTime, i, amount, machine1LastTime);
                        } else {
                            inputLiquidMachine1(mesAll, leadTime, i, amount, mesAll.getOutputPreProcessingTimeList().get(i));
                        }
                    }
                }
            }else{
                // - 2 의 끝나는 시간을 input 타임으로 잡으면 된다.
                if(mesAll.getWhereList().get(mesAll.getWhereList().size()-1) == 1){
                    //직전 결과의 투입이 기계1일때
                    inputLiquidMachine2(mesAll,leadTime,i,amount,mesAll.getLiquidSystemOutputTimeList2().get(mesAll.getLiquidSystemOutputTimeList2().size()-1));
                }else{
                    //직전 결과의 투입이 기계2일때
                    inputLiquidMachine1(mesAll,leadTime,i,amount,mesAll.getLiquidSystemOutputTimeList1().get(mesAll.getLiquidSystemOutputTimeList1().size()-1));
                }
            }
        }
    }


}

