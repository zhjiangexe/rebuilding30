package com.rebuilding.day.web.controller;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Issue update(@PathVariable Long id,
                        @Valid @RequestBody Issue issue) {
        issue.setId(id);

        Issue updated = issueService.update(issue);

        return updated;
    }
}
