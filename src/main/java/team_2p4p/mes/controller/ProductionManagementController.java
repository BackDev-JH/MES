package team_2p4p.mes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team_2p4p.mes.entity.OrderMaterial;
import team_2p4p.mes.entity.ProductionManagement;
import team_2p4p.mes.repository.OrderMaterialRepository;
import team_2p4p.mes.repository.ProductionManagementRepository;
import team_2p4p.mes.service.ProductionManagementService;

import java.util.List;

;


@RestController
@RequestMapping("juicyfresh/")
@RequiredArgsConstructor
public class ProductionManagementController {



    private final ProductionManagementService productionManagementService;

    //생산 일정 조회
    @GetMapping("/productionschedule/list")
    public List<ProductionManagement> productionschedule(){
        return productionManagementService.classification0();
    }

    //생산 현황 조회
    @GetMapping("/productionsituation/list")
    public List<ProductionManagement> productionsituation(){
        return productionManagementService.classification1();
    }
    
    //생산 실적 조회
    @GetMapping("/productionperformance/list")
    public List<ProductionManagement> productionperformance(){
        return productionManagementService.classification2();
    }

}
