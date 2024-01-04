package team_2p4p.mes;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team_2p4p.mes.entity.OrderMaterial;
import team_2p4p.mes.service.MaterialService;
import team_2p4p.mes.service.OrderMaterialService;

import java.time.LocalDateTime;

import java.util.List;
// 23.05.24 자재 발주 스케줄러
@Component
public class Scheduler {

    OrderMaterialService orderMaterialService;
    MaterialService materialService;

    @Scheduled(cron = "11 55 0 * * ?")   // 매일 12시 전에 실행
    public void morningOrderMaterialSchedule() {
        if(LocalDateTime.now().getDayOfWeek().getValue() <= 5) {
            // 발주대기 아이템중 itemId가 1보다 크거나 같고 2보다 작거나 같은 아이템 가져옴
            for (int i = 1; i <= 2; i++) {
                orderItem(i);               // 주문 매서드 실행
            }
        }
    }
    @Scheduled(cron = "14 55 0 * * ?")   // 매일 15시 전에 실행
    public void afternoonOrderMaterialSchedule() {
        if(LocalDateTime.now().getDayOfWeek().getValue() <= 5) {
            for (int i = 3; i <= 4; i++) {
                orderItem(i);
            }
            // 주문한 메인재료를 기준으로 부자재 주문 수량 조정
            adjustItems();
            // 15시 이전 부자재 주문
            for (int i = 5; i <= 8; i++) {
                orderItem(i);
            }
        }
    }

    // 부자재 주문수량 조정 매소드
    void adjustItems() {
        Long[] orderList = {0L,0L,0L,0L,0L,0L,0L,0L,0L};
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

        if(orderList[3] != 0 || orderList[4] != 0){
            addAmount = (orderList[3] + orderList[4]) * 2 / 5;
            alreadyAmount = 0L;
            if (orderMaterialService.checkOrderMaterial(5L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(5L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(5L);
            amount = alreadyAmount + addAmount - stockAmount;
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(5L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(5L, amount, now, calcImportExpectDate(5L, now));
                    materialService.useStockMaterial(5L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(5L, amount, now, calcImportExpectDate(5L, now));
        }
        if (orderList[1] != 0 || orderList[2] != 0) {
            alreadyAmount = 0L;
            addAmount = 20 * orderList[1] + 120 * orderList[2];
            if (orderMaterialService.checkOrderMaterial(6L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(6L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(6L);
            amount = alreadyAmount + addAmount - stockAmount;
            System.out.println("주문 해야 할 포장지 양 : " + amount);
            // 주문해야할 총 부자재양
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(6L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(6L, amount, now, calcImportExpectDate(6L, now));
                    materialService.useStockMaterial(6L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(6L, amount, now, calcImportExpectDate(6L, now));
        }
        if (orderList[3] != 0 || orderList[4] != 0) {
            alreadyAmount = 0L;
            addAmount = (orderList[3] + orderList[4]) * 200;
            if (orderMaterialService.checkOrderMaterial(7L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(7L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(7L);
            amount = alreadyAmount + addAmount - stockAmount;
            // 주문해야할 총 부자재양
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(7L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(7L, amount, now, calcImportExpectDate(7L, now));
                    materialService.useStockMaterial(7L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(7L, amount, now, calcImportExpectDate(7L, now));
        }

        if (orderList[1] != 0 || orderList[2] != 0 || orderList[3] != 0 || orderList[4] != 0) {
            alreadyAmount = 0L;
            addAmount = orderList[1] * 2 / 3 +  orderList[2] * 4 + (orderList[3] + orderList[4]) * 8;
            if (orderMaterialService.checkOrderMaterial(8L) != null) {
                alreadyAmount = orderMaterialService.checkOrderMaterial(8L).getOrderItemAmount();
            }
            stockAmount = materialService.stockMaterialAmount(8L);
            amount = alreadyAmount + addAmount - stockAmount;
            // 주문해야할 총 부자재양
            if (stockAmount > 0) {
                if (amount < 0) {
                    materialService.useStockMaterial(8L, addAmount, now);
                } else {
                    orderMaterialService.saveOrderMaterial(8L, amount, now, calcImportExpectDate(8L, now));
                    materialService.useStockMaterial(8L, stockAmount, now);
                }
            }
            else
                orderMaterialService.saveOrderMaterial(8L, amount, now, calcImportExpectDate(8L, now));
        }
    }


    // 재고와 비교하여 필요한 원자재 주문 매소드(재고와 비교는 이전 과정에서 이미 진행됨)
    // 주문량이 최대 주문량을 넘어설 경우, 금일 시점으로 최대주문량만 주문 후 나머지 양은 데이터베이스에 저장해 둬야함.
    // (현재 mesAll.orderList를 주문필요량을 저장하는 데이터 베이스로 가정)
    void orderItem(long itemId) {
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
//                MesAll.todayOrderList[(int) itemId] = range[1];
//                MesAll.orderList[(int) itemId] -= range[1];
            }
        }
//
//        // db(발주관리)저장 필요
//        // 현재 메인재료에 대해서만 넘겨줌.
//        if (itemId == mesAll.itemId) {
//            LocalDateTime importExpectDate = calcImportExpectDate(itemId, time);
//            mesAll.time = importExpectDate;
//        }
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

    static int[] minMaxOrder(long itemId) {
        int[] range = new int[2];         // range[0] : 최소 주문량, range[1] : 최대 주문량

        if (itemId == 1) {         // 양배추
            range[0] = 1000;
            range[1] = 5000;
        } else if (itemId == 2) {      // 흑마늘
            range[0] = 10;
            range[1] = 5000;
        } else if (itemId == 3) {      // 석류액기스
            range[0] = 5;
            range[1] = 500;
        } else if (itemId == 4) {      // 매실액기스
            range[0] = 5;
            range[1] = 500;
        } else if (itemId == 5) {      // 콜라겐
            range[0] = 5;
            range[1] = 500;
        } else if (itemId == 6) {      // 파우치
            range[0] = 1000;
            range[1] = 1000000;
        } else if (itemId == 7) {      // 스틱파우치
            range[0] = 1000;
            range[1] = 1000000;
        } else {                  // 포장box
            range[0] = 500;
            range[1] = 10000;
        }

        return range;
    }
}


