package com.rebuilding.day19.service.imp;

import com.rebuilding.day19.entity.Issue;
import com.rebuilding.day19.repository.IssueRepository;
import com.rebuilding.day19.service.IssueService;

import java.util.Map;

public class IssueServiceImp implements IssueService {

    private IssueRepository issueRepository;

    public IssueServiceImp(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }


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
}
