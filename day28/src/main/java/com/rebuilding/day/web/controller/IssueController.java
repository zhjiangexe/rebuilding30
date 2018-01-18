package com.rebuilding.day.web.controller;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Issue query(@PathVariable Long id) {
        return issueService.queryById(id);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Issue> search(@RequestParam("keyword") String keyword) {
        List<Issue> list = issueService.search(keyword);
        return list;
    }
}
