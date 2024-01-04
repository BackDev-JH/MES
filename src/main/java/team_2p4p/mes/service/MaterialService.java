package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Material;
import team_2p4p.mes.entity.OrderMaterial;
import team_2p4p.mes.repository.MaterialRepository;
import team_2p4p.mes.repository.OrderMaterialRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final  OrderMaterialService orderMaterialService;
    private final ItemService itemService;

    public Long stockMaterialAmount(Long itemId) {
        List<Material> materialList = materialRepository.findByItemItemId(itemId);
        Long stock = 0L;
        for(Material material : materialList) {
            if(material.getMaterialStat() >= 1) {
                stock -= material.getMaterialStock();
            }
            else {
                stock += material.getMaterialStock();
            }
        }
        return stock;
    }

    // 자재 발주시 재고 확인용
    public Material useStockMaterial(Long itemId, Long stock, LocalDateTime now) {
        Material material = new Material(null, findLatestMaterial(itemId).getOrderMaterial(), findLatestMaterial(itemId).getItem(), stock, now, 1);
        return materialRepository.save(material);
    }

    public Material findLatestMaterial(Long itemId) {
        List<Material> materialList = materialRepository.findByItemItemId(itemId);
        if(materialList.isEmpty())
            return null;
        return materialList.get(materialList.size() - 1);
    }

    public Material addInputMaterial(OrderMaterial orderMaterial){
        Material material = new Material(null, orderMaterial, orderMaterial.getItem(), orderMaterial.getOrderItemAmount(), orderMaterial.getImportExpectDate(),0);
        return materialRepository.save(material);
    }

    // 제품 생산시 재고 소진용
    public Material useMaterial(Long itemId, Long orderId, Long amount){
        Material material;
        if(!materialRepository.findByMaterialStatAndItemId(1, itemId).isEmpty()){
            for (Material mt : materialRepository.findByMaterialStatAndItemId(1, itemId)){
                if (mt.getMaterialStock() <= amount) {
                    material = mt;
                    material.updateMaterial(2, mt.getMaterialStock(), LocalDateTime.now());
                    amount -= mt.getMaterialStock();
                    materialRepository.save(material);
                }
                else {
                    material = mt;
                    material.updateMaterial(1, mt.getMaterialStock() - amount, mt.getWarehouseDate());
                    materialRepository.save(material);
                    material = new Material(null, orderMaterialService.findByOrderId(orderId), itemService.findItemById(itemId), amount, LocalDateTime.now(), 2);
                    materialRepository.save(material);
                    amount = 0L;
                }
                if (amount == 0){
                    return null;
                }
            }
            if (amount > 0){
                material = new Material(null, orderMaterialService.findByOrderId(orderId), itemService.findItemById(itemId), amount, LocalDateTime.now(), 2);
                return materialRepository.save(material);
            }
        }
        material = new Material(null, orderMaterialService.findByOrderId(orderId), itemService.findItemById(itemId), amount, LocalDateTime.now(), 2);
        return materialRepository.save(material);
    }
}
