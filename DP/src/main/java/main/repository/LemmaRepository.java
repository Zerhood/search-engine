package main.repository;

import main.entity.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query("SELECT count (L.lemma) FROM Lemma as L WHERE L.site.id = :site_id")
    Integer findCountLemmaOfId(@Param("site_id") Integer site_id);

    Lemma findLemmaByLemma(String lemma);
}