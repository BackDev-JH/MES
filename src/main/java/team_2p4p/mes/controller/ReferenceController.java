package team_2p4p.mes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team_2p4p.mes.entity.*;
import team_2p4p.mes.entity.Process;
import team_2p4p.mes.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("juicyfresh/")
@RequiredArgsConstructor
public class ReferenceController {


    private final ItemRepository itemRepository;

    private final FacilityRepository facilityRepository;

    private final ProcessRepository processRepository;

    private final RoutingRepository routingRepository;

    private final BomRepository bomRepository;

    private final EnterpriseRepository enterpriseRepository;

    private final CustomerRepository customerRepository;

    //juicyfreash
    @GetMapping("test")
    public List<Item> test(){

        Pageable pageable = PageRequest.of(0,50);

        Page<Item> all = itemRepository.findAll(pageable);

        return all.get().collect(Collectors.toList());
    }

    @GetMapping("facility")
    public List<Facility> facility(){

        List<Facility> facilityList = facilityRepository.findAll();
        return facilityList;
    }

    @GetMapping("process")
    public List<Process> process(){
        List<Process> processList = processRepository.findAll();
        return processList;
    }

    @GetMapping("routing")
    public List<Routing> routing(){

        List<Routing> routingList = routingRepository.findAll();
        return routingList;
    }

    @GetMapping("bom")
    public List<Bom> bom(){

        List<Bom> bomList = bomRepository.findAll();
        return bomList;
    }

    @GetMapping("enterprise")
    public List<Enterprise> enterprise(){

        List<Enterprise> enterpriseList = enterpriseRepository.findAll();
        return enterpriseList;
    }

    @GetMapping("customer")
    public List<Customer> customer(){

        List<Customer> customerList = customerRepository.findAll();
        return customerList;
    }


}
