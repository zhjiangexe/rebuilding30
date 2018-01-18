package com.rebuilding.day.service.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IssueServiceImp implements IssueService {

    private IssueRepository issueRepository;

    @Override
    public Issue findOrSave(Map<String, String> map) {
        Issue issue = new Issue();
        issue.setTitle(map.get("title"));
        if (map.get("id") == null) {
            return issueRepository.save(issue);
        } else {
            issue.setId(Long.valueOf(map.get("id")));
            return this.findById(issue.getId());
        }
    }

    @Override
    public Issue findById(Long id) {
        return issueRepository.findById(id);
    }

    @Override
    public Issue save(Issue issue) {
        return issueRepository.save(issue);
    }

    @Autowired
    public void setIssueRepository(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }
}
