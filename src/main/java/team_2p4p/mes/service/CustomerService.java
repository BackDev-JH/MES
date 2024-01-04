package team_2p4p.mes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team_2p4p.mes.entity.Customer;
import team_2p4p.mes.entity.Item;
import team_2p4p.mes.repository.CustomerRepository;
import team_2p4p.mes.repository.ItemRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;

    public void saveCustomer(String customerAddress, String name, String customerPersonName, String customerTel, Item item1,Item item2) {
        Customer customer = Customer.builder()
                .customerAddress(customerAddress)
                .customerName(name)
                .customerPersonName(customerPersonName)
                .customerTel(customerTel)
                .item1(item1)
                .item2(item2)
                .build();
        System.out.println(customer);
        customerRepository.save(customer);
    }

    public Customer customerFindbyItem(Item item){
        return customerRepository.findByItem1OrItem2(item);
    }
}
