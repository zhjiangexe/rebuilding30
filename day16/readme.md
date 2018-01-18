# Test Doubles: Fake


## Fake堅持手工

Fake是一個用來代替待測系統的外部相依物件
- Fake必須提供與被替換的相依介面或類別一樣的API。
- Fake提供一個行為就像真的一樣的物件，但不應該再production環境使用。

### 待測系統介紹

IssueRepository介面，帶有兩個方法
```java
public interface IssueRepository {

    Issue findById(Long id);

    Issue save(Issue issue);
}
```

### 建立Fake堅持手工

有了外部相依的介面之後，我們可以根據此介面來建立一個Fake。

以下為建立完成的IssueRepositoryFake，為了有步驟的建立相關屬性及方法，這裡的程式碼將不依照慣例的格式顯示：
```java
package com.rebuilding.day16.repository;

import com.rebuilding.day16.entity.Issue;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositoryFake，並將他設定為final避免被繼承
 */
final public class IssueRepositoryFake implements IssueRepository {

    /**
     * 2. 加入一個private long nextId屬性，該屬性會儲存
     */
    private long nextId;

    /**
     * 3. 加入一個Map<Long, Issue>屬性，它將會用來儲存Issue物件
     */
    private Map<Long, Issue> issueMap;

    /**
     * 4. 透過建構函式初始化屬性
     */
    public IssueRepositoryFake() {
        this.nextId = 1L;
        this.issueMap = new HashMap<>();
    }

    /**
     * 5. 實作介面的方法findById(Long id)，它將會從issueMap取得物件
     */
    @Override
    public Issue findById(Long id) {
        return issueMap.get(id);
    }

    /**
     * 6. 實作介面的方法save(Issue issue)，它將會
     *      1. 分配nextId給準備儲存的Issue物件
     *      2. 將Issue放到Map
     *      3. 將nextId屬性值加1
     *      4. 回傳Issue物件
     */
    @Override
    public Issue save(Issue issue) {
        long id = nextId;
        issue.setId(id);

        issueMap.put(id, issue);
        nextId++;

        return issue;
    }
}
```

### 使用Fake堅持手工

以下測試案例，展示如何使用手工的Fake物件，由於它會像真實的物件一樣運作，所以我們不會在每次測試案例前建立新的Fake物件
```java
package com.rebuilding.day16.repository;

import com.rebuilding.day16.entity.Issue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueRepositoryFakeTest {

    private static final Long ID = 1L;
    private static final String TITLE = "ISSUE";

    /**
     * 這裡不再透過方法@Before setup()建立新的IssueRepositoryFake物件
     */
    private IssueRepository repository = new IssueRepositoryFake();

    @Before
    public void setup() {
    }

    /**
     * 可以看到Issue的建立與保存，不需任何的配置，就如同原本真的物件
     */
    @Test
    public void saveAndFindIssue() {
        Issue issue = new Issue();
        issue.setTitle(TITLE);

        Issue saved = repository.save(issue);

        assertThat(saved.getId()).isEqualTo(ID);
        assertThat(saved.getTitle()).isEqualTo(TITLE);

        Issue found = repository.findById(saved.getId());

        assertThat(found.getId()).isEqualTo(ID);
        assertThat(found.getTitle()).isEqualTo(TITLE);
    }
}

```

從這個例子可以知道Fake這個Test Double：
- 在測試案例中無法知道他是使用Fake物件，因為Fake物件就像真的一樣，不需任何配置。
- 而且我們不會在production code使用Fake物件。

比較Fake，Stub，Mock：
- 當想要配置替換的相依物件的行為時，使用Stub。
- 當想要配置替換的相依物件的行為，並且驗證待測系統與模擬物件之間的互動時，則使用Mock。
- 當不想要配置替換的相依物件的行為，且不想要驗證驗證待測系統與模擬物件之間的互動；只用一個與真實物件一樣的Test Double替換時，則使用Fake。

另外：
- Fake通常會是一個輕量級的實作，會比真正的相依快速。
- Fake比真正的相依更容易配置。或者不需要配置，在編寫整合和E2E測試時，會非常重要。

相似的情況，在整合測試中，當我們希望知道整個系統是否能按預期工作，可以透過簡單地做一個Fake物件，只要有能預期的反應即可，比如可能用H2之類的記憶體資料庫來替換真正的資料庫，或者使用假的API替換外部的API。

以上，下一篇的Test Double是Dummy。