package com.rebuilding.day9;

import org.junit.Test;

public class IssueServiceTest1 {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    @Test
    public void createTodoIssueWithoutAssigneeByFactoryMethod() {
        Issue issue = createTodoIssueWithoutAssignee();
    }

    private Issue createTodoIssueWithoutAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.TODO);
        return issue;
    }

    @Test
    public void createTodoIssueWithAssigneeByFactoryMethod() {
        Issue issue = createTodoIssueWithAssignee();
    }

    private Issue createTodoIssueWithAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.TODO);
        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        return issue;
    }

    @Test
    public void createDoneIssueWithResolvedByFactoryMethod() {
        Issue issue = createDoneIssueWithResolved();
    }

    private Issue createDoneIssueWithResolved() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.DONE);
        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        issue.setCloser(new Closer(CLOSER_NUM));
        issue.setResult(Result.RESOLVED);
        return issue;
    }
}
