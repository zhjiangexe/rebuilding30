package com.rebuilding.day.service.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IssueServiceImp implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public List<Issue> queryAll() {
        return new ArrayList<Issue>() {{
            add(new Issue());
        }};
    }

    @Override
    public Issue queryById(Long id) {
        return issueRepository.findById(id);
    }

    @Override
    public Issue getBigIssue() {
        return issueRepository.getBigIssue();
    }
}
