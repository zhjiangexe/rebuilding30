# Test Doubles: Stub 使用Mockito

Mockito是java界流行的king framework，可用於建立各種test doubles，如Stub，Mock，Fake，Spy。

開始之前，我們要先取得套件：
build.gradle
```java
dependencies {
    testCompile group: 'org.mockito', name: 'mockito-core', version: '2.13.0'
}
```

關於Mockito API
- org.mockito.Mockito類別提供原始的Mockito API。此API使用When-Then語法。
- org.mockito.BDDMockito類別提供了支持BDD風格測試，使用Given-When-Then語法的新API，接下來我們會使用官方推薦的這種方式。

使用Mockito要注意的事情：
- Test Double並不是無狀態的，必須在執行每個測試案例之前建立一個全新的Test Double。建議在@Before註解的setup方法中執行。
- Mockito中使用方法mock()來建立Stub、Mock、Dummy；使用方法spy()來建立Spy
- 不要在Test Double中建立Test Double。

由於Mockito裡不區分dummy, stub, mock，只要透過方法mock()建立的Test Double都是一個Mock

為了讓容易辨識，我們將會建立一個工具類別TestDoubles，加入工廠方法來強調建立的是哪一種Mock
```java
package com.rebuilding.day14.repository;

import static org.mockito.Mockito.mock;

/**
 * 1. 建立名為TestDoubles的工具類別
 **/
public class TestDoubles {
    /**
     * 2. 加入private建構函式，避免開發人員直接建立此物件
     */
    private TestDoubles() {
    }

    /**
     * 3. 加入一個回傳mock的靜態方法，方法名稱是為強調所建立的Mock將會當成Stub使用
     */
    public static <T> T stub(Class<T> stubClass) {
        return mock(stubClass);
    }

}
```

## 待測系統介紹

在待測系統中有一個相依介面IssueRepository，其中宣告了4個方法

```java
public interface IssueRepository {
    /**
     * 1. 回傳從資料庫中找到的Issue數量
     */
    long count();

    /**
     * 2. 刪除參數中的Issue，如果資料庫中找不到則發生Exception
     */
    void delete(Issue issue);

    /**
     * 3. 透過參數ID找到指定的Issue，找不到則回傳null
     */
    Issue findById(Long id);

    /**
     * 4. 傳入任務儲存，並回傳Issue物件
     */
    Issue save(Issue issue);
}
```

接下來將會介紹如何透過Mockito來偽造(Stub)這些方法的回傳

## 建立Stub

首先，建立Stub，因為使用Mocktio建立的Test Doubles不是無狀態的物件，所以建議在@Before setup方法中建立Stub，這樣每一個測試案例使用到的Stub都會是全新的。

```java
import static com.rebuilding.day14.repository.TestDoubles.stub;

public class BDDMockitoTest {
    private IssueRepository repository;

    @Before
    public void setup() {
        /**
        * 使用TestDoubles工具類別提供的方法來建立stub
        **/
        repository = stub(IssueRepository.class);
    }
}
```

## Stubbing Method Behavior(偽造方法行為)

實際情境是把Stub物件(IssueRepository)注入到待測系統中，並對各測試案例的需要，進行Stub物件方法的偽造，提供待測系統有一個可預期回傳值的外部相依。

不過以下範例僅依照IssueRepository，提供Mockito API的使用範例：
```java
package com.rebuilding.day14.repository;

import com.rebuilding.day14.entity.Issue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.rebuilding.day14.repository.TestDoubles.stub;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

public class BDDMockitoTest {
    private IssueRepository repository;

    @Before
    public void setup() {
        repository = stub(IssueRepository.class);
    }

    /**
     * 案例1：無方法參數，有回傳值或物件
     */
    @Test
    public void testCount() {
        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法).willReturn(指定的回傳值或物件)
         */
        given(repository.count()).willReturn(1L);

        /**
         * actualCount此時就會取得指定的回傳值1L
         */
        long actualCount = repository.count();

        assertThat(actualCount).isEqualByComparingTo(1L);
    }

    /**
     * 案例2：有方法參數，有回傳值或物件
     */
    @Test
    public void testFindById1() {

        Issue expected = new Issue();
        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法(指定參數)).willReturn(指定的回傳值或物件)
         */
        given(repository.findById(1L)).willReturn(expected);

        /**
         * actual此時就會取得指定的回傳Issue
         */
        Issue actual = repository.findById(1L);

        /**
         * 如果傳入非指定的參數，則會取得2L
         */
        // Issue actual = repository.findById(2L);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * 案例3：有方法參數，有回傳值或物件，使用ArgumentMatchers
     */
    @Test
    public void testFindById2() {

        Issue expected = new Issue();

        given(repository.findById(eq(1L))).willReturn(expected);

        Issue actual = repository.findById(1L);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * 案例4：有方法參數，有回傳值或物件，使用ArgumentMatchers anyLong
     */
    @Test
    public void testFindById3() {

        Issue expected = new Issue();

        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法(anyLong())).willReturn(指定的回傳值或物件)
         */
        given(repository.findById(anyLong())).willReturn(expected);

        /**
         * 任意給個型態為Long的參數，都可取得指定的回傳值
         */
        Issue actual = repository.findById(99L);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * 案例4：有方法參數，有回傳值或物件，拋出異常
     */
    @Test(expected = Exception.class)
    public void testFindById4() {

        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法).willThrow(指定的Exception)
         */
        given(repository.findById(1L)).willThrow(new Exception());

        /**
         * 調用此方法會發生異常
         * 透過@Test屬性excepted捕捉期望異常
         */
        repository.findById(1L);
    }

    /**
     * 案例5：無回傳值或物件，拋出異常
     */
    @Test(expected = Exception.class)
    public void testDelete() {

        Issue deleted = new Issue();

        /**
         * 調用BDDMockito類別的方法，沒有回傳值，跟前面不一樣是以given()開頭
         * willThrow(指定的Exception).given(Stub).執行方法
         */
        willThrow(new Exception()).given(repository).delete(deleted);

        /**
         * 調用此方法會發生異常
         * 透過@Test屬性excepted捕捉期望異常
         */
        repository.delete(deleted);
    }

    /**
     * 案例6：如果具有連Argument Matcher都無法解決的情境，則實作Answer介面來解決
     */
    @Test
    public void testFindById5() {

        Issue expected = new Issue();

        /**
         * 調用BDDMockito類別的方法，沒有回傳值，跟前面不一樣是以given()開頭
         * willThrow(想要偽造的方法).willAnswer(複雜情境實作Answer介面)
         */
        given(repository.findById(1L)).willAnswer(new Answer<Issue>() {
            @Override
            public Issue answer(InvocationOnMock invocation) {
                Long idParameter = (Long) invocation.getArguments()[0];
                if (idParameter.equals(1L)) {
                    return expected;
                } else {
                    return null;
                }
            }
        });

        Issue actual = repository.findById(1L);

        assertThat(actual).isSameAs(expected);
    }

}
```

Mockito透過簡單的語法，讓我們很容易的建立Stub，並且偽造方法的行為，回傳指定值、物件，或異常，解決了相依問題。

下一篇，談談Mock