package com.rebuilding.day.service.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import com.rebuilding.day.service.IssueService;
import com.rebuilding.day.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

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
        Issue one = issueRepository.findOne(issue.getId());
        if (one == null) {
            throw new NotFoundException(format("issue id %s not found", issue.getId()));
        }
        one.setTitle(issue.getTitle());
        one.setDesc(issue.getDesc());
        return issueRepository.save(one);
    }

    @Override
    public List<Issue> search(String keyword) {
        return issueRepository.search(keyword);
    }
}
