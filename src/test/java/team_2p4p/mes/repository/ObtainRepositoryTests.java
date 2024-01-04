package team_2p4p.mes.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team_2p4p.mes.entity.Obtain;
import team_2p4p.mes.service.ObtainService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ObtainRepositoryTests {

    @Autowired
    private ObtainRepository obtainRepository;

    @Autowired
    private ObtainService obtainService;

   @Test
   public void obtains() {
       List<Obtain> obtainList = obtainService.classification0();

       System.out.println(obtainList);
   }
}
