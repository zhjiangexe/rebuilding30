package com.rebuilding.day5;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class IssueTest1 {
    private static final String CREATOR = "CREATOR";

    @Test
    public void testIssue1isResolved() {
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setActiveState(ActiveState.DONE);
        issue.setCreator(CREATOR);
        issue.setLog("log");
        SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(issue.getActiveState())
                .overridingErrorMessage(
                        "已解決的問題的活動狀態必需是已完成的(DONE), 但是為: %s",
                        issue.getActiveState()
                )
                .isEqualTo(ActiveState.DONE);

        assertions.assertThat(issue.getLog())
                .overridingErrorMessage(
                        "已解決的問題的必需要有紀錄內容，但是它的紀錄內容: %s",
                        issue.getLog()
                )
                .isNotNull();

        assertions.assertAll();
    }

    @Test
    public void testIssue2isResolved() {
        Issue issue = new Issue();
        issue.setId(2L);
        issue.setActiveState(ActiveState.DONE);
        issue.setCreator(CREATOR);
        issue.setLog("everything is ok.");

        SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(issue.getActiveState())
                .overridingErrorMessage(
                        "已解決的問題的活動狀態必需是已完成的(DONE), 但是為: %s",
                        issue.getActiveState()
                )
                .isEqualTo(ActiveState.DONE);

        assertions.assertThat(issue.getLog())
                .overridingErrorMessage(
                        "已解決的問題的必需要有紀錄內容，但是它的紀錄內容: %s",
                        issue.getLog()
                )
                .isNotNull();

        assertions.assertAll();
    }
}
