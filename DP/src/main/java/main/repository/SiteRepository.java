package main.repository;

import main.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    boolean existsSiteByUrl(String url);
    @Query("FROM Site AS s WHERE s.url = ?1")
    Site findSiteByUrl(String url);
    Site findSiteById(int id);
}