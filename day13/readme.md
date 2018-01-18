# Test Doubles: Stub 堅持手工

通常來說待測類別都會有一些外部相依的存在，他們可能會造成：
- 測試可能因為相依而變慢，如網路、資料庫、檔案、外部物件等。
- 導致誤判測試的結果，是SUT本身出錯，還是相依的物件出錯。
- 等待相依的物件開發完成，才能測試待測物件。
- 無法測試，譬如開發與正式的環境不一樣。

為了解決單元測試遇到相依的問題，接下來我們會使用Test Doubles(測試替身)來替換待測類別的相依。

Test Doubles測試替身的種類可以分得很細：
1. Stub
2. Mock
3. Fake
4. Spy
5. Dummy

今天就先從Stub開始介紹吧，同時會介紹手刻Stub以及使用Mock工具Mockito建立。

## 什麼是Stub

Stub是一個用來代替待測系統的外部相依物件
- Stub必須提供與被替換的相依介面或類別一樣的API。
- 可以制定調用Stub的方法回傳的值、物件、或錯誤。
- 而每一次調用Stub方法時，都會依照制定回傳。

## 待測系統介紹

待測類別IssueServiceImp，有一個相依屬性IssueRepository，要測試的方法find(Issue issue)
```java
package com.rebuilding.day13.service.imp;
import com.rebuilding.day13.entity.Issue;
import com.rebuilding.day13.repository.IssueRepository;

public class IssueServiceImp {

    private IssueRepository repository;

    /**
    * 傳入帶有ID的Issue物件，透過IssueRepository找到完整的Issue，本篇要測試的方法
    **/
    public Issue find(Issue issue) {
        return repository.find(issue);
    }

    /**
     * IssueRepository的注入方法
     */
    public void setRepository(IssueRepository repository) {
        this.repository = repository;
    }
}
```


相依提供了一個介面IssueRepository，並有一個傳入參數Issue，並回傳Issue物件的find()的方法，
```java
public interface IssueRepository {

    Issue find(Issue issue);

}
```

## 手工建立Stub

有了外部相依的介面之後，我們可以根據此介面來建立一個Stub，依照以下步驟：

1. 建立一個實作IssueRepository的IssueRepositoryStub，並將他設定為final避免被繼承
2. 加入一個類別屬性private final Issue
3. 加入一個可以注入Issue物件的建構函數
4. 實作find()方法會回傳的找到的Issue物件，帶有title及desc

```java
package com.rebuilding.day13.repository;
import com.rebuilding.day13.entity.Issue;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositoryStub，並將其設定為final避免被繼承
 */
final public class IssueRepositoryStub implements IssueRepository {

    /**
     * 2. 加入一個類別屬性private final Issue
     */
    private final Issue issue;

    /**
     * 3. 加入一個可以注入Issue物件的建構函式
     */
    IssueRepositoryStub(Issue issue) {
        this.issue = issue;
    }

    /**
     * 4. 設定find方法會回傳的找到的Issue物件，帶有title及desc
     */
    @Override
    public Issue find(Issue issue) {
        issue.setTitle(this.issue.getTitle());
        issue.setDesc(this.issue.getDesc());
        return issue;
    }
}
```


## 使用Stub

得到了一個簡單的Stub，讓我們來看看該怎麼使用他呢？
1. 由於Stub並非無狀態的物件，所以在每個測試案例之前我們都必須建立新的Stub，所以我們在@Before註解的方法裡建立Stub。
    1. 建立Issue物件，並設定其title及desc屬性值，其為模擬IssueRepository.find()找到完整的Issue內容。
    2. 並透過構造函式注入Issue物件到IssueRepositoryStub建立
2. 編寫測試案例，這裡是簡單地驗證"當調用方法find，於是回傳具有Title的Issue"
    1. 調用IssueRepository介面的方法find()
    2. 驗證回傳的Issue是否為預期帶有Title
```java
package com.rebuilding.day13.repository;

import com.rebuilding.day13.entity.Issue;
import com.rebuilding.day13.service.imp.IssueServiceImp;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
         * 建立Issue物件，並設定其title及desc屬性值，其為模擬IssueRepository.find()找到完整的Issue內容
         * 並透過構造函式注入Issue物件到IssueRepositoryStub建立
         */
        Issue configured = new Issue();
        configured.setTitle("TITLE");
        configured.setDesc("DESC");
        repository = new IssueRepositoryStub(configured);
    }

    /**
     * 2. 編寫測試案例，這裡是簡單地驗證"當調用方法find，於是回傳具有Title的Issue"
     */
    @Test
    public void When_Find_Then_ReturnHasTitleIssue() {
        /**
         * 2-1. 初始化待測物件issueServiceImp，初始化方法參數issue
         */
        IssueServiceImp issueServiceImp = new IssueServiceImp();
        issueServiceImp.setRepository(repository);
        Issue issue = new Issue();
        issue.setId(ID);

        /**
         * 2-2. 調用IssueRepository介面的方法find()
         */
        Issue actual = issueServiceImp.find(issue);

        /**
         * 2-3. 驗證回傳的Issue是否為預期帶有Title
         */
        assertThat(actual.getTitle()).isNotEmpty();
    }
}
```

從這個例子可以知道Stub這個Test Double：
- 可以定制調用方法所回傳的值
- 每次調用會回傳相同的值
- 無法驗證待測系統與Stub之間發生的互動
- 如果外部相依只是一個資料來源，我們可以用Stub來替換

如果外部相依會儲存資料到資料庫、向外部發送Http Request等，如果我們無法驗證這部分待測系統是否有按照預期執行，則不應該執行。

通常不管待測系統與外部相依如何互動，重點使用在

1. 驗證待測系統的回傳值。
2. 驗證待測系統的狀態改變。

所以我們透過Stub直接模擬外部相依，指定外部相依的回傳，來驗證待測系統的變化。

下一篇讓我們來看看使用Mockito來建立Stub