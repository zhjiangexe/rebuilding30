package com.rebuilding.day10.stateful;

import com.rebuilding.day10.Issue;
import org.junit.Test;

public class IssueServiceTest {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    /**
     * 1. 待處理的Issue，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .withStateTodo()
                .build();
    }

    /**
     * 2. 待處理的Issue，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .assignee(ASSIGNEE_NUM)
                .withStateTodo()
                .build();
    }

    /**
     * 3. 執行中的Issue
     */
    @Test
    public void createInProgressIssue() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .assignee(ASSIGNEE_NUM)
                .withStateInProgress()
                .build();
    }

    /**
     * 4. 已解決的Issue
     */
    @Test
    public void createResolvedIssue() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .assignee(ASSIGNEE_NUM)
                .withResultResolved(CLOSER_NUM)
                .build();
    }

    /**
     * 5. 已駁回的Issue，未有負責人
     */
    @Test
    public void createRejectIssueWithoutAssignee() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .withResultResolved(CLOSER_NUM)
                .build();
    }
}
