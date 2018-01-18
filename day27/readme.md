# 整合測試《四》使用 DbUnit DataSet

測試過程使用DbUnit並非不使用資料庫，而是透過DataSet.xml載入資料的方式，來進行資料的初始化及還原，使其達到可重複測試特性。為此我們也要遵守幾個規則：

- 盡量使用小小的DataSets來驗證一個邏輯概念。
- 將DataSet.xml與測試類別放在同一個package。
- DataSet命名規則：測試案例名稱-期望結果-expected.xml
- DataSet讀取位置規則：
    - 當DataSets.xml與測試類別在同一package時，則直接使用檔案名稱指定位置即可。
    - 當DataSets.xml與測試類別在不同package時，則使用完整的路徑指定位置。

# 初始化資料庫，使用DbUnit配置DataSet將測試資料輸入到資料庫中，有兩種方式：

1. 在測試類別上註解@DatabaseSetup：如果所有的測試方法都有相同的DataSet則使用這種方式
```java
@DatabaseSetup("/com/rebuilding/integrationTest/issue.xml")
public class IssueIntegrationTets {
    @Test
    public void then1() {
    }
    @Test
    public void then2() {
    }
}
```

2. 在測試方法上註解@DatabaseSetup：如果每個測試方法都有不同的DataSet則使用這種方式
```java
public class IssueIntegrationTets {
    @Test
    @DatabaseSetup("issue1.xml")
    public void then1() {
    }

    @Test
    @DatabaseSetup("issue2.xml")
    public void then2() {
    }
}
```

還可以使用多個DataSet，DbUnit會依照指定的順序執行，下列會以1. XXX, 2. AAA的順序執行：
```java
@DatabaseSetup({
    "XXX.xml",
    "AAA.xml"
})
```

另外，在沒有指定DatabaseOperation時，預設為CLEAN_INSERT，也就是先清除指定的資料表並將設定的DataSet測試資料輸入到資料庫中。一般測試案例使用預設行為即可，但有需要的話，可以透過指定type來完成，以下使用UPDATE：
```java
@DatabaseSetup(value = "fake.xml", type = DatabaseOperation.UPDATE)
```

# 為資料庫中的測試資料編寫斷言

DbUnit不僅可以輸入資料，並且可以使用@ExpectedDatabase來指定期望的資料集合。
但我們不使用預設(DEFAULT)驗證所有欄位的斷言模式(assertionMode)，通常是使用NON_STRICT僅只驗證指定欄位。如下：
```java

public class IssueIntegrationTets {
    @Test
    @ExpectedDatabase(value="thenGetExpectedIssue1.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void then1() {
    }

    @Test
    @ExpectedDatabase(value="thenGetExpectedIssue2.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void then2() {
    }
}
```

