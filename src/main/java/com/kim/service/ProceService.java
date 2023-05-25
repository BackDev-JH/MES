package com.kim.service;

import com.kim.dto.OrdersDto;
import com.kim.dto.ProceDto;
import com.kim.entity.Orders;
import com.kim.entity.Proce;
import com.kim.repository.ProceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class ProceService {
    @Autowired
    private ProceRepository proceRepository;

    public Proce saveProce(Proce proce) { return proceRepository.save(proce);  }

    public Long updateProce(ProceDto proceDto) throws Exception{
        Proce proce = proceRepository.findById(proceDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        proce.updateProce(proceDto);
        return proce.getId();
    }

    @Transactional(readOnly = true)
    public ProceDto getProceDtl(Long proceId){

        Proce proce = proceRepository.findById(proceId)
                .orElseThrow(EntityNotFoundException::new);
        ProceDto proceDto = ProceDto.of(proce);
        return proceDto;
    }
}
