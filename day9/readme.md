## 工廠方法(Factory Methods)介紹

一開始先為我們的工廠方法的定義：
- 建立一個新的物件，設置其物件屬性值，然後回傳物件
- 不一定要帶入方法參數

以下是一個簡單的例子：

```java
public Issue createIssue(){
    // 不帶參數，建立Issue，設置屬性值，回傳物件
}

public Issue createIssue(String title, String desc) {
    // 帶參數，建立Issue，設置屬性值，回傳物件
}
```

另外，建立工廠方法之前，我們得先
1. 替方法找出一個好名字。對於測試來說，容易閱讀及維護是最重要的事情，避免泛用的名稱，盡量具體指出建立的物件及其屬性為何。
2. 決定方法參數數量。如果參數太少，就無法找出具描述性且簡短的名字。如果參數太多，則會造成維護的困難。
3. 決定放置的位置。如果工廠方法只被一個測試類別使用，則放到測試類別即可。但如果是多個測試類別共用，則建議提取出來放到共用的ObjectMother。

每一個測試適用的工廠方法不盡相同，我們必須試著找出來較佳的作法。

## 使用工廠方法建立測試資料

接續前面測試類別IssueServiceTest，以下將會試著使用3種做法
1. 在測試類別中使用無方法參數的工廠方法
2. 在測試類別中使用帶有方法參數的工廠方法
3. 將工廠方法提出取來放到ObjectMother


### 1. 在測試類別中使用無方法參數的工廠方法

這裡將原本在測試方法中建立Issue物件的程式碼，拉出來變成private並回傳Issue物件的工廠方法，程式碼如下：

```java
package com.rebuilding.day9;

import org.junit.Test;

public class IssueServiceTest1 {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    @Test
    public void createTodoIssueWithoutAssigneeByFactoryMethod() {
        Issue issue = createTodoIssueWithoutAssignee();
    }

    private Issue createTodoIssueWithoutAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.TODO);
        return issue;
    }

    @Test
    public void createTodoIssueWithAssigneeByFactoryMethod() {
        Issue issue = createTodoIssueWithAssignee();
    }

    private Issue createTodoIssueWithAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.TODO);
        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        return issue;
    }

    @Test
    public void createDoneIssueWithResolvedByFactoryMethod() {
        Issue issue = createDoneIssueWithResolved();
    }

    private Issue createDoneIssueWithResolved() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.DONE);
        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        issue.setCloser(new Closer(CLOSER_NUM));
        issue.setResult(Result.RESOLVED);
        return issue;
    }
}
```

在這個範例中，工廠方法都有不錯的名字，也回傳了相符的Issue物件，但它不帶參數，讓測試資料與測試方法之間的關聯不見了，而且帶來了一些問題：

- 當我們需要了解屬性值，則必須閱讀工廠方法的內容，這會使得我們閱讀速度變慢。
- 如果要為建立的物件編寫斷言，則必須使用測試類別上聲明的常數來指定期望值，但我們的測試方法裡卻又沒指定實際的屬性值。所以要為建立的物件編寫斷言，則不要使用不帶參數的工廠方法。


### 2. 在測試類別中使用帶有方法參數的工廠方法

這裡不改變工廠方法的命名，將這些方法所需的資訊作為方法參數，重新建立測試資料與測試方法之間的關聯，程式碼如下：

```java
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
```

這個範例，因為重新建立測試資料與測試方法之間的關聯，我們可以看到Issue物件所需的屬性值，所以比較容易閱讀，但還是有問題

- 方法參數太多，那麼它不太帶來太多價值，因為它只是本來方式的包裝
- 如果方法參數有順序及邏輯，則必須確保所有人都了解它的意義


### 3. 將工廠方法提出取來放到ObjectMother

首先，必須建立ObjectMother類別，依照：
1. 建立final public類別，避免開發人員繼承，此ObjectMother通常以建立物件的名稱加上Factory來命名。
2. 因為我們只使用ObjectMother的方法，所以加入private建構函式到類別裡，避免開發人員自己new物件。
3. 接下來，將原本測試類別的工廠方法改成public static並搬移至ObjectMother。

完成後的IssueFactory如下：
```java
package com.rebuilding.day9;

/**
 * 1. 建立final public類別，避免開發人員繼承，此ObjectMother通常以建立物件的名稱加上Factory來命名。
 */
final public class IssueFactory {
    /**
     * 2. 因為我們只使用ObjectMother的方法，所以加入private建構函式到類別裡，避免開發人員自己new物件。
     */
    private IssueFactory() {

    }

    /**
     * 3. 接下來，將原本測試類別的工廠方法改成public static並搬移至ObjectMother。
     */
    public static Issue createTodoIssueWithoutAssignee(Long id, String title, String desc, Long creatorNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.TODO);
        return issue;
    }

    public static Issue createTodoIssueWithAssignee(Long id, String title, String desc, Long creatorNum, Long assigneeNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.TODO);
        issue.setAssignee(new Assignee(assigneeNum));
        return issue;
    }

    public static Issue createDoneIssueWithResolved(Long id, String title, String desc, Long creatorNum, Long assigneeNum, Long closerNum) {
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
```

然後，使用IssueFactory在測試方法中建立測試資料：
```java
package com.rebuilding.day9;

import org.junit.Test;

public class IssueServiceTest3 {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    @Test
    public void createTodoIssueWithoutAssigneeByFactoryMethod() {
        Issue issue = IssueFactory.createTodoIssueWithoutAssignee(ID, TITLE, DESC, CREATOR_NUM);
    }

    @Test
    public void createTodoIssueWithAssigneeByFactoryMethod() {
        Issue issue = IssueFactory.createTodoIssueWithAssignee(ID, TITLE, DESC, CREATOR_NUM, ASSIGNEE_NUM);
    }

    @Test
    public void createDoneIssueWithResolvedByFactoryMethod() {
        Issue issue = IssueFactory.createDoneIssueWithResolved(ID, TITLE, DESC, CREATOR_NUM, ASSIGNEE_NUM, CLOSER_NUM);
    }

}
```

這個範例，可以看到我們的測試類別IssueServiceTest變得相當乾淨，但與前面一樣：

- 方法參數太多，那麼它不會帶來太多價值，因為它只是本來方式的包裝
- 如果方法參數有順序及邏輯，則必須確保所有人都了解它的意義
- ObjectMother不善於處理測試資料變化，如果我們在測試資料變化很大的時候使用它，我們最終會得到一個難以維護的類別。


## 何時可以使用工廠方法

到最後工廠方法似乎還是有些問題，但透過正確的方式使用，它將非常有用。

不要用工廠方法建立物件，因為：
- 有太多的方法參數
- 參數順序需有特定人員才了解的邏輯
- 測試資料及測試方法的聯繫被打破

使用用工廠方法，因為
- 工廠方法沒有太多參數
- 只關心建立的物件的特定屬性
- 使用工廠方法的好處超過缺點

總結：

- 工廠方法使得測試易於編寫和維護。因為將物件建立邏輯從測試方法移到工廠方法中，所以這個邏輯只能從一個地方找到及維護而達到效果。
- 工廠方法使得測試易於閱讀。如果工廠方法有正確的命名可以理解建立的物件，且不需要太多的方法參數，則測試將會更容易閱讀。

下一篇是builder建造者出場