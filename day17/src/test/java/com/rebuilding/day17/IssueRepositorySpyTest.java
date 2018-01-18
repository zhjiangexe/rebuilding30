package com.rebuilding.day17;

import com.rebuilding.day17.entity.Issue;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueRepositorySpyTest {
    private static final String TITLE = "TITLE";
    private IssueRepositorySpy repository;

    /**
     * 1. 由於Spy具有狀態，所以必須在每個測試案例之前重新建立
     */
    @Before
    public void setup() {
        repository = new IssueRepositorySpy();
    }

    @Test
    public void verifySaveMethod() {
        /**
         * arrange
         * 3. 建立方法所需的物件
         */
        Issue issue = new Issue();
        issue.setTitle(TITLE);

        /**
         * act
         * 2. 不執行待測系統方法，直接手動呼叫Spy方法save()
         */
        repository.save(issue);

        /**
         * assert
         * 3. 取得Spy所記錄的資料，並驗證執行待測系統的方法時，IssueRepository實際做了哪些事
         */
        int executeCount = repository.getSaveExecuteCount();

        assertThat(executeCount).isEqualTo(1);

        List<Issue> savedIssueList = repository.getSavedIssueList();

        assertThat(savedIssueList).containsExactly(issue);

    }
}
