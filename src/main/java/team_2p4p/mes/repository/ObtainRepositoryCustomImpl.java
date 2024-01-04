package team_2p4p.mes.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.thymeleaf.util.StringUtils;
import team_2p4p.mes.dto.SearchDTO;
import team_2p4p.mes.entity.Obtain;
import team_2p4p.mes.entity.QObtain;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ObtainRepositoryCustomImpl implements ObtainRepositoryCustom {
    private JPAQueryFactory queryFactory;


    public ObtainRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression regDtsAfter(String searchAfterDateType) {
        LocalDate date = LocalDate.now();
        if (StringUtils.equals("전체", searchAfterDateType) || searchAfterDateType == null) {
            return null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime parsedDate = LocalDate.parse(searchAfterDateType, formatter).atStartOfDay();
            return QObtain.obtain.customerRequestDate.after(parsedDate);
        }
    }

    private BooleanExpression regDtsBefore(String searchBeforeDateType) {
        LocalDate date = LocalDate.now();
        if (StringUtils.equals("전체", searchBeforeDateType) || searchBeforeDateType == null) {
            return null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime parsedDate = LocalDate.parse(searchBeforeDateType, formatter).atStartOfDay();
            return QObtain.obtain.customerRequestDate.before(parsedDate);
        }
    }


    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (StringUtils.equals("전체", searchBy)) {
            // 검색어가 없는 경우 null을 반환하여 전체를 조회하도록 합니다.
            return null;
        }else if(StringUtils.equals(null, searchBy))
            return null;

        if (StringUtils.equals("업체명", searchBy)) {
            return QObtain.obtain.customer.customerName.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("제품명", searchBy)) {
            return QObtain.obtain.item.itemName.like("%" + searchQuery + "%");
        }

        return null;
    }


//    @Override
//    public List<Obtain> getObtainList(SearchDTO searchDto) {
//        List<Obtain> results = queryFactory
//                .selectFrom(QObtain.obtain)
//                .where(regDtsAfter(searchDto.getSearchAfterDateType()),
//                        regDtsBefore(searchDto.getSearchAfterDateType()),
//                        searchByLike(searchDto.getSearchBy(), searchDto.getSearchQuery()))
//                .orderBy(QObtain.obtain.obtainId.desc())
//                .fetch();
//
//        return results;
    @Override
    public List<Obtain> getObtainList(SearchDTO searchDto) {
        List<Obtain> results = queryFactory
                .selectFrom(QObtain.obtain)
                .where(regDtsAfter(searchDto.getSearchAfterDateType()),
                        regDtsBefore(searchDto.getSearchBeforeDateType()), // 수정된 부분
                        searchByLike(searchDto.getSearchBy(), searchDto.getSearchQuery()))
                .orderBy(QObtain.obtain.obtainId.desc())
                .fetch();

        return results;
    }

}

