package com.rebuilding.day10.dataCentral;

import com.rebuilding.day10.*;
import org.junit.Test;

public class IssueServiceTest {
    /**
     * 1. 在測試類別內加入會使用到的常數
     */
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    /**
     * 2. 使用new建立createDoneIssueWithResolved()需要的測試資料
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

    /**
     * 3. 使用IssueBuilder建立buildDoneIssueWithResolved()需要的測試資料
     */
    @Test
    public void buildDoneIssueWithResolved() {
        Issue issue = new IssueBuilder()
                .title(TITLE)
                .desc(DESC)
                .id(ID)
                .creator(CREATOR_NUM)

                .assignee(ASSIGNEE_NUM)
                .closer(CLOSER_NUM)
                .state(State.DONE)
                .result(Result.RESOLVED)
                .build();
    }
}
