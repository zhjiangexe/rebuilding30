package com.rebuilding.day.repository.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IssueRepositoryImp implements IssueRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Issue findById(Long id) {
        Issue issue = jdbcTemplate.queryForObject("SELECT id, title, desc FROM ISSUE WHERE id = ?", new Object[]{id}, (rs, rowNum) -> {
            Issue data = new Issue();
            data.setTitle(rs.getString("title"));
            data.setId(rs.getLong("id"));
            data.setDesc(rs.getString("desc"));
            return data;
        });
        return issue;
    }

    @Override
    public Issue getBigIssue() {
        Issue issue = new Issue();
        issue.setTitle("BIG");
        return issue;
    }
}
