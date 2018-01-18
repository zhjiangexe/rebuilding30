package com.rebuilding.day.repository;

import com.rebuilding.day.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT i FROM Issue i " +
            "WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(i.desc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Issue> search(@Param("keyword") String keyword);
}
