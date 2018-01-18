# 建立測試資料

本篇主題在於如何建立測試資料，使其易讀、好維護。

## 待測系統介紹

- Issue類別，每個屬性都有其getter、setter，Java Bean模式
```java
public class Issue {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT
    // getter
    // setter
}
```

- State狀態列舉
```java
public enum State {
    TODO, // 待處理
    IN_PROGRESS, // 執行中
    DONE; // 已結案

    State() {
    }
}
```

- Result結案方式列舉
```java
public enum Result {
    RESOLVED, // 已解決
    REJECT; // 已駁回

    Result() {
    }
}
```

- Creator建立人
```java
public class Creator {
    private final Long memberNum;

    public Creator(Long memberNum) {
        this.memberNum = memberNum;
    }
}
```

- 負責人
```java
public class Assignee {
    private final Long memberNum;

    public Assignee(Long memberNum) {
        this.memberNum = memberNum;
    }
}
```

- 結案人
```java
public class Closer {
    private final Long memberNum;

    public Closer(Long memberNum) {
        this.memberNum = memberNum;
    }

}
```

## 建立測試資料

讓我們依照下列步驟來建立測試資料：
1. 建立測試類別
2. 將所需的常數加入到測試類別中
3. 這裡我們將建立三種狀態的Issue物件來進行測試。除了必填屬性為id, title, desc, creator，他們各自有需要填入的屬性
    1. Issue待處理，未有負責人
        - state
    2. Issue待處理，有負責人
        - state
        - assignee
    3. Issue已結案，結案形式為已解決
        - state
        - assignee
        - closer
        - result

```java
package com.rebuilding.day8;

import org.junit.Test;

/**
 * 1. 建立測試類別
 */
public class IssueServiceTest {
    /**
     * 2. 將所需的常數加入到測試類別中
     */
    private static final Long ID = 1L;
    private static final Long CREATOR_NUM = 5L;
    private static final Long ASSIGNEE_NUM = 10L;
    private static final Long CLOSER_NUM = 15L;

    private static final String TITLE = "this title";
    private static final String DESC = "this desc";

    /**
     * 3-1. Issue待處理，未有負責人
     */
    @Test
    public void createTodoIssueWithoutAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setState(State.TODO);
    }

    /**
     * 3-2. Issue待處理，有負責人
     */
    @Test
    public void createTodoIssueWithAssignee() {
        Issue issue = new Issue();
        issue.setId(ID);
        issue.setTitle(TITLE);
        issue.setDesc(DESC);
        issue.setCreator(new Creator(CREATOR_NUM));

        issue.setAssignee(new Assignee(ASSIGNEE_NUM));
        issue.setState(State.TODO);
    }

    /**
     * 3-3. Issue已結案，結案形式為已解決
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
}

```

在這個範例中可以看到幾個問題：
1. 讀者或維護者必須熟悉每個屬性，不同Issue狀態有不一樣的屬性值需要填入，反過來說，某一屬性值我們必須了解在什麼狀態下需要填入，這會造成讀寫上的困難。
2. 由於建立物件的邏輯分散在各個測試案例，如果對Issue狀態所需的屬性值有所變更，那我們就必須找出所有的測試案例進行更改，這樣不僅會造成維護上的困難，甚至沒有更改到的話可能會發生不對的測試而不自知。

我們必須知道：

- 建立的物件具有不同的狀態，則不該在測試中使用java bean模式來建立。除非僅有單一或少數測試類別使用到，或者使用此方法建立物件是優點大於缺點。
- 建立的物件不具狀態，只用於傳遞資料，則可以使用java bean模式來建立

明天，讓我們來看看解決的方式