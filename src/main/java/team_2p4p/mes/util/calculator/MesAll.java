package team_2p4p.mes.util.calculator;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MesAll {

    public MesAll(){
        inputPreProcessingTimeList = new ArrayList<>(); //전처리
        outputPreProcessingTimeList = new ArrayList<>(); //
        preProcessingAmountList = new ArrayList<>();
        preProcessingMapList = new ArrayList<>();

        liquidSystemInputTimeList1 = new ArrayList<>(); // 액체제조1 1회당 시작시간
        liquidSystemOutputTimeList1 = new ArrayList<>(); //액체제조1 1회당 끝난시간
        liquidSystemInputAmountList1 = new ArrayList<>(); //액체제조1 1회당 인풋양
        liquidSystemOutputAmountList1 = new ArrayList<>(); //액체제조1 1회당 아웃양

        liquidSystemInputTimeList2 = new ArrayList<>(); // 액체제조2 1회당 시작시간
        liquidSystemOutputTimeList2 = new ArrayList<>(); //액체제조2 1회당 끝난시간
        liquidSystemInputAmountList2 = new ArrayList<>(); //액체제조2 1회당 인풋양
        liquidSystemOutputAmountList2 = new ArrayList<>(); //액체제조2 1회당 아웃양
        whereList = new ArrayList<>();
        liquidSystemInputTimeList = new ArrayList<>(); // 액체제조 1회당 시작시간
        liquidSystemOutputTimeList = new ArrayList<>(); //액체제조 1회당 끝난시간
        liquidSystemInputAmountList = new ArrayList<>(); //액체제조 1회당 인풋양
        liquidSystemOutputAmountList = new ArrayList<>(); //액체제조 1회당 아웃양

        fillPouchInputTimeList = new ArrayList<>(); // 즙충진 1회당 시작시간
        fillPouchOutputTimeList = new ArrayList<>(); //즙충진 1회당 끝난시간
        fillPouchInputAmountList = new ArrayList<>(); //즙충진 1회당 인풋
        fillPouchOutputAmountList = new ArrayList<>(); //즙충진 1회당 아웃풋

        fillStickInputTimeList = new ArrayList<>(); // 스틱충진 1회당 시작시간
        fillStickOutputTimeList = new ArrayList<>(); //스틱충진 1회당 끝난시간
        fillStickInputAmountList = new ArrayList<>(); //스틱충진 1회당 인풋
        fillStickOutputAmountList = new ArrayList<>(); //스틱충진 1회당 아웃풋

        checkInputTimeList = new ArrayList<>(); // 검사 1회당 시작시간
        checkOutputTimeList = new ArrayList<>(); // 검사 1회당 끝난시간
        checkInputAmountList = new ArrayList<>(); //검사 1회당 인풋
        checkOutputAmountList = new ArrayList<>(); //검사 1회당 아웃풋
        packingInputTimeList = new ArrayList<>(); // 포장 1회당 시작시간
        packingOutputTimeList = new ArrayList<>();; // 포장 1회당 끝난시간
        packingInputAmountList = new ArrayList<>();; // 포장 1회당 인풋
        packingOutputAmountList = new ArrayList<>();; // 포장 1회당 아웃풋
        remainAmountList = new ArrayList<>(); //포장 1회당 남은량

    }


    boolean stockEnough;            // 완성품 재고 충분한 경우 : true, 부족한 경우 : false;

    // 발주 주문 종합을 위한 배열
//    static int[] orderList = new int[9];
//   static int[] orderList = {0, 0, 100, 300, 50, 0, 0, 0, 0};

    // 금일 발주 진행을 위한 배열
//    static int[] todayOrderList = new int[9];


    // uk -> tae
    long itemId; // 원료계량 전에 return 받은 itemId 0,1,2,3
    double amount; // 원료계량 전에 return 받은 amount 원료계량 후에도 이값을 return함
    LocalDateTime time;
    long orderId;


    private double measurementAmount; // 원료 계량 투입량
    private LocalDateTime inputMeasurementTime; // 원료계량 투입시간
    private  LocalDateTime outputMeasurementTime; // 원료계량 끝나는 시간



    private  int preProcessingCount; //전처리 몇번해야하는지    원료 계량 투입량/1000
    private List<LocalDateTime> inputPreProcessingTimeList; // 전처리 1회당 시작시간
    private List<LocalDateTime> outputPreProcessingTimeList; //전처리 1회당 끝난시간
    private List<Integer> preProcessingAmountList; //전처리 1회당 양
    private List<Map<String, Object>> preProcessingMapList; //전처리 모든 정보



    //액체제조 관련
    private int totalLiquidSystemCount; // 액체제조 총 몇번 돌리는지
    private int liquidSystemCount1; //액체제조1에 몇번 돌려야 하는지
    private List<LocalDateTime> liquidSystemInputTimeList1; // 액체제조 1회당 시작시간
    private List<LocalDateTime> liquidSystemOutputTimeList1; //액체제조 1회당 끝난시간
    private List<Integer> liquidSystemInputAmountList1; //액체제조 1회당 인풋
    private List<Integer> liquidSystemOutputAmountList1; //액체제조 1회당 아웃풋
    private int liquidSystemCount2; //액체제조2에 몇번 돌려야 하는지
    private List<LocalDateTime> liquidSystemInputTimeList2; // 액체제조2 1회당 시작시간
    private List<LocalDateTime> liquidSystemOutputTimeList2; //액체제조2 1회당 끝난시간
    private List<Integer> liquidSystemInputAmountList2; //액체제조2 1회당 인풋
    private List<Integer> liquidSystemOutputAmountList2; //액체제조2 1회당 아웃풋
    private List<Integer> whereList;//어디로 갔는지
    private List<LocalDateTime> liquidSystemInputTimeList; // 액체제조 1회당 시작시간
    private List<LocalDateTime> liquidSystemOutputTimeList; //액체제조 1회당 끝난시간
    private List<Integer> liquidSystemInputAmountList; //액체제조 1회당 인풋
    private List<Integer> liquidSystemOutputAmountList; //액체제조 1회당 아웃풋
    


    //즙충진관련
    private int fillPouchCount;
    private List<LocalDateTime> fillPouchInputTimeList; // 즙충진 1회당 시작시간
    private List<LocalDateTime> fillPouchOutputTimeList; //즙충진 1회당 끝난시간
    private List<Integer> fillPouchInputAmountList; //즙충진 1회당 인풋
    private List<Integer> fillPouchOutputAmountList; //즙충진 1회당 아웃풋
    


    //스틱충진
    private int fillStickCount;
    private List<LocalDateTime> fillStickInputTimeList; // 스틱충진 1회당 시작시간
    private List<LocalDateTime> fillStickOutputTimeList; //스틱충진 1회당 끝난시간
    private List<Integer> fillStickInputAmountList; //스틱충진 1회당 인풋
    private List<Integer> fillStickOutputAmountList; //스틱충진 1회당 아웃풋



    //검사
    private int checkCount; //검사 count
    private List<LocalDateTime> checkInputTimeList; // 검사 1회당 시작시간
    private List<LocalDateTime> checkOutputTimeList; //검사 1회당 끝난시간
    private List<Integer> checkInputAmountList; //검사 1회당 인풋
    private List<Integer> checkOutputAmountList; //검사 1회당 아웃풋


    //포장
    private int packingCount;
    private List<LocalDateTime> packingInputTimeList; // 포장 1회당 시작시간
    private List<LocalDateTime> packingOutputTimeList; //포장 1회당 끝난시간
    private List<Integer> packingInputAmountList; //포장 1회당 인풋
    private List<Integer> packingOutputAmountList; //포장 1회당 아웃풋
    private List<Integer> remainAmountList; //포장 1회당 남은량

    // 예상납품일
    private LocalDateTime estimateDate;
    void infoMeasurement(){
        System.out.println("============MESAll infoMeasurement===============");
        System.out.println("투입량 " + measurementAmount);
        System.out.println("itemid " + itemId);
        System.out.println("리턴받은 시간 " + time);
        System.out.println("투입시간 " + inputMeasurementTime);
        System.out.println("끝나는 시간" + outputMeasurementTime);

    }
    void infoPreProcessing(){
        System.out.println("============MESAll infoPreProcessing===============");
        System.out.println("투입량 " + measurementAmount);
        System.out.println("전처리 투입 대기시작시간 " + outputMeasurementTime);
        System.out.println("전처리 시작시간 " + inputPreProcessingTimeList);
        System.out.println("전처리 완료시간 " + outputPreProcessingTimeList);
        System.out.println("아웃풋 양" + preProcessingAmountList);


    }


    void infoLiquidSystem(){
        System.out.println("============액체제조===============");

        System.out.println("1번기계 투입회수" + liquidSystemCount1);
        System.out.println("2번기계 투입회수" + liquidSystemCount2);
        System.out.println("1번기계 투입량 리스트" + liquidSystemInputAmountList1);
        System.out.println("2번기계 투입량 리스트" + liquidSystemInputAmountList2);
        System.out.println("1번기계 생산량 리스트" + liquidSystemOutputAmountList1);
        System.out.println("2번기계 생산량 리스트" + liquidSystemOutputAmountList2);
        System.out.println("1번기계 투입시간 리스트" + liquidSystemInputTimeList1);
        System.out.println("2번기계 투입시간 리스트" + liquidSystemInputTimeList2);
        System.out.println("1번기계 완료시간 리스트" + liquidSystemOutputTimeList1);
        System.out.println("2번기계 완료시간 리스트" + liquidSystemOutputTimeList2);
    }

    void infoFillPouchProcessing(){
        System.out.println("=============즙충진===================");
        System.out.println("투입회수" + fillPouchCount);
        System.out.println("투입량" + fillPouchInputAmountList);
        System.out.println("생산량" + fillPouchOutputAmountList);
        System.out.println("투입시간" + fillPouchInputTimeList);
        System.out.println("생산완료시간" + fillPouchOutputTimeList);
    }
    void infoFillStickProcessing(){
        System.out.println("=============스틱충진===================");
        System.out.println("투입회수" + fillStickCount);
        System.out.println("투입량" + fillStickInputAmountList);
        System.out.println("생산량" + fillStickOutputAmountList);
        System.out.println("투입시간" + fillStickInputTimeList);
        System.out.println("생산완료시간" + fillStickOutputTimeList);
    }

    void infoCheckProcessing(){
        System.out.println("==============검사================");
        System.out.println("투입회수" + checkCount);
        System.out.println("투입량" + checkInputAmountList);
        System.out.println("생산량" + checkOutputAmountList);
        System.out.println("투입시간" + checkInputTimeList);
        System.out.println("검사완료시간" + checkOutputTimeList);
    }

    void infoPacking(){
        System.out.println("==============포장================");
        System.out.println("투입회수" + packingCount);
        System.out.println("투입시간" + packingInputTimeList);
        System.out.println("포장완료시간" + packingOutputTimeList);
        System.out.println("투입량" + packingInputAmountList);
        System.out.println("완료량" + packingOutputAmountList);
    }


    void infoAll(){
        infoMeasurement();
        infoPreProcessing();
        infoLiquidSystem();
        infoFillPouchProcessing();
        infoFillStickProcessing();
        infoCheckProcessing();
        infoPacking();
        System.out.println("==========예상납품일==========");
        System.out.println(estimateDate);
    }
}
