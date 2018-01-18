package com.rebuilding.day17;

import com.rebuilding.day17.entity.Issue;

import java.util.ArrayList;
import java.util.List;

public class IssueReviewLogSpy implements IssueReviewLog {

    private int reviewCount;

    private List<Issue> reviewedIssues;

    public IssueReviewLogSpy() {
        reviewCount = 0;
        reviewedIssues = new ArrayList<>();
    }

    @Override
    public void logIssue(Issue issue) {
        reviewCount++;
        reviewedIssues.add(issue);
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public List<Issue> getReviewedIssues() {
        return reviewedIssues;
    }
}
