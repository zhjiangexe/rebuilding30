package com.rebuilding.day.controller;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Issue> queryAll() {
        return issueService.queryAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Issue queryById(@PathVariable Long id) {
        return issueService.queryById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Issue create(@Valid @RequestBody Issue issue) {
        return issueService.create(issue);
    }
}
