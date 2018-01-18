# 建立測試資料By Test Data Builder

## Test Data Builder介紹

Test Data builder是使用建造者模式來建立測試案例會用到的測試資料，主要用來解決：

1. 當用new建構子或工廠方法建構測試資料時，必須清楚傳遞的參數及其順序，使得建構資料緩慢。
2. 為了簡單地建立具有變化的測試資料，我們會建立新的工廠方法，並將其放置ObjectMother模式，但是越來越多不同的測試資料，會造成ObjectMother類別越來越肥大，並且難以維護。
3. 想要很容易地識別建立的物件其屬性值，讓讀者一看就知道所建立的物件為何。

也就是說，想要透過簡單的方法調用，就可以建立我們想要的物件，隱藏了其建立的細節。

## 以資料為中心的test data builder

第一，開始來建立Issue物件的Test Data Builder，請執行以下步驟：

1. 建立一個final類別，不讓其他開發人遠可以繼承此類別，類別名稱則以建立的物件為開頭，並以Builder為後綴
2. 將Issue類別中的屬性加入到Builder中
3. 增加沒有參數的建構子到建立的類別
4. 為各個屬性建立設置其屬性值的方法，並且回傳this實現對builder的引用，方法名稱可以直接以屬性名稱當方法名title(String title)、或者使用withTitle(String title)
5. 建立build()方法，它不傳遞任何參數，並且新增Issue物件及設置屬性值之後回傳

```java
package com.rebuilding.day10.dataCentral;

import com.rebuilding.day10.*;

/**
 * 1. 建立一個final類別，不讓其他開發人遠可以繼承此類別，類別名稱則以建立的物件為開頭，並以Builder為後綴
 */
public class IssueBuilder {
    /**
     * 2. 將Issue類別中的屬性加入到Builder中
     */
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    /**
     * 3. 增加沒有參數的建構子到建立的類別
     */
    public IssueBuilder() {
    }

    /**
     * 4. 為各個屬性建立設置其屬性值的方法，並且回傳this實現對builder的引用，方法名稱可以直接以屬性名稱當方法名title(String title)、或者使用withTitle(String title)
     */
    public IssueBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public IssueBuilder title(String title) {
        this.title = title;
        return this;
    }

    public IssueBuilder desc(String desc) {
        this.desc = desc;
        return this;
    }

    public IssueBuilder creator(Long creatorNum) {
        this.creator = new Creator(creatorNum);
        return this;
    }

    public IssueBuilder assignee(Long assigneeNum) {
        this.assignee = new Assignee(assigneeNum);
        return this;
    }

    public IssueBuilder closer(Long closerNum) {
        this.closer = new Closer(closerNum);
        return this;
    }

    public IssueBuilder state(State state) {
        this.state = state;
        return this;
    }

    public IssueBuilder result(Result result) {
        this.result = result;
        return this;
    }

    /**
     * 5. 建立build()方法，它不傳遞任何參數，並且新增Issue物件及設置屬性值之後回傳
     */
    public Issue build() {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(creator);
        issue.setAssignee(assignee);
        issue.setCloser(closer);
        issue.setState(state);
        issue.setResult(result);
        return issue;
    }
}

```

第二，使用Test Data Builder建立測試資料，請執行以下步驟：

1. 在測試類別內加入會使用到的常數
2. 使用new建立createDoneIssueWithResolved()需要的測試資料
3. 使用IssueBuilder建立buildDoneIssueWithResolved()需要的測試資料

```java
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
```

可以看到兩個測試方法中，建立測試資料的方式，區別只有在是否使用流暢API來設置屬性值，看起來沒有比較容易；而本測試案例buildDoneIssueWithResolved必須建立一個已「活動狀態為完成，結案形式為已解決」的Issue，在此之前我們必須知道此Issue：

1. 活動狀態必須為State.DONE
2. 結案形式必須為Result.RESOLVED
3. 必須指定creator建立人
4. 必須指定assignee負責人
5. 必須指定closer結案人

所以，如果我們使用以資料為中心的test data builder來建立具有不同狀態的物件，只是增加測試程式的複雜度而已，那要如何解決呢？

## 根據需求評估來建立Test Data Builder

接下來將會把物件的建立邏輯從測試案例中搬移到Test Data Builder裡，提供好用的API，使測試容易閱讀及編寫。
但是在此之前，必須為Test Data Builder找出具體的需求，讓我們依照以下步驟進行：

1. 識別出物件的所有狀態。而Test Data Builder必須支援這些狀態。
2. 逐一確定這些狀態必須的物件屬性值。如果該屬性不造成影響則忽略。
3. 我們可以找出一些Issue具有下列狀態：
    1. 待處理的Issue
        - creatorNum: set
        - assigneeNum: set or null
        - closerNum: null
        - state: State.TODO
        - result: null

    2. 執行中的Issue
        - creatorNum: set
        - assigneeNum: set
        - closerNum: null
        - state: State.IN_PROGRESS
        - result: null

    3. 已完成的Issue
        - creatorNum: set
        - assigneeNum: set
        - closerNum: set
        - state: State.DONE
        - result: Result.RESOLVED, Result.REJECT

現在已經找出這些需求，讓我們依照下列規則來建立新的Test Data Builder
1. 找出設定物件狀態的屬性，因為設定屬性是建立物件中比較複雜的部份，這部分的邏輯接下來將會移到Test Data Builder類別裡。
2. 找出多個狀態所需要的屬性，例如creatorNum及assigneeNum會由State=TODO, IN_PROGRESS, DONE所需要，而creatorNum及assigneeNum並不會設定狀態，所以接下來會以資料為中心的方式來設定creator及assignee的值。
3. 找出只有一個狀態所需要的屬性，例如closerNum及result，它們只有在State.DONE的時候才會被設定，所以接下來會編寫一個方法，它會在狀態為DONE的時候同時設置closerNum及result。
4. 找出其餘屬性，使用資料為中心的方法來設定這些屬性。

依照上列規則，我們會有較具體的需求來建立我們新Test Data Builder，需求如下：
1. assigneeNum, creatorNum, id, title, desc的屬性值使用資料為中心的方法來設定。
2. 建立設定Issue物件狀態的方法，這些方法會對於Test Data Builder的使用者隱藏複雜的業務邏輯。
3. Test Data Builder的使用者只有在建立狀態為State.DONE，才可以設定closerNum及result的值。

接下來，開始執行，Test Data Builder改造的步驟

第一，按照上述規則及需求，我們先對原先的Test Data Builder執行一些整理：
1. 只留下assigneeNum, creatorNum, id, title, desc屬性值的設定方法
2. 移除設定狀態的state(State state)方法
3. 移除只有當狀態為State.DONE才會設置屬性的方法closerNum(Long closerNum), result(Result result)

整理後的程式碼如下：
```java
public class IssueBuilder {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    public IssueBuilder() {
    }

    /**
    * 1. 只留下assigneeNum, creatorNum, id, title, desc屬性值的設定方法
    **/
    public IssueBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public IssueBuilder title(String title) {
        this.title = title;
        return this;
    }

    public IssueBuilder desc(String desc) {
        this.desc = desc;
        return this;
    }

    public IssueBuilder creatorNum(Long creatorNum) {
        this.creator = new Creator(creatorNum);
        return this;
    }

    public IssueBuilder assigneeNum(Long assigneeNum) {
        this.assignee = new Assignee(assigneeNum);
        return this;
    }
    /**
    * 2. 移除設定狀態的state(State state)方法
    **/
    /**
    * 3. 移除只有當狀態為State.DONE才會設置屬性的方法closerNum(Long closerNum), result(Result result)
    **/
    public Issue build() {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(creator);
        issue.setAssignee(assignee);
        issue.setCloser(closer);
        issue.setState(state);
        issue.setResult(result);
        return issue;
    }
}
```


第二，重建設定狀態的方法，在這裡因為不是直接指定屬性值，所以使用with當作前綴來表示
1. 建立方法withStateTodo()，描述Issue狀態為待處理。
2. 建立方法withStateInProgress()，描述Issue狀態為執行中。
3. 建立方法withStateDone(Long closerNum, Result result)，傳入參數closerNum及result，但這裡的Result可能為RESOLVED及REJECT，我們可以改成更具體的方法。
    - withResultResolved(Long closerNum)，描述Issue已解決，並帶入結案人
    - withResultReject(Long closerNum)，描述Issue被駁回，並帶入結案人
4. 額外的，如果要確保Issue不能沒有creator，則修改build()執行時要做判斷。

重建後的程式碼如下：
```java
package com.rebuilding.day10.stateful;

import com.rebuilding.day10.*;

public class IssueBuilder {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    public IssueBuilder() {
    }

    public IssueBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public IssueBuilder title(String title) {
        this.title = title;
        return this;
    }

    public IssueBuilder desc(String desc) {
        this.desc = desc;
        return this;
    }

    public IssueBuilder creator(Long creatorNum) {
        this.creator = new Creator(creatorNum);
        return this;
    }

    public IssueBuilder assignee(Long assigneeNum) {
        this.assignee = new Assignee(assigneeNum);
        return this;
    }

    /**
     * 1. 建立方法withStateTodo()，描述Issue狀態為待處理。
     */
    public IssueBuilder withStateTodo() {
        this.state = State.TODO;
        return this;
    }

    /**
     * 2. 建立方法withStateInProgress()，描述Issue狀態為執行中。
     */
    public IssueBuilder withStateInProgress() {
        this.state = State.IN_PROGRESS;
        return this;
    }

    /**
     * 3. withResultResolved(Long closerNum)，描述Issue已解決，並帶入結案人
     */
    public IssueBuilder withResultResolved(Long closerNum) {
        this.closer = new Closer(closerNum);
        this.state = State.DONE;
        this.result = Result.RESOLVED;
        return this;
    }

    /**
     * 3. withResultReject(Long closerNum)，描述Issue被駁回，並帶入結案人
     */
    public IssueBuilder withResultReject(Long closerNum) {
        this.closer = new Closer(closerNum);
        this.state = State.DONE;
        this.result = Result.REJECT;
        return this;
    }

    public Issue build() {
        /**
         * 4. 額外的，如果要確保Issue不能沒有creator，則修改build()執行時要做判斷。
         */
        if (creator == null) {
            throw new NullPointerException(
                    "You cannot create a task without creator"
            );
        }
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(creator);
        issue.setAssignee(assignee);
        issue.setCloser(closer);
        issue.setState(state);
        issue.setResult(result);
        return issue;
    }
}
```

使用改進後的Test Data Builder來建立測試資料
1. 待處理的Issue，未有負責人
2. 待處理的Issue，有負責人
3. 執行中的Issue
4. 已解決的Issue
5. 已駁回的Issue，未有負責人

IssueServiceTest如下：
```java
package com.rebuilding.day10.stateful;

import com.rebuilding.day10.Issue;
import org.junit.Test;

public class IssueServiceTest {
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    /**
     * 1. 待處理的Issue，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .withStateTodo()
                .build();
    }

    /**
     * 2. 待處理的Issue，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .assignee(ASSIGNEE_NUM)
                .withStateTodo()
                .build();
    }

    /**
     * 3. 執行中的Issue
     */
    @Test
    public void createInProgressIssue() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .assignee(ASSIGNEE_NUM)
                .withStateInProgress()
                .build();
    }

    /**
     * 4. 已解決的Issue
     */
    @Test
    public void createResolvedIssue() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .assignee(ASSIGNEE_NUM)
                .withResultResolved(CLOSER_NUM)
                .build();
    }

    /**
     * 5. 已駁回的Issue，未有負責人
     */
    @Test
    public void createRejectIssueWithoutAssignee() {
        Issue issue = new IssueBuilder()
                .id(ID)
                .title(TITLE)
                .desc(DESC)
                .creator(CREATOR_NUM)
                .withResultResolved(CLOSER_NUM)
                .build();
    }
}
```


## Test Data Builder的使用時機

我們必須清楚使用Test Data Builder的好處是否大於未來需要維護一個額外類別的成本，注意事項如下：

1. 透過清楚且易維護的方式建立可變測試資料，是它帶來的可能性。
2. 多出一個額外的類別需要維護。
3. 如果只是單純的java bean方式則不要使用，因為。
4. 如果只是少數測試類別使用，則要注意其維護成本會高於帶來的好處。

當根據需求評估來建立Test Data Builder，注意事項如下：

1. 無狀態的屬性值如id, title, desc, creator, assignee，以資料為中心的方式設定。
2. 具有狀態的屬性，不以資料為中心的方式設定，需透過清楚明確的方法及參數來設定，並將複雜的業務邏輯隱藏。
3. 實際上業務邏輯或規則並無法完全隱藏，所以在編寫之前，我們必須明確且具體的了解需求。
4. 一樣要注意其維護成本是否會高於帶來的好處。