package main.repository;

import main.entity.IndexT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexTRepository extends JpaRepository<IndexT, Integer> {}
