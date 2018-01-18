為拋出的異常編寫斷言
==========

除了一般情況的驗證，今天還要來處理關於Exception的驗證斷言。

## 待測系統介紹

IssueService類別，帶有方法queryById(Long id)，且這個方法會拋出EntityNotFoundException：

```java
public class IssueService {

    public Issue queryById(Long id) {
        throw new EntityNotFoundException(id);
    }
}
```
EntityNotFoundException類別，繼承RuntimeException，帶有建構子

```java
public class EntityNotFoundException {
    private Long id;
    EntityNotFoundException (Long id) {
        super(String.format("ID: %d找不到實體", id));
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}
```

接下來，為queryById編寫斷言進行驗證。

## 使用@Test為方法queryById()編寫斷言

透過配置@Test(expected=異常類別.class)進行簡單的驗證

1. 準備目標物件：
    1. private IssueService issueService;
    2. 透過`@Before` setup方法產生IssueService物件
2. 測試案例調用目標物件的方法`issueService.queryById(ID)`
3. 如果要驗證只拋出一個預期的異常，可以透過配置@Test(expected=異常類別.class)來達成

```java
package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Test;

public class IssueServiceTest2 {
    private static final long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup () {
        issueService = new IssueService();
    }

    @Test(expected = EntityNotFoundException.class)
    public void queryById_shouldThrowException () {
        issueService.queryById(ID);
    }
}

```

但這算是個簡單的驗證，如果我們要做更多的驗證，還需要透過其他方式。

## 使用AssertJ為方法queryById()編寫斷言

注意：此方式系統必須為java8

AssertJ提供了org.assertj.core.api.Assertions.catchThrowable()方法來捕捉異常，執行步驟如下

1. 準備目標物件

2. 將準備執行的目標物件方法queryById，做成lambda表達式並當作方法參數，傳遞給catchThrowable()，它將傳回EntityNotFoundException，如果沒有拋出異常則傳回null

3. 編寫斷言來驗證拋出的異常isExactlyInstanceOf()，驗證其異常訊息hasMessageContaining()

```java
package com.rebuilding.day6;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class IssueServiceTest {
    private static final long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup () {
        issueService = new IssueService();
    }

    @Test
    public void queryById_shouldThrowException () {
        Throwable throwable = catchThrowable(() -> issueService.queryById(ID));
        assertThat(throwable)
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("找不到實體");
    }
}

```

## 使用ExpectedException Rule為方法queryById()編寫斷言

ExpectedException Rule為我們提供了捕獲被測系統拋出的異常的可能性，並通過使用Hamcrest匹配器來編寫捕獲異常的斷言。

如果我們想使用ExpectedException Rule來進行驗證，我們必須通過以下步驟進行準備：

1. 準備目標物件
2. 準備Rule(下一篇將會談到Rule)
    1. 加入`public ExpectedException thrown`屬性到我們測試類別，並通過調用ExpectedException類別的靜態方法none()來初始化。
    2. 使用@Rule標註
```java
package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IssueServiceTest3 {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup() {
        issueService = new IssueService();
    }
}

```

準備好Rule之後，如果我們想驗證queryById()方法拋出一個異常，依照以下步驟執行

1. 調用ExpectedException類別的expect()方法，且將異常類別作為一個方法參數傳遞，作為期望的異常驗證
2. 調用issueService.queryById(ID)

```java
package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IssueServiceTest3 {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup() {
        issueService = new IssueService();
    }

    @Test
    public void queryById_ShouldThrowException() {
        thrown.expect(EntityNotFoundException.class);
        issueService.queryById(ID);
    }
}

```


第二，如果我們想驗證異常物件是否包含正確的ID，依照以下步驟執行

> 以下會使用到hamcrest-library套件庫來幫忙斷言(之後會提到)，請先引入套件庫
> testCompile group: 'org.hamcrest', name: 'hamcrest-library', version: '1.3'

1. 調用ExpectedException類別的expect()方法，並把Hamcrest matcher - hasProperty("id", is(ID))當做方法參數傳遞，它將驗證所拋出的異常物件是否包含正確的ID
2. 調用issueService.queryById(ID)


```
package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class IssueServiceTest4 {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup() {
        issueService = new IssueService();
    }

    @Test
    public void queryById_ShouldThrowException() {
        thrown.expect(hasProperty("id", is(ID)));
        issueService.queryById(ID);
    }
}

```

本篇另外談到了Rule及Hamcrest，將在下一篇說明。