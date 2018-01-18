package com.rebuilding.day5;

import org.junit.Test;

import static com.rebuilding.day5.IssueAssert.assertThatIssue;

public class IssueTest2 {
    private static final String CREATOR = "CREATOR";

    @Test
    public void testIssue1isResolved() {
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setActiveState(ActiveState.DONE);
        issue.setCreator(CREATOR);
        issue.setLog("log");
        assertThatIssue(issue).isResolved();
    }

    @Test
    public void testIssue2isResolved() {
        Issue issue = new Issue();
        issue.setId(2L);
        issue.setActiveState(ActiveState.DONE);
        issue.setCreator(CREATOR);
        issue.setLog("everything is ok.");

        assertThatIssue(issue).isResolved();
    }
}
