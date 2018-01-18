package com.rebuilding.day9;

import org.junit.Test;

public class IssueServiceTest2 {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    @Test
    public void createTodoIssueWithoutAssigneeByFactoryMethod() {
        Issue issue = createTodoIssueWithoutAssignee(ID, TITLE, DESC, CREATOR_NUM);
    }

    private Issue createTodoIssueWithoutAssignee(Long id, String title, String desc, Long creatorNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.TODO);
        return issue;
    }

    @Test
    public void createTodoIssueWithAssigneeByFactoryMethod() {
        Issue issue = createTodoIssueWithAssignee(ID, TITLE, DESC, CREATOR_NUM, ASSIGNEE_NUM);
    }

    private Issue createTodoIssueWithAssignee(Long id, String title, String desc, Long creatorNum, Long assigneeNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.TODO);
        issue.setAssignee(new Assignee(assigneeNum));
        return issue;
    }

    @Test
    public void createDoneIssueWithResolvedByFactoryMethod() {
        Issue issue = createDoneIssueWithResolved(ID, TITLE, DESC, CREATOR_NUM, ASSIGNEE_NUM, CLOSER_NUM);
    }

    private Issue createDoneIssueWithResolved(Long id, String title, String desc, Long creatorNum, Long assigneeNum, Long closerNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.DONE);
        issue.setAssignee(new Assignee(assigneeNum));
        issue.setCloser(new Closer(closerNum));
        issue.setResult(Result.RESOLVED);
        return issue;
    }
}
