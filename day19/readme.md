# 使用 JUnit HierarchicalContextRunner編寫巢狀的單元測試

一般使用JUnit編寫單元測試最大的問題在於重複的程式碼，因為我們必須建立測試資料，或者配置待測系統所需的測試替身，雖然我們可以在setup方法中解決這個問題，但同時會產生以下問題：
1. setup方法會變得相當龐大，使人難以閱讀及維護。
2. 測試方法會變得很難閱讀及維護，因為無法在測試案例中直接看到測試資料，必須回頭查看setup方法。
3. 不同的測試案例可能會有不同的測試替身，使得測試方法其實會有不相容的配置。

假設待測系統有所變更，我們可能會為了修改所需的測試資料及測試替身花費相當多的時間，所以想解決上述問題，我們可以透過編寫巢狀的單元測試來解決。

## 編寫巢狀單元測試

在那之前，則需要先引入此套件庫：
```java
dependencies {
    testCompile group: 'de.bechte.junit', name: 'junit-hierarchicalcontextrunner', version: '4.12.1'
}
```

有了套件庫以後，只要使用`@RunWith(HierarchicalContextRunner.class)`註解測試類別，就可以開始編寫巢狀單元測試了。
```java
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class RebuildingTest {
}
```

接下來，該怎麼編寫巢狀的單元測試呢？

## 編寫巢狀單元測試

在這裡我們的巢狀寫法包含內部類別總共三層：
1. 第一層，用來配置待測類別及其相依物件，並透過@Before註解，在執行測試案例之前取得新建立的物件，確保測試案例不會互相影響。
2. 第二層，每一個待測方法建立一個內部類別，並以待測方法名稱當作內部類別命名。
3. 第三層，每一個情境再建立一層內部類別，並以測試情境When命名。

```java
import com.rebuilding.day19.entity.Issue;
import com.rebuilding.day19.service.IssueService;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class IssueServiceTest {
    private IssueService mockService;
    /**
     * 第一層配置待測類別
     */
    @BeforeClass
    public static void rootBeforeClass() {
        System.out.println("root before class");
    }

    @Before
    public void rootBefore() {
        System.out.println("root before");
        mockService = mock(IssueService.class);
    }

    @After
    public void rootAfter() {
        System.out.println("root after");
    }

    @AfterClass
    public static void rootAfterClass() {
        System.out.println("root after class");
    }

    public class FindById {
        /**
         * ↑第二層，每一個方法建立一個內部類別，並以待測方法名稱當作內部類別命名
         * ↓透過setup配置該方法所需的測試資料或測試替身等
         */
        public static final long ID = 1L;

        @Before
        public void findByIdBefore() {
            System.out.println("findById before");
        }

        @After
        public void findByIdAfter() {
            System.out.println("findById after");
        }

        public class WhenIssueFound {
            /**
             * ↑第三層，依測試案例作為第三層內部類別命名
             * ↓並配置該案例所需的測試替身或測試資料
             */
            @Before
            public void FindByIdWhenIssueFoundBefore() {
                System.out.println("findById when issue found before");
                given(mockService.findById(ID)).willReturn(new Issue());
            }

            @Test
            public void testCase(){
                System.out.println("find by id when issue found test case");
            }
        }

        public class WhenIssueNotFound {

            @Before
            public void FindByIdWhenIssueNotFoundBefore() {
                System.out.println("findById when issue found before");
                given(mockService.findById(ID)).willReturn(new Issue());
            }

            @Test
            public void testCase(){
                System.out.println("find by id when issue not found test case");
            }
        }
    }
}

```

只要把測試替身、常數等配置的共用及分離做好，譬如沒有共用的部分寫在內層，共用的部分寫在外層，就能幫助到測試程式的閱讀及維護。

巢狀單元測試的做法可以：

1. 透過建立不同的層次來分離測試案例的結構，讓案例有相關的物件及方法聚在一起，個別的測試案例setup可以在各自的內部類別裡進行，使得測試更容易閱讀及維護。
2. 並且只在個別測試案例建立所需的測試資料或測試替身，藉以刪除重複的程式碼。
3. 由於編輯器具有可折疊的功能(folding)，這種分層便於閱讀時可以專注在閱讀的測試案例。
4. 另外，當待測系統的方法有所變更時，我們也可以把測試的修改盡量限制在一個地方。

以上是編寫巢狀單元測試的部分，下一篇要來了解怎麼做到參數化測試。