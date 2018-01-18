package com.rebuilding.day15.repository;

import com.rebuilding.day15.entity.Issue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueServiceTest {
    private static final Long ID = 66L;
    private IssueRepositoryMock repository;

    /**
     * 由於Mock並非無狀態的物件，所以在每個測試案例之前我們都必須建立新的Mock
     * 所以我們在@Before註解的方法裡建立Mock
     */
    @Before
    public void setup() {
        repository = new IssueRepositoryMock();
    }

    /**
     * 1. 驗證待測系統是否有調用外部相依物件的方法deleteById、是否傳入正確的參數
     */
    @Test
    public void Given_ExpectedArgumentId_When_DeleteById_Then_MethodWasInvokedWithCorrectId() {
        /**
         * 對外部相依Mock物件，設定調用deleteById()的參數期望值
         */
        repository.setExpectedArgumentId(ID);

        /**
         * 手動直接調用外部相依物件的方法
         */
        repository.deleteById(ID);

        /**
         * 驗證
         */
        repository.verify();
    }

    /**
     * 2. 驗證待測系統調用外部相依物件的方法deleteById()，是否回傳了正確的物件
     */
    @Test
    public void Given_SpecifiedIdIssue_When_DeleteById_Then_ReturnDeleteIssue() {

        /**
         * 對外部相依Mock物件，設定調用deleteById()的回傳的Issue物件
         */
        Issue deleted = new Issue();
        deleted.setId(ID);
        repository.setReturnedIssue(deleted);

        /**
         * 手動直接調用外部相依物件的方法
         */
        Issue returned = repository.deleteById(ID);

        /**
         * 驗證
         */
        assertThat(returned).isSameAs(deleted);
    }
}
