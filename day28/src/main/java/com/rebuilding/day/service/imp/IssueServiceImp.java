package com.rebuilding.day.service.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueServiceImp implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue queryById(Long id) {
        return issueRepository.findOne(id);
    }

    @Override
    public Issue update(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public List<Issue> search(String keyword) {
        return issueRepository.search(keyword);
    }
}
