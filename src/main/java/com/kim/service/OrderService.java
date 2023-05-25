package com.kim.service;

import com.kim.constant.Status;
import com.kim.dto.MoniteringDto;
import com.kim.dto.OrderSearchDto;
import com.kim.dto.OrdersDto;
import com.kim.entity.Mat;
import com.kim.entity.Orders;
import com.kim.repository.MatRepository;
import com.kim.repository.MoniteringRepository;
import com.kim.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private final OrdersRepository ordersRepository;
    @Autowired
    private final MatRepository matRepository;
    @Autowired
    private final MoniteringService moniteringService;
    @Autowired
    private final MoniteringRepository moniteringRepository;

    public Orders saveOrder(Orders orders) { return ordersRepository.save(orders);  }


    @Transactional(readOnly = true)
    public OrdersDto getorderDtl(Long ordersId){

        Orders orders = ordersRepository.findById(ordersId)
                .orElseThrow(EntityNotFoundException::new);
        OrdersDto ordersDto = OrdersDto.of(orders);
        return ordersDto;
    }

    public Long updateOrders(OrdersDto ordersDto) throws Exception{
        Orders orders = ordersRepository.findById(ordersDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        orders.updateOrders(ordersDto);
        return orders.getId();
    }
    public Long updateOrders2(OrdersDto ordersDto) throws Exception{
        Orders orders = ordersRepository.findById(ordersDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        orders.updateOrders2(ordersDto);
        return orders.getId();
    }
    public List<Orders> ordersList() {

        return ordersRepository.findAll();
    }

    public Page<Orders> ordersList(Pageable pageable){
        //기존 List<Board>값으로 넘어가지만 페이징 설정을 해주면 Page<Board>로 넘어갑니다.
        return ordersRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Orders> getOrdersPage(OrderSearchDto orderSearchDto, Pageable pageable){
        return ordersRepository.getOrdersPage(orderSearchDto, pageable);
    }


    public void saveProcess1(Orders orders){


        int dayOver = 0;

        //세척

        MoniteringDto moniteringDto1 = new MoniteringDto();
        //moniteringDto1.setOrderId("kim-wash-"+001);//번호 자동 증가 구현 필요

        moniteringDto1.setOrderId(orders.getId());

        double aaaa = orders.getBox()*((30*80)/0.8)/2/1000;
        int cab = (int) Math.ceil(aaaa);
        List<Mat> matlist = matRepository.findByMatName("양배추");
        Mat mat1 = matlist.get(0);
        if(mat1.getMatNum() >= cab){
            mat1.setMatNum(mat1.getMatNum()-cab);
        }
        else{
            mat1.setMatNum(0);
            Mat mat2 = matlist.get(1);
            mat2.setMatNum(mat2.getMatNum()+mat1.getMatNum()-cab);
        }




        int water = cab;





    }

    public Page<Orders> ordersSearchList1(Long keyword, Pageable pageable){

        return ordersRepository.findById(keyword, pageable);
    }

    public Page<Orders> ordersSearchList2(String keyword, Pageable pageable){

        return ordersRepository.findByOrderFromContaining(keyword, pageable);
    }

    public Page<Orders> ordersSearchList3(String keyword, Pageable pageable){

        return ordersRepository.findByProductContaining(keyword, pageable);
    }

    public Page<Orders> ordersSearchList4(LocalDateTime start,LocalDateTime end, Pageable pageable){



        return ordersRepository.findByOrderDateBetween(start, end , pageable);
    }

    public Page<Orders> ordersSearchList5(LocalDateTime start,LocalDateTime end, Pageable pageable){



        return ordersRepository.findByComDateBetween(start, end , pageable);
    }
    public Page<Orders> ordersSearchList6(Long keyword, Status status, Pageable pageable){

        return ordersRepository.findByIdAndStatus(keyword,status, pageable);
    }

    public Page<Orders> ordersSearchList7(String keyword,  Status status, Pageable pageable){

        return ordersRepository.findByOrderFromContainingAndStatus(keyword, status,pageable);
    }

    public Page<Orders> ordersSearchList8(String keyword, Status status,  Pageable pageable){

        return ordersRepository.findByProductContainingAndStatus(keyword,status, pageable);
    }

    public Page<Orders> ordersSearchList9(LocalDateTime start,LocalDateTime end, Status status,  Pageable pageable){



        return ordersRepository.findByOrderDateBetweenAndStatus(start, end , status,pageable);
    }

    public Page<Orders> ordersSearchList10(LocalDateTime start,LocalDateTime end,  Status status, Pageable pageable){



        return ordersRepository.findByComDateBetweenAndStatus(start, end ,status, pageable);
    }

    public List<Orders> ordersSearchList11(Status status, Long keyword){

        return ordersRepository.findByStatusAndId(status, keyword);
    }

    public List<Orders> ordersSearchList12(String keyword,  Status status){

        return ordersRepository.findByStatusAndOrderFromContaining(status, keyword);
    }

    public List<Orders> ordersSearchList13(String keyword, Status status,  Pageable pageable){

        return ordersRepository.findByStatusAndProductContaining(status,keyword);
    }

    public List<Orders> ordersSearchList14(LocalDateTime start,LocalDateTime end, Status status,  Pageable pageable){



        return ordersRepository.findByStatusAndOrderDateBetween(status, start, end);
    }

    public List<Orders> ordersSearchList15(LocalDateTime start,LocalDateTime end,  Status status, Pageable pageable){



        return ordersRepository.findByStatusAndComDateBetween(status, start, end);
    }




}
