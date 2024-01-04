package team_2p4p.mes.util.calculator;

/*
 * 1. 제품에 따른 필요 자재량 확인
 * 2. 재고량과 비교하여 부족 수량 확인
 * 3. 부족 수량에 따른 최소 주문 단위로 주문
 * 4. 주문시 도착 시간 계산
 */


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.OrderMaterial;
import team_2p4p.mes.entity.Product;
import team_2p4p.mes.service.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalcOrderMaterial {
    static MesAll mesAll;

    private final ProductService productService;
    private final ItemService itemService;
    private final OrderMaterialService orderMaterialService;
    private final MaterialService materialService;

    Calculator cal = new Calculator();
    public MesAll test(long itemId, int amount) {
        System.out.println("인풋 amount" + amount);
        mesAll = new MesAll();
        LocalDateTime now = LocalDateTime.now();   // 수주시간
        LocalDateTime time;

        mesAll.itemId = itemId;

        // 수주량과 완성품 재고량 비교
        int comparedAmount = compareStockedProduct(itemId, amount);
        // 재고량이 충분한 경우 comparedAmount < 0

        // 23.05.23 db관련
        Item item = itemService.findItemById(itemId);
        Product product = productService.lastStock(itemId);
        if (comparedAmount < 0) {
            mesAll.stockEnough = true;
            // mesAll에 대한 값이 tae에게 넘어갈 필요가 없음.
            // 이부분에 대한 부분 추가 고려 필요
            // 23.05.23 db에 재고에서 수주량 차감
            productService.addMinusProductStock(item, (long) amount, product.getMakeDate(), product.getLotLogId());
        } else {
            // 수주량이 재고량보다 많은 경우 am > 0
            // 재고수량 0으로 만들어줌 -> 출하로 넘겨야 함
            // 23.05.23 db에 재고에서 수주량 차감
            Long stock = productService.productStock(itemId);
            if(stock != 0)
                productService.addMinusProductStock(item, stock, product.getMakeDate(), product.getLotLogId());
            // 부족한 만큼에 대한 재료들이 발주 필요 데이터베이스(현재 orderList)에 저장
            saveOrderList(itemId, comparedAmount, now);
            // estimateDate를 사용해야할거같기는 한데...
            mesAll.amount = calcEstimateAmount(itemId, comparedAmount);
            // 메인재료 도착예정일 계산
            time = estimateDate(itemId, comparedAmount, now).time;
            long orderId = orderMaterialService.findOrderId(itemId + 8);
            mesAll.orderId = orderId;
            mesAll.time = time;
        }
        System.out.println("아웃풋 amount" + mesAll.amount);

        return mesAll;
    }

    MesAll sendTae() {
        return mesAll;
    }

    // 수주 확정 전 날짜 예측을 위한 매소드
    public MesAll estimateDate(Long itemId, int amount, LocalDateTime now) {
        MesAll mesAll = new MesAll();
        mesAll.itemId = itemId;
        long materialId = itemId + 8;   // 원자재 id는 제품 id에 +8 해줌

        int comparedAmount = compareStockedProduct(itemId, amount);

        if (comparedAmount < 0) {
            mesAll.time = now;
            mesAll.amount = 0;
            mesAll.stockEnough = true;

        } else {
            // 수주량에서 재고량을 뺀 양만큼이 amount 가 되어야함
            amount = comparedAmount;

            // 수주받은 제품 메인 재료 원자재 량 계산
            if (itemId == 1) {
                amount *= 1.5;
            } else if (itemId == 2) {
                amount /= 4;
            } else {
                amount /= 8;
            }

            // obatainList에 동일 아이템이 이미 존재하는 경우(obtainList[0] : itemId, obtainList[1] : 아이템의 원자재 량)
            //  -> 발주db에서 받아와야함
            // already에 메인 원자재량을 추가해줌

            int already = 0;
            int maxOrder = minMaxOrder(materialId)[1];

            OrderMaterial orderMaterial = orderMaterialService.checkOrderMaterial(materialId);

            if(orderMaterial != null) {
                already = Math.toIntExact(orderMaterial.getOrderItemAmount());
            }

            int totalOrderAmount = already + amount;

            // 추가 주문이 필요한 경우(ex 기존 1000kg 주문해야하는데 2000kg을 주문해야하는경우)
            //  -> mesAll 추가로 발송
            // 추가 주문이 필요 없는 경우(ex 기존 750kg만 필요해서 최소 1000kg을 맞추기 위해 1000kg을 주문했으나, 250kg치가 추가로 수주된 경우)
            //  -> mesAll 추가발송 x or amount를 0으로 발송
            int beforeAmount = calcEstimateAmount(materialId, already);
            int afterAmount = calcEstimateAmount(materialId, totalOrderAmount);
            int sendAmount = afterAmount - beforeAmount;
            mesAll.amount = sendAmount;

            if (totalOrderAmount <= maxOrder) {
                mesAll.time = calcEstimateDate(materialId, now);
            } else {
                if (totalOrderAmount % maxOrder == 0) {
                    now = calcWeekend(now, totalOrderAmount / maxOrder);
                    mesAll.time = calcEstimateDate(materialId, now);
                } else {
                    now = calcWeekend(now, totalOrderAmount / maxOrder + 1);
                    mesAll.time = calcEstimateDate(materialId, now);
                }
            }
        }
        System.out.println("리턴전");
        System.out.println(mesAll.getAmount());
        return mesAll;
    }

    static int calcEstimateAmount(Long itemId, int amount) {
        int minAmount = minMaxOrder(itemId)[0];
        if (amount % minAmount != 0) {
            amount = (amount / minAmount + 1) * minAmount;
        }
        return amount;
    }

    static LocalDateTime calcWeekend(LocalDateTime date, int plusDays) {
        if (plusDays < 5) {
            date = date.plusDays(plusDays);
            if (date.getDayOfWeek().getValue() > 5) {
                date.plusDays(2);
            }
            return date;
        } else {
            date = date.plusDays(7);
            return calcWeekend(date, plusDays - 5);
        }
    }

    static LocalDateTime calcEstimateDate(Long itemId, LocalDateTime date) {
        LocalDateTime expectDate;

        if (itemId < 11) {
            if (date.getHour() < 12) {
                expectDate = calcImportExpectDate(itemId, date);
            } else {
                date = calcWeekend(date, 1);
                expectDate = calcImportExpectDate(itemId, date);
            }
        } else {
            if (date.getHour() < 15) {
                expectDate = calcImportExpectDate(itemId, date);
            } else {
                date = calcWeekend(date, 1);
                expectDate = calcImportExpectDate(itemId, date);
            }
        }

        return expectDate;
    }

    // 완제품 재고와 주문량 비교 매소드
    int compareStockedProduct(long itemId, int amount) {
        // 23.05.23 db에서 가져오는 내용 추가
        Long stock = productService.productStock(itemId);
        return (int) (amount - stock);
    }

    // 부족한 원자재 발주 필요 데이터베이스에 저장하는 매소드
    void saveOrderList(long itemId, int amount, LocalDateTime now) {
        // 발주 필요량 계산
        // 23.05.24 db에 추가 및 업데이트 내용 추가
        Map<String, Object> needItem = needItem(itemId, amount);
        long materialId = itemId + 8;   // 원자재 아이디 = 아이템아이디 + 8;
        Long materialAmount = Long.parseLong(String.valueOf(needItem.get("material")));

        orderMaterialService.saveOrderMaterial(materialId, materialAmount, now, calcEstimateDate(materialId, now));
    }

    // 제품을 생산하기 위해 필요한 자재와 그에 따른 양 계산 매소드
    static Map<String, Object> needItem(long itemId, int amount) {
        int material = 0;            // 필요한 메인 원자재 양(kg)
        int pouch = 0;               // 필요한 포장재 개수(ea)
        int collagen = 0;            // 필요한 콜라겐 양(kg)
        int box = amount;            // 필요한 박스 개수(ea)

        Map<String, Object> needItem = new HashMap<>();

        if (itemId == 1) {
            material = (int) (amount * 1.5);         // 1box 당 양배추 1.5kg 필요
            pouch = amount * 30;               // 1box 당 파우치 30ea 필요
        } else if (itemId == 2) {
            material = (int) (amount * 0.25);      // 1box 당 흑마늘 0.25kg 필요
            pouch = amount * 30;               // 1box 당 파우치 30ea 필요
        } else {
            material = (int) (amount * 0.125);      // 1box 당 젤리 0.125kg 필요
            pouch = amount * 25;               // 1box 당 스틱파우치 25ea 필요
            collagen = (int) (amount * 0.05);      // 1box 당 콜라겐 0.05kg 필요
            needItem.put("collagen", collagen);
        }
        needItem.put("material", material);
        needItem.put("pouch", pouch);
        needItem.put("box", box);

        return needItem;
    }

    // 각 자재별 최소, 최대 주문량
    static int[] minMaxOrder(long itemId) {
        int[] range = new int[2];         // range[0] : 최소 주문량, range[1] : 최대 주문량

        switch ((int) itemId){
            case 9 :         // 양배추
                range[0] = 1000;
                range[1] = 5000;
            break;
            case 10 :      // 흑마늘
                range[0] = 10;
                range[1] = 5000;
            break;
            case 11 :      // 석류액기스
                range[0] = 5;
                range[1] = 500;
            break;
            case 12 :      // 매실액기스
                range[0] = 5;
                range[1] = 500;
            break;
            case 14 :      // 콜라겐
                range[0] = 5;
                range[1] = 500;
            break;
            case 15 :      // 파우치
                range[0] = 1000;
                range[1] = 1000000;
            break;
            case 16 :      // 스틱파우치
                range[0] = 1000;
                range[1] = 1000000;
            break;
            default :      // 포장box
                range[0] = 500;
                range[1] = 10000;
        }
        return range;
    }

    // 원자재 주문은 11:59, 14:59에 일괄주문으로 지정
    // 원자재 주문은 평일만 가능
    static LocalDateTime calcImportExpectDate(long itemId, LocalDateTime time) {

        int plusDays = 2;
        if (itemId > 2 && itemId < 6) {
            plusDays = 3;
        }

        time = time.plusDays(plusDays);                  // 배송시간 추가

        // getDayOfWeek 1 ~ 7 : 월 ~ 일
        int day = time.getDayOfWeek().getValue();
        if (day < 4) {
            time = time.plusDays(3 - day);
        } else if (day < 6) {
            time = time.plusDays(5 - day);
        } else if (day == 6) {
            time = time.plusDays(2);
        } else {
            time = time.plusDays(3);
        }

        time = time.withHour(10);
        time = time.withMinute(0);
        time = time.withSecond(0);
        time = time.withNano(0);

        return time;
    }

    public void useMaterial(MesAll mesAll, long inputAmount){
        materialService.useMaterial(mesAll.getItemId(), mesAll.getOrderId(), inputAmount);
    }

    @Scheduled(cron = "10 0 0 * * ?")
    public void inputMaterialSchedule() {
        List<OrderMaterial> orderMaterialList = orderMaterialService.finishOrderMaterial();
        for(OrderMaterial orderMaterial : orderMaterialList) {
            orderMaterialService.saveFinishOrderMaterial(orderMaterial);
            materialService.addInputMaterial(orderMaterial);
        }
    }

    // 스케줄러 자동 실행 부분
    @Scheduled(cron = "11 55 0 * * ?")   // 매일 12시 전에 실행
    public void morningOrderMaterialSchedule() {
        if(LocalDateTime.now().getDayOfWeek().getValue() <= 5) {
            // 발주대기 아이템중 itemId가 1보다 크거나 같고 2보다 작거나 같은 아이템 가져옴
            for (int i = 9; i <= 10; i++) {
                orderConfirmItem(i);               // 주문 매서드 실행
            }
        }
    }

    @Scheduled(cron = "14 55 0 * * ?")   // 매일 15시 전에 실행
    public void afternoonOrderMaterialSchedule() {
        if(LocalDateTime.now().getDayOfWeek().getValue() <= 5) {
            for (int i = 11; i <= 12; i++) {
                orderConfirmItem(i);
            }
            // 주문한 메인재료를 기준으로 부자재 주문 수량 조정
            adjustItems();
            // 15시 이전 부자재 주문
            for (int i = 14; i <= 17; i++) {
                orderConfirmItem(i);
            }
        }
    }

    void orderConfirmItem(long itemId) {
        if(orderMaterialService.checkOrderMaterial(itemId) != null) {
            OrderMaterial orderMaterial = orderMaterialService.checkOrderMaterial(itemId);
            LocalDateTime now = LocalDateTime.now();           // 발주시간
            Long amount = orderMaterial.getOrderItemAmount();    // 주문량
            int[] range = minMaxOrder((int) itemId);            // range[0] : 최소 주문량, range[1] : 최대 주문량

            if (amount < range[1]) {
                if (amount % range[0] != 0) {
                    // 이 상황이면 amount를 조정해서 발주 확정으로 바꿔준다
                    amount = (amount / range[0] + 1) * range[0];
                }
                // 발주 확정으로 바꾸는 매소드 실행
                orderMaterialService.confirmOrderMaterial(orderMaterial, amount, now, calcImportExpectDate(itemId, now));
            } else {
                orderMaterialService.confirmOrderMaterial(orderMaterial, (long) range[1], now, calcImportExpectDate(itemId, now));
                orderMaterialService.saveOrderMaterial(itemId, amount - range[1], now, calcImportExpectDate(itemId, now.plusDays(1)));
                // 필요주문량(orderList) 에 금일 주문량(todayOrderList) 빼줌.
            }
        }
    }

    void adjustItems() {
        long[] orderList = new long[18];
        for(int i = 0; i < 18; i++){
            orderList[i] = 0L;
        }
        LocalDateTime now = LocalDateTime.now();
        // 원자재 담을 배열 생성
        List<OrderMaterial> orderMaterialList = orderMaterialService.todayOrderMaterial();
        // 금일 주문한 원자재가 있을경우 배열에 저장
        for (int i = 0; i < orderMaterialList.size(); i++) {
            System.out.println("Scheduler 클래스 adjustItems의 값 확인용 : " + Math.toIntExact(orderMaterialList.get(i).getItem().getItemId()));
            orderList[Math.toIntExact(orderMaterialList.get(i).getItem().getItemId())] = orderMaterialList.get(i).getOrderItemAmount();
            // orderMaterialList의 i번째 값의 itemId에 해당하는 orderList에 oderMaterial 저장
        }

        // 금일 주문한 원자재가 있을 경우 필요 부자재 양을 계산
        // 필요 부자재양을 계산
        // 주문대기에 부자재가 있을 경우 주문대기에 값을 추가
        // 재고에 부자재가 있을 경우 값을 차감
        // 없을 경우 원자재수에 따른 부자재 주문

        Long amount;
        Long addAmount;         // 금일 원자재 주문에 따른 필요량
        Long alreadyAmount;     // 주문대기중인 부자재가 있을 경우
        Long stockAmount;       // 부자재 재고가 있을경우 stockAmount에 저장

        if(orderList[11] != 0 || orderList[12] != 0){
            addAmount = (orderList[11] + orderList[12]) * 2 / 5;
            alreadyAmount = 0L;
            if (orderMaterialService.checkOrderMaterial(14L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(14L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(14L);
            amount = alreadyAmount + addAmount - stockAmount;
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(14L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(14L, amount, now, calcImportExpectDate(14L, now));
                    materialService.useStockMaterial(14L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(14L, amount, now, calcImportExpectDate(14L, now));
        }
        if (orderList[9] != 0 || orderList[10] != 0) {
            alreadyAmount = 0L;
            addAmount = 20 * orderList[9] + 120 * orderList[10];
            if (orderMaterialService.checkOrderMaterial(15L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(15L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(15L);
            amount = alreadyAmount + addAmount - stockAmount;
            System.out.println("주문 해야 할 포장지 양 : " + amount);
            // 주문해야할 총 부자재양
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(15L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(15L, amount, now, calcImportExpectDate(15L, now));
                    materialService.useStockMaterial(15L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(15L, amount, now, calcImportExpectDate(15L, now));
        }
        if (orderList[11] != 0 || orderList[12] != 0) {
            alreadyAmount = 0L;
            addAmount = (orderList[11] + orderList[12]) * 200;
            if (orderMaterialService.checkOrderMaterial(16L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(16L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(16L);
            amount = alreadyAmount + addAmount - stockAmount;
            // 주문해야할 총 부자재양
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(16L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(16L, amount, now, calcImportExpectDate(16L, now));
                    materialService.useStockMaterial(16L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(16L, amount, now, calcImportExpectDate(16L, now));
        }

        if (orderList[9] != 0 || orderList[10] != 0 || orderList[11] != 0 || orderList[12] != 0) {
            alreadyAmount = 0L;
            addAmount = orderList[9] * 2 / 3 +  orderList[10] * 4 + (orderList[11] + orderList[12]) * 8;
            if (orderMaterialService.checkOrderMaterial(17L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(17L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(17L);
            amount = alreadyAmount + addAmount - stockAmount;
            // 주문해야할 총 부자재양
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(17L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(17L, amount, now, calcImportExpectDate(17L, now));
                    materialService.useStockMaterial(17L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(17L, amount, now, calcImportExpectDate(17L, now));
        }
    }
}