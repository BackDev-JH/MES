package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import team_2p4p.mes.dto.ObtainDTO;
import team_2p4p.mes.entity.*;
import team_2p4p.mes.repository.ObtainRepository;
import team_2p4p.mes.repository.ProductRepository;
import team_2p4p.mes.util.calculator.CalcOrderMaterial;
import team_2p4p.mes.util.calculator.Calculator;
import team_2p4p.mes.util.calculator.MesAll;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ProductService {
    private final ProductRepository productRepository;
    Calculator cal = new Calculator();


    public Long productStock(Long itemId) {
        List<Product> productList = productRepository.findByItemItemId(LocalDate.now(), itemId);
        Long stock = 0L;
        if(!productList.isEmpty()) {
            for (Product product : productList) {
                if (!product.isExportStat())
                    stock += product.getProductStock();
                else
                    stock -= product.getProductStock();
            }
        }
        return stock;
    }

    public Product addMinusProductStock(Item item, Long stock, LocalDateTime makeDate, LotLog lot) {
        Product product = new Product(null, item, stock, makeDate, true, lot);
        return productRepository.save(product);
    }

    public Product lastStock(Long itemId) {
        List<Product> productList = productRepository.findByItemItemId(LocalDate.now(), itemId);
        return productList.isEmpty()? null :productList.get(productList.size() - 1);
    }

    public List<Product> getProductList(){
        List<Product> productList = productRepository.findAll();

        return productList;
    }

}
