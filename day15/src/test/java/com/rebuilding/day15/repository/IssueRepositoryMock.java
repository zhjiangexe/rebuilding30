package com.rebuilding.day15.repository;

import com.rebuilding.day15.entity.Issue;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositoryMock，並將他設定為final避免被繼承
 */
final public class IssueRepositoryMock implements IssueRepository {

    /**
     * 2. 加入一個類別屬性private final Issue
     */
    private Issue returnIssue;

    /**
     * 3. 加入一個可以注入回傳物件Issue的方法
     */
    public void setReturnedIssue(Issue returnIssue) {
        this.returnIssue = returnIssue;
    }

    /**
     * 4. 加入一個類別屬性private Long expectedArgumentId，此屬性將作為驗證的期望值
     */
    private Long expectedArgumentId;

    /**
     * 5. 加入一個可以注入expectedArgumentId值的方法
     */
    public void setExpectedArgumentId(Long expectedArgumentId) {
        this.expectedArgumentId = expectedArgumentId;
    }

    /**
     * 6. 加入一個類別屬性private Long actualIdArgument，此屬性將儲存調用deleteById()帶入的方法參數
     */
    private Long actualArgumentId;

    /**
     * 7. 加入一個類別屬性private boolean deleteByIdCalled = false，此屬性將紀錄是否調用過deleteById()
     */
    private boolean deleteByIdCalled = false;

    /**
     * 8. 實作方法deleteById()，調用此方法時
     * 1. 儲存方法參數至屬性actualIdArgument
     * 2. 變更屬性deleteByIdCalled為true
     * 3. 回傳配置的issue物件
     */
    @Override
    public Issue deleteById(Long id) {
        actualArgumentId = id;
        deleteByIdCalled = true;
        return returnIssue;
    }

    /**
     * 9. 實作方法verify()，調用此方法時
     * 1. 驗證deleteByIdCalled為true，意思是驗證deleteById()曾被調用過
     * 2. 驗證actualIdArgument相等於expectedIdArgument
     */
    public void verify() {
        assertThat(deleteByIdCalled)
                .overridingErrorMessage(
                        "Expected that deleteById() was called but it was not"
                ).isTrue();

        assertThat(actualArgumentId)
                .overridingErrorMessage("Invalid id argument. Expected: %d but was: %d"
                        , expectedArgumentId
                        , actualArgumentId)
                .isEqualByComparingTo(expectedArgumentId);
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Issue issue) {

    }

    @Override
    public Issue findById(Long id) {
        return null;
    }

    @Override
    public Issue save(Issue issue) {
        return null;
    }

    @Override
    public Issue find(Issue issue) {
        return null;
    }
}
