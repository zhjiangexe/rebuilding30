package com.rebuilding.day8;

import org.junit.Test;

/**
 * 1. 建立測試類別
 */
public class IssueServiceTest {
    /**
     * 2. 將所需的常數加入到測試類別中
     */
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    /**
     * 3-1. Issue待處理，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.TODO);
    }

    /**
     * 3-2. Issue待處理，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        issue.setState(State.TODO);
    }

    /**
     * 3-3. Issue已結案，結案形式為已解決
     */
    @Test
    public void createDoneIssueWithResolved() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        issue.setCloser(new Closer(CLOSER_NUM));
        issue.setState(State.DONE);
        issue.setResult(Result.RESOLVED);
    }
}
