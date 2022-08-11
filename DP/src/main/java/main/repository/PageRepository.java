package main.repository;

import main.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    @Query("SELECT COUNT (p.path) FROM Page AS p WHERE p.site.id = ?1")
    Integer findCountPathOfId(Integer site_id);

    @Query("select p from Page as p where p.site.url = ?1")
    List<Page> findPageBySiteUrl(String url);

    @Query("SELECT p FROM Page AS p JOIN p.index AS i JOIN i.lemma AS l WHERE l.lemma = ?1")
    List<Page> getAllPageByLemma(String lemma);

    @Query("SELECT SUM(i.rank) " +
            "FROM Index AS i " +
            "WHERE i.page.id IN (?1) AND i.lemma.id IN (?2) " +
            "GROUP BY i.page.id")
    List<Float> getSumRank(Collection<Integer> pageId, Collection<Integer> lemmaId);

    @Query("SELECT p.path, p.content, SUM (i.rank) / MAX (?1) " +
            "FROM Index AS i " +
            "JOIN i.page AS p " +
            "JOIN i.lemma AS l " +
            "WHERE i.page.id IN (?2) AND i.lemma.id IN (?3) " +
            "GROUP BY i.page.id")
    List<Object[]> getPathAndContentAndRelevance(float maxRank,
                                                 Collection<Integer> pageId,
                                                 Collection<Integer> lemmaId);
}