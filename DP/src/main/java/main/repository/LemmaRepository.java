package main.repository;

import main.entity.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query("SELECT count (l.lemma) FROM Lemma as l WHERE l.site.id = ?1")
    Integer findCountLemmaOfId(Integer site_id);

    @Query("FROM Lemma AS l WHERE l.lemma = ?1")
    Lemma findLemmaByLemma(String lemma);
}