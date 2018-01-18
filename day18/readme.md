# JUnit Category & Test Runner

當我們使用gradle執行測試，在一般未做設定的情況，gradle會執行預設的配置：
- 執行路徑src/test/java底下的測試類別
- 如果有測試需要的配置文件properties等，則會放在src/test/resource
- 並且測試類別必須是滿足下列條件之一的類別：
    - 繼承類別TestCase
    - 具有@RunWith註解
    - 帶有被@Test註解的方法

以下將要說明Category以及Test Runner測試配置。

## Category

如果我們只編寫單元測試，則不需要對`build.gradle`做任何修改，就會執行前面提到的預設行為，但實際上也必須編寫整合測試(Integration Test)及端到端測試(End-To-End Test)。

而且當遇到部分整合測試很慢的情況，我們不希望每次都會執行它，此時就需要透過Category來輔助我們進行分類執行。

1. 首先，建立一個用來標記分類的介面
```java
public interface UnitTest {
}
```

2. 接下來，在想要設定的測試類別上進行標記
```java
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CategoryTest {

    @Test
    public void categoryTest() {
        System.out.println("The category: UnitTest");
    }
}
```

3. 設定build.gradle，透過設定屬性includeCategories，gradle只會執行被UnitTest標記的類別
```java
test {
    useJUnit {
        includeCategories 'com.rebuilding.day18.categories.UnitTest'
    }
}
```

接下來執行測試`gradle test`，就可以發現到僅執行了有被`@Category(UnitTest.class)`標記的類別。

## Test Runner

Test Runner是：
- 一個會自動執行測試案例並產出報表的元件。
- 每個類別只能有一個Test Runner，也就是說一個類別上最多只能有一個@RunWith。
- 可以透過@RunWith來指定Test Runner，4.5版之後的JUnit預設是使用BlockJUnit4ClassRunner。
- 所有的Test Runner都必須繼承抽象類別org.junit.runner.Runner

想要自行指定Test Runner，只要在測試類別上加上`@RunWith`及帶入繼承*org.junit.runner.Runner*的類別就可以了。
```java
@RunWith(HierarchicalContextRunner.class)
public IssueServiceTest {
    @Test
    public void testMethod(){//...}
}
```

下一篇接續要介紹的是如何透過Hierarchical Context Runner來編寫巢狀的單元測試。