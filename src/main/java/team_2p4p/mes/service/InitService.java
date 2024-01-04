//package team_2p4p.mes.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Service;
//import team_2p4p.mes.entity.LotLog;
//import team_2p4p.mes.entity.Product;
//import team_2p4p.mes.entity.ProductionManagement;
//import team_2p4p.mes.entity.Send;
//import team_2p4p.mes.repository.LotLogRepository;
//import team_2p4p.mes.repository.ProductRepository;
//import team_2p4p.mes.repository.ProductionManagementRepository;
//import team_2p4p.mes.repository.SendRepository;
//
//import javax.transaction.Transactional;
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//@Log4j2
//public class InitService {
//    private final ProductionManagementRepository productionManagementRepository;
//    private final LotLogRepository lotLogRepository;
//    private final ProductRepository productRepository;
//    private final SendRepository sendRepository;
//
//    public void initial() {
//        LocalDate today = LocalDate.now();
//
//        List<ProductionManagement> productionManagementProcessingList = productionManagementRepository.statZeroToOne(today);
//        if(!productionManagementProcessingList.isEmpty()) {
//            for(ProductionManagement productionManagement : productionManagementProcessingList) {
//                productionManagement.update(1);
//            }
//        }
//
//        List<ProductionManagement> productionManagementDoneList = productionManagementRepository.statOneToTwo(today);
//        if(!productionManagementDoneList.isEmpty()) {
//            for(ProductionManagement productionManagement : productionManagementDoneList) {
//                productionManagement.update(2);
//            }
//        }
//
//        List<LotLog> lotLogDoneList = lotLogRepository.statZeroToOne(today);
//        if(!lotLogDoneList.isEmpty()) {
//            for(LotLog lotLog : lotLogDoneList) {
//                lotLog.update();
//            }
//        }
//
//        List<Product> productDoneList = productRepository.statZeroToOne(today);
//        if(!productDoneList.isEmpty()) {
//            for(Product product : productDoneList) {
//                product.update();
//            }
//        }
//
//        List<Send> sendDoneList = sendRepository.statZeroToOne(today);
//        if(!sendDoneList.isEmpty()) {
//            for(Send send : sendDoneList) {
//                send.update();
//            }
//        }
//    }
//}
