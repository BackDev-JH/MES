package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Enterprise;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.entity.OrderMaterial;
import team_2p4p.mes.repository.EnterpriseRepository;
import team_2p4p.mes.repository.ItemRepository;
import team_2p4p.mes.repository.OrderMaterialRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class OrderMaterialService {
    private final OrderMaterialRepository orderMaterialRepository;
    private final ItemRepository itemRepository;
    private final EnterpriseRepository enterpriseRepository;

    public List<OrderMaterial> orderMaterialList() {
        return orderMaterialRepository.findAll();
    }

    // 23.05.24
    // 수주 확정시 발주량 업데이트하는 매소드
    // 기존 발주대기가 있을 경우, 발주량 추가
    // 없을 경우 생성.
    public OrderMaterial saveOrderMaterial(Long itemId, Long amount, LocalDateTime orderDate, LocalDateTime importExpectDate) {
        List<OrderMaterial> orderMaterialList = orderMaterialRepository.findByItemItemId(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("아이템이 없습니다."));
        Enterprise enterprise = enterpriseRepository.findByItemItemId(itemId);
        OrderMaterial orderMaterial;
        if (orderMaterialList.isEmpty()){
            orderMaterial = new OrderMaterial(null, item, enterprise, amount, orderDate, importExpectDate, 0);
        }
        else {
            orderMaterial = orderMaterialList.get(orderMaterialList.size() - 1);
            if (orderMaterial.getOrderStat() == 0) {
                orderMaterial.updateOrderMaterial(orderMaterial, orderMaterial.getOrderItemAmount() + amount, orderDate, importExpectDate, 0);
            } else {
                orderMaterial = new OrderMaterial(null, item, enterprise, amount, orderDate, importExpectDate, 0);
            }
        }
        return orderMaterialRepository.save(orderMaterial);
    }

    public OrderMaterial saveFinishOrderMaterial(OrderMaterial orderMaterial){
        orderMaterial.updateOrderMaterial(orderMaterial, orderMaterial.getOrderItemAmount(), orderMaterial.getOrderDate(), orderMaterial.getImportExpectDate(), 2);
        return orderMaterialRepository.save(orderMaterial);
    }

    @Nullable
    public OrderMaterial checkOrderMaterial(Long itemId) {
        List<OrderMaterial> orderMaterialList = orderMaterialRepository.findByItemItemId(itemId);
        if(!orderMaterialList.isEmpty()) {
            if (orderMaterialList.get(orderMaterialList.size() - 1).getOrderStat() == 0)
                return orderMaterialList.get(orderMaterialList.size() - 1);
        }
        return null;
    }

    public OrderMaterial confirmOrderMaterial(OrderMaterial orderMaterial, Long amount, LocalDateTime orderDate, LocalDateTime importExpectDate) {
        orderMaterial.updateOrderMaterial(orderMaterial, amount, orderDate, importExpectDate, 1);
        return orderMaterialRepository.save(orderMaterial);
    }

    public List<OrderMaterial> todayOrderMaterial() {
        LocalDate today = LocalDate.now();
        List<OrderMaterial> orderMaterialList = orderMaterialRepository.findByOrderDate(today);
        return orderMaterialList;
    }

    public long findOrderId(Long itemId) {
        if (!orderMaterialRepository.findByItemItemId(itemId).isEmpty()) {
            List<OrderMaterial> orderMaterialList = orderMaterialRepository.findByItemItemId(itemId);
            return orderMaterialList.get(orderMaterialList.size() - 1).getOrderId();
        }
        return -1;
    }

    public List<OrderMaterial> finishOrderMaterial() {
        List<OrderMaterial> orderMaterialList = orderMaterialRepository.findByImportExpectDate(LocalDate.now());
        return orderMaterialList;
    }

    public OrderMaterial findByOrderId(Long orderId) {
        return orderMaterialRepository.findById(orderId).orElseThrow(() -> new NullPointerException("주문이 없습니다."));
    }

}