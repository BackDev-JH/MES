package team_2p4p.mes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {

    private String searchAfterDateType;

    private String searchBeforeDateType;

    private String searchBy;

    private String searchBy2;

    private String searchQuery ="";

}
