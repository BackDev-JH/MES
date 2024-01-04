package team_2p4p.mes.repository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import team_2p4p.mes.entity.Product;

import java.util.List;

@SpringBootTest

public class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @Test
    void test2(){

     List<Product> productList = productRepository.findAll();
        System.out.println(productList);
    }


}
