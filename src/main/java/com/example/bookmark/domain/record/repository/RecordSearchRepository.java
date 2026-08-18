package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.dto.response.RecordSearchResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class RecordSearchRepository {

    private static final String DTO =
            "com.example.bookmark.domain.record.dto.response.RecordSearchResponse";

    private final EntityManager em;

    public List<RecordSearchResponse> searchByTerms(Long userId, List<String> terms, int limit) {

        // title / content 가 null 일 수 있으므로 coalesce 로 빈 문자열 처리
        String title = "lower(coalesce(r.title, ''))";
        String content = "lower(coalesce(r.content, ''))";

        String where = IntStream.range(0, terms.size())
                .mapToObj(i -> "(" + title + " like :kw" + i + " escape '!'"
                        + " or " + content + " like :kw" + i + " escape '!')")
                .collect(Collectors.joining(" or "));

        // 제목 매칭 2점, 본문 매칭 1점
        String score = IntStream.range(0, terms.size())
                .mapToObj(i -> "case when " + title + " like :kw" + i
                        + " escape '!' then 2 else 0 end"
                        + " + case when " + content + " like :kw" + i
                        + " escape '!' then 1 else 0 end")
                .collect(Collectors.joining(" + "));

        String jpql = """
                select new %s(r.id, r.title, ch.name, cb.title, r.createdAt)
                from Record r
                join r.chapter ch
                join ch.collectBook cb
                where r.user.id = :userId and (%s)
                order by (%s) desc, r.createdAt desc
                """.formatted(DTO, where, score);

        TypedQuery<RecordSearchResponse> query =
                em.createQuery(jpql, RecordSearchResponse.class);

        query.setParameter("userId", userId);
        for (int i = 0; i < terms.size(); i++) {
            query.setParameter("kw" + i, "%" + terms.get(i) + "%");
        }

        return query.setMaxResults(limit).getResultList();
    }
}
