package com.kim.service;

import com.kim.dto.MoniteringDto;
import com.kim.entity.Monitering;
import com.kim.repository.MoniteringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MoniteringService {

    private final MoniteringRepository moniteringRepository;
    public Monitering saveBarcode(Monitering monitering) { return moniteringRepository.save(monitering);  }

    @Transactional(readOnly = true)
    public MoniteringDto getBarcodeDtl(Long orderId){

        Monitering monitering = moniteringRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        MoniteringDto moniteringDto = MoniteringDto.of(monitering);
        return moniteringDto;
    }
       public List<Monitering> barcodeList() {
        //기존 List<Board>값으로 넘어가지만 페이징 설정을 해주면 Page<Board>로 넘어갑니다.
        return moniteringRepository.findAll();
    }
    public List<Monitering> barcodeList2(Long id) {
        //기존 List<Board>값으로 넘어가지만 페이징 설정을 해주면 Page<Board>로 넘어갑니다.
        return moniteringRepository.findByOrderId(id);
   }


}
