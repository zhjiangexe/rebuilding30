package com.rebuilding.day9;

import org.junit.Test;

public class IssueServiceTest3 {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    @Test
    public void createTodoIssueWithoutAssigneeByFactoryMethod() {
        Issue issue = IssueFactory.createTodoIssueWithoutAssignee(ID, TITLE, DESC, CREATOR_NUM);
    }

    @Test
    public void createTodoIssueWithAssigneeByFactoryMethod() {
        Issue issue = IssueFactory.createTodoIssueWithAssignee(ID, TITLE, DESC, CREATOR_NUM, ASSIGNEE_NUM);
    }

    @Test
    public void createDoneIssueWithResolvedByFactoryMethod() {
        Issue issue = IssueFactory.createDoneIssueWithResolved(ID, TITLE, DESC, CREATOR_NUM, ASSIGNEE_NUM, CLOSER_NUM);
    }

}
