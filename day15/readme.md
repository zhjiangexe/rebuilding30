# Test Doubles: Mock

## Mock堅持手工

Mock是一個用來代替待測系統的外部相依物件
- Mock必須提供與被替換的相依介面或類別一樣的API。
- 可以制定調用Mock的方法回傳的值、物件、或錯誤。
- 而每一次調用Mock方法時，都會依照制定回傳。
- Mock必須提供方法來驗證待測系統與Mock之間的互動

### 待測系統介紹

IssueRepository介面，待有刪除方法Issue deleteById(Long id)
```java
public interface IssueRepository {
    Issue deleteById(Long id);
}
```

### 建立mock堅持手工

有了外部相依的介面之後，我們可以根據此介面來建立一個Mock。

以下為建立完成的IssueRepositoryMock，為了有步驟的建立相關屬性及方法，這裡的程式碼將不依照慣例的格式顯示：
```java
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
     *      1. 儲存方法參數至屬性actualIdArgument
     *      2. 變更屬性deleteByIdCalled為true
     *      3. 回傳配置的issue物件
     */
    @Override
    public Issue deleteById(Long id) {
        actualArgumentId = id;
        deleteByIdCalled = true;
        return returnIssue;
    }

    /**
     * 9. 實作方法verify()，調用此方法時
     *      1. 驗證deleteByIdCalled為true，意思是驗證deleteById()曾被調用過
     *      2. 驗證actualIdArgument相等於expectedIdArgument
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
}
```

### 使用mock堅持手工

以下兩個測試案例，展示如何使用手工的Mock物件

```java
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
```

從這個例子可以知道Mock這個Test Double：
- 可以定制調用方法所回傳的值
- 每次調用會回傳相同的值
- 提供方法驗證待測系統與Mock物件之間發生的互動
- 可配置調用Mock物件方法時應作為方法參數傳遞的期望值或物件。

如果外部相依不僅只是個資料來源，還具有Side Effect的操作(副作用：函數副作用指當調用函數時，除了回傳函數值之外，還對主調用函數產生附加的影響)，
如儲存資料到資料庫、向外部發送Http Request等，則應該用Mock來替換。


---

## Mock使用Mockito

### 待測系統介紹

IssueRepository介面，帶有查詢方法Issue findById(Long id)
```java
public interface IssueRepository {
    Issue findById(Long id);
}
```

### 建立mock使用Mockito

與Stub相同，由於Mockito的Mock不是無狀態的物件，所以我們必須在@Before setup方法建立mock
```java
public class IssueServiceTest2 {
    private IssueRepository repository;

    @Before
    public void setup() {
        repository = mock(IssueRepository.class);
    }
}
```

### 使用mock使用Mockito

我們可以透過Mockito提供的API輕易地驗證待測系統與Mock物件之間的互動。

1. 透過調用Mockito.verify()驗證待測系統與mock物件之間的互動，有以下兩種方法：
    verify(Mock物件).指定方法()：驗證指定方法調用過一次
    verify(Mock物件, 驗證條件).指定方法()：驗證指定方法調用的情形與條件相符
2. 透過調用Mockito.verifyNoMoreInteractions(Mock物件)，檢查Mock物件是否有經過verify()以外的方法被調用，如果有則失敗
3. 透過調用Mockito.verifyZeroInteractions(Mock物件)驗證待測系統與Mock物件之間沒有互動。

實際情境是把Mock物件(IssueRepository)注入到待測系統中，並執行待測系統方法之後，再進行待測系統與Mock物件互動的驗證。

不過以下範例僅依照IssueRepository，提供Mockito API的使用範例：
```java
package com.rebuilding.day15.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class IssueServiceTest2 {
    private IssueRepository repository;

    @Before
    public void setup() {
        repository = mock(IssueRepository.class);
    }

    /**
     * 驗證待測系統調用了1次Mock物件的指定方法
     */
    @Test
    public void verifyWhenMethodWasCalledOnce() {

        repository.findById(1L);

        verify(repository).findById(1L);
    }

    /**
     * 驗證待測系統調用了2次Mock物件的指定方法
     */
    @Test
    public void verifyThatMethodWasCalledOnce() {

        repository.findById(1L);
        repository.findById(1L);

        verify(repository, times(2)).findById(1L);
    }

    /**
     * 驗證待測系統從未調用Mock物件的指定方法
     */
    @Test
    public void verifyThatMethodWasNotInvoked() {

        verify(repository, never()).findById(1L);
    }

    /**
     * 驗證待測系統調用了2次Mock物件的指定方法，除此之外沒有任何互動
     */
    @Test
    public void verifyThatNoOtherMethodsWereInvoked() {

        repository.findById(1L);
        repository.findById(1L);

        verify(repository, times(2)).findById(1L);

        verifyNoMoreInteractions(repository);
    }

    /**
     * 驗證待測系統與Mock物件沒有任何互動，換句話說，驗證待測系統從未調用Mock物件的任何方法
     */
    @Test
    public void verifyThatNoInteractionsHappenedBetweenSUTAndMock() {

        verifyZeroInteractions(repository);
    }

    /**
     * 驗證待測系統調用了1次Mock物件的指定方法，且透過調用ArgumentMatchers.eq()指定方法參數為1L
     */
    @Test
    public void verifyThatMethodWasInvokedByPassing1LAsMethodParameter() {

        repository.findById(1L);

        verify(repository).findById(eq(1L));
    }

    /**
     * 驗證待測系統調用了1次Mock物件的指定方法，且透過調用ArgumentMatchers.anyLong()指定方法參數為任意Long型態
     */
    @Test
    public void verifyThatMethodWasInvokedByPassingAnyLongAsMethodParameter() {

        repository.findById(3L);

        verify(repository).findById(anyLong());
    }

    /**
     * 如果想為傳入Mock物件指定方法的方法參數進行斷言，則必須透過ArgumentCaptor<T>來取得實際方法參數
     *
     * 驗證待測系統調用了1次Mock物件的指定方法，且由外部斷言方法參數為1L
     */
    @Test
    public void verifyThatMethodWasInvokedByUsingArgumentCaptor() {
        repository.findById(1L);

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);

        verify(repository).findById(argument.capture());

        Long actualId = argument.getValue();

        assertThat(actualId).isEqualTo(1L);
    }

    /**
     * 如果想為傳入Mock物件指定方法的方法參數進行斷言
     * 除了上述的方式以外，如果是使用java8的使用者，則可以引入額外的相依套件庫，進行簡化
     *
     * 驗證待測系統調用了1次Mock物件的指定方法，且由外部斷言方法參數為1L
     */
    @Test
    public void verifyThatMethodWasInvokedByUsingArgumentCaptorJava8() {
        repository.findById(1L);

        verify(repository).findById(
                assertArg(
                        argument -> assertThat(argument).isEqualTo(1L)
                )
        );
    }
}
```

> 最後一個案例示範使用java8 lambda來簡化測試，但請先引入額外的相依套件庫：
> `testCompile group: 'info.solidsoft.mockito', name: 'mockito-java8', version: '2.2.0'`

以上，下一篇的Test Double是Fake。