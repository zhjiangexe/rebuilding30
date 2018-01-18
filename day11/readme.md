# 建立測試資料 By Test Data Builder(續)

當我們希望不提供太多細節，就可以建立一個具有複雜狀態的物件時，就有可能透過Builder Pattern來達成。
而Builder Pattern還可以幫我們：
- 提供一個介面，建立具有初始狀態的物件
- 避免建立一個具有無效狀態的物件

## 待測系統介紹

在這裡，我們要替Issue類別實作了Builder Pattern，透過以下步驟

1. 移除setter，只留下getter
2. 建立一個內部類別Builder
    1. 加入屬性id, title, desc, assignee, creator。
    2. 加入private建構函式到類別裡，避免開發人員自己new物件。
    3. 加入屬性id, title, desc, assignee, creator設定的方法，並回傳builder的引用
    4. 加入build()方法，執行Issue類別的private建構函式，產生Issue物件回傳。
3. 加入private建構函式，建立初始狀態為待處理(TODO)的Issue物件
4. 加入靜態方法getBuilder()，回傳Builder

程式碼如下：
```java
package com.rebuilding.day11.builderPattern;

import com.rebuilding.day11.*;

public class Issue {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    /**
     * 1. 移除setter，只留下getter
     */
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Creator getCreator() {
        return creator;
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public Closer getCloser() {
        return closer;
    }

    public State getState() {
        return state;
    }

    public Result getResult() {
        return result;
    }

    /**
     * 2. 建立一個內部類別Builder
     */
    public static class Builder {
        /**
         * 2-1. 加入屬性id, title, desc, assignee, creator。
         */
        private Long id;
        private String title;
        private String desc;
        private Creator creator;
        private Assignee assignee;

        /**
         * 2-2. 加入private建構函式到類別裡，避免開發人員自己new物件。
         */
        private Builder() {
        }

        /**
         * 2-3. 加入屬性id, title, desc, assignee, creator設定的方法，並回傳builder的引用
         */
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder creator(Long creatorNum) {
            if (creatorNum != null) {
                this.creator = new Creator(creatorNum);
            }
            return this;
        }

        public Builder assignee(Long assigneeNum) {
            if (assigneeNum != null) {
                this.assignee = new Assignee(assigneeNum);
            }
            return this;
        }

        /**
         * 2-4. 加入build()方法，執行Issue類別的private建構函式，產生Issue物件回傳。
         */
        public Issue build() {
            return new Issue(this);
        }
    }

    /**
     * 3. 加入private建構函式，建立初始狀態為待處理的Issue物件
     */
    private Issue(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.desc = builder.desc;
        this.creator = builder.creator;
        this.assignee = builder.assignee;
        this.closer = null;
        this.state = State.TODO;
        this.result = null;
    }

    /**
     * 4. 加入靜態方法getBuilder()，回傳Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

}
```

仔細研究這個實作Builder Pattern的Issue類別可以發現：
1. 如果想改變Issue狀態為執行中(IN_PROGRESS)，必須找到方法來設置state
2. 如果想改變Issue狀態為已結案(DONE)，必須找到方法來設置state及result

而這個Builder無法達到我們的要求，那麼難道破壞原本的封裝(在Issue類別中加入設定state、result的方法)是個好解法嗎？

## 建立Test Data Builder，覆蓋Builder Pattern

接下來，為了符合實作了Builder Pattern的Issue類別，並且不破壞其原本的封裝，我們將要修改前一篇提到的Test Data Builder，請依照以下步驟執行：

1. 將屬性creator, assignee, closer修改為creatorNum, assigneeNum, closerNum
2. 並將方法creator(Long creatorNum), assignee(Long assigneeNum)內部設值方式，從this.creator = new Creator(creatorNum)修改為this.creatorNum = creatorNum
3. 將withResultResolved(Long closerNum), withResultReject(Long closerNum)內部設值的方式this.closerNum = new Closer(closerNum)修改為this.closerNum = closerNum
4. 修改build()，透過Issue類別所提供的Builder來建立Issue物件並回傳。
5. 由於Issue並未提供方法來設定closer, state, result，所以這邊會透過反射來達成。
    1. 建立共用反射設值的方法setFieldValue(Issue target, String fieldName, Object fieldValue)
    2. 使用setFieldValue()方法，來設定closer, state, result

完成後的程式碼如下：
```java
package com.rebuilding.day11.builderPattern;

import com.rebuilding.day11.Closer;
import com.rebuilding.day11.Result;
import com.rebuilding.day11.State;

import java.lang.reflect.Field;

public class IssueBuilder {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    /**
     * 1. 將屬性creator, assignee, closer修改為creatorNum, assigneeNum, closerNum
     */
    private Long creatorNum; // 建立人
    private Long assigneeNum; // 負責人
    private Long closerNum; // 結案人
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

    /**
     * 2. 並將方法creator(Long creatorNum), assignee(Long assigneeNum)內部設值方式，從this.creator = new Creator(creatorNum)修改為this.creatorNum = creatorNum
     */
    public IssueBuilder creator(Long creatorNum) {
        this.creatorNum = creatorNum;
        return this;
    }

    public IssueBuilder assignee(Long assigneeNum) {
        this.assigneeNum = assigneeNum;
        return this;
    }

    public IssueBuilder withStateTodo() {
        this.state = State.TODO;
        return this;
    }

    public IssueBuilder withStateInProgress() {
        this.state = State.IN_PROGRESS;
        return this;
    }

    /**
     * 3. 將withResultResolved(Long closerNum), withResultReject(Long closerNum)內部設值的方式this.closerNum = new Closer(closerNum)修改為this.closerNum = closerNum
     */
    public IssueBuilder withResultResolved(Long closerNum) {
        this.closerNum = closerNum;
        this.state = State.DONE;
        this.result = Result.RESOLVED;
        return this;
    }

    public IssueBuilder withResultReject(Long closerNum) {
        this.closerNum = closerNum;
        this.state = State.DONE;
        this.result = Result.REJECT;
        return this;
    }

    /**
     * 4. 修改build()，透過Issue類別所提供的Builder來建立Issue物件並回傳。
     */
    public Issue build() {
        Issue issue = Issue.getBuilder()
                .id(id)
                .title(title)
                .desc(desc)
                .creator(creatorNum)
                .assignee(assigneeNum)
                .build();

        /**
         * 5-2. 使用setFieldValue()方法，來設定closer, state, result
         */
        if (closerNum != null) {
            setFieldValue(issue, "closer", new Closer(closerNum));
        }

        setFieldValue(issue, "state", state);
        setFieldValue(issue, "result", result);
        return issue;
    }

    /**
     * 5-1. 建立共用反射設值的方法setFieldValue(Issue target, String fieldName, Object fieldValue)
     */
    private void setFieldValue(Issue target, String fieldName, Object fieldValue) {
        try {
            Field targetField = target.getClass().getDeclaredField(fieldName);
            targetField.setAccessible(true);
            targetField.set(target, fieldValue);
        } catch (Exception ex) {
            throw new RuntimeException(
                    String.format("錯誤，無法設定屬性：%s", fieldName),
                    ex
            );
        }
    }
}
```

現在，這個IssueBuilder，達到覆蓋Issue類別Builder Pattern的效果，由於Test Data Builder提供的API沒有變化，所以對於先前的測試範例沒有造成影響。

而一開始建立Issue物件的方式變成Builder Pattern，但由於我們使用Test Data Builder(IssueBuilder)，將其建立的細節隱藏起來，不僅容易閱讀，在Issue發生變化需要修改時，也僅需要修改一個地方。

另外，這裡面使用了反射來設定屬性值closer, state, result，是因為從這兩個選項做了選擇：
1. 破壞Issue類別原本的封裝。也就是說，由Issue提供設定closer, state, result的方法。
2. 使用反射的Test Data Builder

建立新的設置方法，破壞production code的封裝是相對容易的，但原本的封裝有其原因，如果擅自打破，則不能保證所有人都只會在測試中使用這些方法，或許日後會在專案各處發現，相反的反射只有在一個地方就解決了問題。


## 強調建立的物件的狀態

在某些情況，我們只關心建立的物件的狀態，不在意id, title, desc, creator, assignee, closer的屬性值，這些細節可能讓我們感到混亂，話雖如此，依然得為這些屬性設定值。

而這時候，為了避免在測試案例中，為了建立測試資料調用不必要的設值方法，而造成後面的人閱讀上的問題，我們可以試著使用預設值，來為這些屬性設值，並且使用不正常的值來表達我們並不在意此屬性。

這裡我們透過預設必填屬性creatorNum=NOT_IN_USE及title=NOT_IMPORTANT來做示範
```java
public class IssueBuilder {

    private static final Long NOT_IN_USE = -1L;

    private static final String NOT_IMPORTANT = "NOT IMPORTANT";

    private Long id; // issue單號
    private String title = NOT_IMPORTANT; // 標題
    private String desc; // 內容描述
    private Long creatorNum = NOT_IN_USE; // 建立人
    private Long assigneeNum; // 負責人
    private Long closerNum; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    /** 以下省略 **/
}
```

然後，由於必要的屬性使用了預設值，不需填入，使得測試案例看起來更乾淨。
```java
package com.rebuilding.day11.emphasizing;

import com.rebuilding.day11.builderPattern.Issue;
import org.junit.Test;

public class IssueServiceTest {
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    /**
     * 1. 待處理的Issue，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = new IssueBuilder()
                .withStateTodo()
                .build();
    }

    /**
     * 2. 待處理的Issue，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = new IssueBuilder()
                .assignee(ASSIGNEE_NUM)
                .withStateTodo()
                .build();
    }

    /**
     * 3. 已解決的Issue
     */
    @Test
    public void createResolvedIssue() {
        Issue issue = new IssueBuilder()
                .assignee(ASSIGNEE_NUM)
                .withResultResolved(CLOSER_NUM)
                .build();
    }
}

```

但我們還可以做得更好，先前曾提到工廠方法，工廠方法擅長強調建立的物件的狀態，接下來，我們將把工廠方法與Test Data Builder結合。

在IssueBuilder中加入這些方法：
1. createTodoIssueWithoutAssignee()，待處理的Issue，未有負責人
2. createTodoIssueWithAssignee()，待處理的Issue，有負責人
3. createResolvedIssue()，已解決的Issue

```java
public class IssueBuilder {

    private static final Long NOT_IN_USE = -1L;

    private static final String NOT_IMPORTANT = "NOT IMPORTANT";

    private Long id; // issue單號

    private String title = NOT_IMPORTANT; // 標題
    private String desc; // 內容描述

    private Long creatorNum = NOT_IN_USE; // 建立人
    private Long assigneeNum; // 負責人
    private Long closerNum; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    public IssueBuilder() {
    }

    public static Issue createTodoIssueWithoutAssignee() {
        return new IssueBuilder()
                .withStateTodo()
                .build();
    }

    public static Issue createTodoIssueWithAssignee(Long assigneeNum) {
        return new IssueBuilder()
                .assignee(assigneeNum)
                .withStateTodo()
                .build();
    }

    public static Issue createResolvedIssue(Long closerNum) {
        return new IssueBuilder()
                .assignee(NOT_IN_USE)
                .withResultResolved(closerNum)
                .build();
    }

    // 以下省略
```


可以看到我們的測試類別，使用了新的工廠方法變得更乾淨了
```java
public class IssueServiceTest {
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    /**
     * 1. 待處理的Issue，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = IssueBuilder.createTodoIssueWithoutAssignee();
    }

    /**
     * 2. 待處理的Issue，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = IssueBuilder.createTodoIssueWithAssignee(ASSIGNEE_NUM);
    }

    /**
     * 3. 已解決的Issue
     */
    @Test
    public void createResolvedIssue() {
        Issue issue = IssueBuilder.createResolvedIssue(CLOSER_NUM);
    }
}
```

最後，我們可以發現：

- 如果想要強調建立的物件的狀態，必須盡量減少調用設值方法。
- 可以透過修改Test Data Builder，來為不在意的必填屬性設定預設值，達到減少調用設值方法。
- 為了強調建立的物件的狀態這個目的，使用的工廠方法最多只能有一個參數。

---
以上，這是今天的部分，建立測試資料也就到這邊。
下一篇要來談談一些測試的原則。