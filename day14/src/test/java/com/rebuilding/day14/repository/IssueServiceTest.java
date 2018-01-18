package com.rebuilding.day14.repository;

import com.rebuilding.day14.entity.Issue;
import com.rebuilding.day14.service.imp.IssueServiceImp;
import org.junit.Before;
import org.junit.Test;

import static com.rebuilding.day14.repository.TestDoubles.stub;

public class IssueServiceTest {
    private static final Long ID = 66L;
    private IssueRepository repository;

    /**
     * 1. 由於Stub並非無狀態的物件，所以在每個測試案例之前我們都必須建立新的Stub
     * 所以我們在@Before註解的方法裡建立Stub
     */
    @Before
    public void setup() {
        /**
         * 1-1.
         * 建立Issue物件，並設定其title及desc屬性值，為了模擬IssueRepository.find()找到完整的Issue內容
         * 透過構造函式注入Issue物件到IssueRepositoryStub並建立
         */
        repository = stub(IssueRepository.class);
    }

    /**
     * 2. 編寫測試案例，這裡是簡單地驗證"當調用方法find，於是回傳具有Title的Issue"
     */
    @Test
    public void When_Find_Then_ReturnHasTitleIssue() {
        /**
         * 2-1. 初始化待測物件issueServiceImp，初始化方法參數issue
         */

        Issue issue = new Issue();
        issue.setId(ID);

//        /**
//         * 2-2. 調用IssueRepository介面的方法find()
//         */
//        Issue actual = issueServiceImp.find(issue);
//
//        /**
//         * 2-3. 驗證回傳的Issue是否有Title
//         */
//        assertThat(actual.getTitle()).isNotEmpty();
    }
}
