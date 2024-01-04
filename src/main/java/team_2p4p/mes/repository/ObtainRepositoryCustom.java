package team_2p4p.mes.repository;


import team_2p4p.mes.dto.SearchDTO;
import team_2p4p.mes.entity.Obtain;

import java.util.List;

public interface ObtainRepositoryCustom {

    List<Obtain> getObtainList(SearchDTO searchDto);

}
