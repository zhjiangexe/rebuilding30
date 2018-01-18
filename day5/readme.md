# 自訂斷言

## 待測系統介紹

這裡Issue類別，類別具有屬性如下
1. id (Issue編號)
2. activeState (Issue執行狀態TODO, PROGRESS, DONE)
3. resultState (Issue執行結果RESOLVED, UNSOLVED)
4. creator (Issue建立人)
5. solver (Issue解決人)
6. log (Issue解決紀錄)

```java
package com.rebuilding.day5;

public class Issue {
    private Long id;
    private ActiveState activeState;
    private ResultState resultState;
    private String creator;
    private String solver;
    private String log;
    // getter, setter
}
```

我們想要驗證「Issue已解決」，必須簡單達成兩個條件
1. activeState必須為DONE
2. log必須有寫入紀錄

透過軟斷言來編寫一個測試，目的是驗證「Issue已解決」，範例如下

> - 今天請先把焦點從建立Issue的部分(不是重點)，移轉到編寫斷言上
> - overridingErrorMessage()方法表示驗證發生錯誤時，顯示我們自己編寫的錯誤訊息

```java
package com.rebuilding.day5;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class IssueTest {
    private static final String CREATOR = "CREATOR";

    @Test
    public void testIssue1isResolved() {
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setActiveState(ActiveState.DONE);
        issue.setCreator(CREATOR);

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
```

可以看到testIssue1isResolved，建立了一個Issue，並且為了驗證「Issue已解決」，使用軟斷言編寫了兩個判斷，這樣的方式會帶來幾個問題：

- 它不像只有一個斷言的測試那麼容易閱讀。每多一個斷言就多難以閱讀一點，而且通常實際應用的複雜狀態需要更多的斷言，難以閱讀的情況會更加重。
- 它包含實現細節。這可能會讓讀者的注意力從我們測試的本質轉移到實現的細節上，編寫得好是多花時間閱讀，編寫得不好是難以閱讀。
- 業務邏輯被加入到我們的測試中。這是一個糟糕作法。假設我們改變了這個「Issue已解決」的判斷邏輯，同時有其他測試案例做相同的判斷，那麼同樣的我也需要維護他們，這可能會是一場惡夢。

這些問題我們或許可以透過自訂斷言來解決。

## 建立一個自訂斷言

請依照以下步驟來執行

第一，建立一個IssueAssert類別，它必須是public final，並繼承AbstractAssert<SELF, actual>
```java
package com.rebuilding.day5;
import org.assertj.core.api.AbstractAssert;

public final class IssueAssert extends AbstractAssert<IssueAssert, Issue> {

}
```

第二，建立一個private的建構子
1. 將實際的Issue物件做為建構子參數
2. 透過super調用AbstractAssert類別的建構子，並將Issue實際值物件及IssueAssert.class當作參數傳遞

```java
package com.rebuilding.day5;
import org.assertj.core.api.AbstractAssert;

public final class IssueAssert extends AbstractAssert<IssueAssert, Issue> {
    private IssueAssert(Issue actual) {
        super(actual, IssueAssert.class);
    }
}
```

第三，新增一個給外部調用的靜態工廠方法， 因為此斷言類別是為Issue物件所編寫，所以方法命名為assertThatIssue
```java
package com.rebuilding.day5;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;

public final class IssueAssert extends AbstractAssert<IssueAssert, Issue> {
    private IssueAssert(Issue actual) {
        super(actual, IssueAssert.class);
    }

    public static IssueAssert assertThatIssue(Issue actual) {
        return new IssueAssert(actual);
    }
}
```

第四，我們必須加入自訂的斷言方法到IssueAssert類別，依照以下步驟
1. 加入一個public isResolved()方法到IssueAssert類別，這個方法不帶方法參數，且回傳IssueAssert物件的引用
2. 使用SoftAssertions類別驗證實際Issue物件已有回覆
3. 回傳一個使用的IssueAssert物件參考。這給我們鏈結我們自訂斷言的可能性。

在這裡範例中，isResolved()方法用來驗證「Issue已解決」
```java
package com.rebuilding.day5;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;

public final class IssueAssert extends AbstractAssert<IssueAssert, Issue> {
    private IssueAssert(Issue actual) {
        super(actual, IssueAssert.class);
    }

    public static IssueAssert assertThatIssue(Issue actual) {
        return new IssueAssert(actual);
    }

    public IssueAssert isResolved() {
        SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(actual.getActiveState())
                .overridingErrorMessage(
                        "已解決的問題的活動狀態必需是已完成的(DONE), 但是為: %s",
                        actual.getActiveState()
                )
                .isEqualTo(ActiveState.DONE);

        assertions.assertThat(actual.getLog())
                .overridingErrorMessage(
                        "已解決的問題的必需要有紀錄內容，但是它的紀錄內容: %s",
                        actual.getLog()
                )
                .isNotNull();

        assertions.assertAll();
        return this;
    }
}
```

接下來，讓我的測試案例使用自訂斷言IssueAssert
```java
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
```

看到原本從軟斷言到自訂斷言的最終結果，是不是清爽許多，那我們應該總是使用自訂斷言嗎？

## 什麼時候應該用自訂斷言

因為自訂斷言實際上會增加一個類別，會增加我們整體測試的複雜度，只有當它的利大於弊時才使用。

使用它可能會有幾個原因：
- 使用自訂斷言比使用一般斷言更容易閱讀
- 因為移除實作細節，使得我們的測試更容易閱讀
- 自訂斷言使得測試案例可以移除業務邏輯，並因為集中管理，使我們的測試更容易編寫及維護。
