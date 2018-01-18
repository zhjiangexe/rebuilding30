package com.rebuilding.day11.emphasizing;

import com.rebuilding.day11.builderPattern.Issue;
import org.junit.Test;

public class IssueServiceTest {
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    /**
     * 1. 待處理的Issue，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = IssueBuilder.createTodoIssueWithoutAssignee();
    }

    /**
     * 2. 待處理的Issue，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = IssueBuilder.createTodoIssueWithAssignee(ASSIGNEE_NUM);
    }

    /**
     * 3. 已解決的Issue
     */
    @Test
    public void createResolvedIssue() {
        Issue issue = IssueBuilder.createResolvedIssue(CLOSER_NUM);
    }
}
