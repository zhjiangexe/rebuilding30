# Test Doubles: Spy & Dummy

說在前頭，Spy跟Mock長得很像，

## Spy堅持手工

Spy是一個用來代替待測系統的外部相依物件
- Fake必須提供與被替換的相依介面或類別一樣的API。
- Spy不配置方法調用的回傳
- Spy不會直接驗證待測系統與Spy之間發生的互動，但是它會將訊息記錄下來，並提供取得紀錄的getter方法。

Spy跟Mock非常相似

### 待測系統介紹

IssueRepository介面，帶有儲存Issue的方法
```java
public interface IssueRepository {
    Issue save(Issue issue);
}
```

### 建立Spy堅持手工

有了外部相依的介面之後，我們可以根據此介面來實作一個Spy。

以下為建立完成的IssueRepositorySpy，為了有步驟的建立相關屬性及方法，這裡的程式碼將不依照慣例的格式顯示：
```java
package com.rebuilding.day17;
import com.rebuilding.day17.entity.Issue;
import com.rebuilding.day17.repository.IssueRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositorySpy，並將他設定為final避免被繼承
 */
final public class IssueRepositorySpy implements IssueRepository {

    /**
     * 2. 加入一個int saveExecuteCount屬性，該屬性用來紀錄方法save()執行的次數
     */
    private int saveExecuteCount;

    /**
     * 3. 提供一個可以取得記錄執行次數的方法
     */
    public int getSaveExecuteCount() {
        return saveExecuteCount;
    }

    /**
     * 4. 加入一個屬性List<Issue> issueList，該List用來記錄帶入save()方法參數的Issue物件
     */
    private List<Issue> issueList;

    /**
     * 5. 提供一個取得列表的方法
     */
    public List<Issue> getSavedIssueList() {
        return issueList;
    }

    /**
     * 6. 透過建構函式初始化用來記錄的屬性
     */
    public IssueRepositorySpy() {
        saveExecuteCount = 0;
        issueList = new ArrayList<>();
    }

    /**
     * 7. 實作方法save，並編寫通過此方法欲紀錄的資料
     */
    @Override
    public Issue save(Issue issue) {
        saveExecuteCount++;
        issueList.add(issue);
        return issue;
    }
}
```

### 使用Spy堅持手工

以下測試案例，展示如何使用手工的Spy物件，實際情境是把Spy物件(IssueRepositorySpy)注入到待測系統中，並執行待測系統方法之後，再使用Spy物件提供的方法取得紀錄來進行驗證。
```java
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
```

從這個例子可以知道Spy這個Test Double：
- 用來記錄待測系統對相依物件的調用，並提供getter方法取得調用的資訊。
- 跟Mock直接有驗證方法不一樣。Spy必須自行取回調用資訊，再執行驗證。


## Spy with Mockito

### 待測系統介紹

相同的IssueRepository介面，並有一個IssueRepositoryImp實作
```java
package com.rebuilding.day17.repository.imp;
import com.rebuilding.day17.entity.Issue;
import com.rebuilding.day17.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueRepositoryImp implements IssueRepository {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(IssueRepositoryImp.class);

    @Override
    public Issue save(Issue issue) {
        LOGGER.info("execute save, issue title: " + issue.getTitle());
        return issue;
    }
}
```


### 透過Mockito建立spy

由於Mockito的Spy不是無狀態的物件，所以我們必須在@Before setup方法建立spy，但是這裡傳入的不是一個類別，而是一個物件
```java
public class MockitoSpyTest {

    private IssueRepository repository;

    @Before
    public void setup() {
        /**
        * 注意到這裡所傳入的實際上是一個物件，假如沒有偽造方法的話，那將會執行真實物件的方法
        **/
        repository = spy(new IssueRepositoryImp());
    }
}
```

### 透過Mockito使用spy

我們可以透過Mockito提供的API輕易地驗證待測系統與Spy物件之間的互動。

1. 透過調用Mockito.verify()驗證待測系統與mock物件之間的互動，有以下兩種方法：
    verify(Spy物件).指定方法()：驗證指定方法調用過一次
    verify(Spy物件, 驗證條件).指定方法()：驗證指定方法調用的情形與條件相符
2. 透過調用Mockito.verifyNoMoreInteractions(Spy物件)，檢查Mock物件是否有經過verify()以外的方法被調用，如果有則失敗
3. 透過調用Mockito.verifyZeroInteractions(Spy物件)驗證待測系統與Mock物件之間沒有互動。

實際情境是把Spy物件(IssueRepository)注入到待測系統中，並執行待測系統方法之後，再進行待測系統與Spy物件互動的驗證。

不過以下範例僅依照IssueRepository介面，提供Mockito API的使用範例：
```java
package com.rebuilding.day17;

import com.rebuilding.day17.entity.Issue;
import com.rebuilding.day17.repository.IssueRepository;
import com.rebuilding.day17.repository.imp.IssueRepositoryImp;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

public class MockitoSpyTest {

    private IssueRepository repository;

    @Before
    public void setup() {
        repository = spy(new IssueRepositoryImp());
    }

    @Test
    public void testExecuteTimes() {
        /**
         * arrange
         * 建立欲存入的Issue物件
         */
        Issue expected = new Issue();

        /**
         * act
         * 執行方法，此時會真的執行物件IssueRepositoryImp的方法save()
         */
        Issue saved = repository.save(expected);

        /**
         * assert
         * 驗證執行次數
         */
        verify(repository, times(1)).save(saved);
    }

    @Test
    public void testReturnObject() {
        /**
         * arrange
         * 建立欲存入的Issue物件
         * 偽造部分方法，但其他方法不會被影響，依然會執行真正物件的方法
         * 這裡不用given(repository.save(any())).willReturn(expected)順序的寫法，主要是因為當我們無法得知待測系統內部，並且想要驗證某些事情避免出現異常而使用
         */
        Issue expected = new Issue();
        willReturn(expected).given(repository).save(any());

        /**
         * act
         * 執行方法，此時將不會執行真正的物件方法
         */
        Issue saved2 = repository.save(expected);

        /**
         * 驗證回傳物件是否相同
         */
        assertThat(saved2).isSameAs(expected);
    }
}
```

可以看到Spy與Mock非常相似，我們可以透過下列幾點來判斷使用時機：
- Mock會用來驗證物件行為，而Spy會用來驗證物件狀態。
- Mock的作為完全的替身，必須偽造每一個想要驗證的方法。
- Spy可以做到部分偽造，那些沒有偽造的部分則會呼叫真實的物件方法。

在Mockito中使用spy
- 可以使用與Mock一樣的語法來驗證待測系統與Spy之間發生的互動。
- 如果我們要驗證內部結構不明確的物件，則必須透過相反順序的寫法來避免發生異常。


## Dummy簡單講

Dummy是一個相當簡單的Test Double，需要Dummy的兩個常見原因：
- 程式碼編譯不過
- 測試會拋出異常無法通過，因為目標物件裡缺少了必要值

也就是說Dummy物件其實跟當前測試案例無關，只是單純需要它讓程式可以編譯通過，不要拋出異常而已。

建立的方式大概有下列四種
1. 替換的如果是primitive type屬性，則使用一個明確命名的常數且賦予一個奇怪的數值，讓讀者了解這不是一個需要關注的重點。
    `private static final long NOT_IMPORTANT=-99L`
2. 使用new建立一個Dummy物件，但同樣給予一個明確的命名指出此物件為Dummy。
    `Issue notImportant = new Issue()`
3. 使用工廠方法來建立Dummy物件，同時工廠方法名稱必須明確指出建立的物件為Dummy。
    `Issue notImportant = IssueFactory.dummyIssue();`
4. 使用Mockito建立Dummy物件，但由於在Mockito中不區分Stub, Mock, Dummy，通過方法mock()建立的物件都是一個mock，所以：
    1. 為了強調所建立的物件是Dummy，可以在之前建立的TestDoubles類別中加入一個新方法，這樣在使用時我們就可以盡量避免誤解：
    ```java
    public class TestDouble {
        public static <T> T dummy(Class<T> dummyClass){
            return mock(dummyClass);
        }
    }
    ```
    2. 現在透過TestDoubles明確指出使用方法dummy()建立出來的物件為一個Dummy，但在這之後注意絕對不要使用方法given()、verify()等來對它做一些設定，這只會讓閱讀及維護帶來混亂。

關於Test Double測試替身的主題就到此結束，希望各位也可以區分他們之間的差別了，並且能夠透過手工或工具來建立測試替身，讓待測系統與相依物件隔離，達到良好的可測試性。

下一篇，介紹JUnit的@Category還有Test Runner。