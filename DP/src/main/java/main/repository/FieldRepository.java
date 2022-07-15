package main.repository;

import main.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    @Query("SELECT f.weight FROM Field AS f WHERE f.name = ?1")
    Float findWeightByName(String name);
}