package main.repository;

import main.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    boolean existsSiteByUrl(String url);
    Site findSiteByUrl(String url);
    Site findSiteById(int id);
}