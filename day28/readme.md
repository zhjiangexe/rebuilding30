# 整合測試《五》- 讀

最後兩篇，將結合前面所學，為幾個案例編寫整合測試來做個結尾，開始吧！

## 待測系統介紹

這邊以IssueController的兩個方法做為案例，請看以下程式碼及說明：
```java
package com.rebuilding.day.controller;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    /**
    * 案例一、取得指定ID的Issue，GET: /api/issue/{id}
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Issue query(@PathVariable Long id) {
        return issueService.queryById(id);
    }

    /**
    * 案例二、查詢Issue，GET: /api/issue/search?keyword=?
    */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Issue> search(@RequestParam("keyword") String keyword) {
        List<Issue> list = issueService.search(keyword);
        return list;
    }
}
```

並列出相依的Service
```java
package com.rebuilding.day.service.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueServiceImp implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue queryById(Long id) {
        return issueRepository.findOne(id);
    }

    @Override
    public Issue update(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public List<Issue> search(String keyword) {
        return issueRepository.search(keyword);
    }
}
```

以及Repository，這邊以Spring Data Jpa進行實作，繼承JpaRepository會隱含實作方法findOne()及save()等，並且自行額外實作方法search()來查詢title及desc欄位
```java
package com.rebuilding.day.repository;

import com.rebuilding.day.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT i FROM Issue i " +
            "WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(i.desc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Issue> search(@Param("keyword") String keyword);
}
```

並且使用DbUnit DataSet準備測試資料
issue.xml
```xml
<dataset>
    <issue id="1"
           title="BIG"
           desc="big issue desc"
           state="TODO"
           creator_num="100"
           assignee_num="[null]"
           closer_num="[null]"
           result="[null]"
    />
    <issue id="2"
           title="SMALL"
           desc="small issue easy "
           state="IN_PROGRESS"
           creator_num="100"
           assignee_num="101"
           closer_num="[null]"
           result="[null]"
    />
</dataset>
```

以上列出待測系統中Controller、Service、Repository及測試資料，其他配置則先省略不列出，之後會把程式碼放上來。

----

## 案例一、取得指定ID的Issue

1. 驗證回傳http status code為200
2. 驗證回傳正確的資訊

請參考以下程式碼及說明：
```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.rebuilding.day.config.DemoContextConfig;
import com.rebuilding.day.integrationTest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {DemoContextConfig.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("/com/rebuilding/day/testcase/issues.xml")
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
@ActiveProfiles("integrationTest")
public class QueryIssueWhenIssueIsFoundTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    /**
     * 載入配置的WebApplicationContext
     */
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 加入一個回傳ResultActions的私有方法來簡化程式碼
     */
    private ResultActions queryIssue() throws Exception {
        return mockMvc.perform(get("/api/issue/{id}", QueryIssue.Data.ISSUE_ID));
    }

    /**
     * 1. 驗證回傳http status code為200
     */
    @Test
    public void thenReturnHttpStatusCodeOk() throws Exception {
        queryIssue()
                .andExpect(status().isOk());
    }

    /**
     * 2. 驗證回傳正確的資訊
     */
    @Test
    public void thenReturnInfo() throws Exception {
        queryIssue()
                .andExpect(jsonPath(QueryIssue.Property.ID, is(QueryIssue.Data.ISSUE_ID.intValue())))
                .andExpect(jsonPath(QueryIssue.Property.TITLE, is(QueryIssue.Data.TITLE)))
                .andExpect(jsonPath(QueryIssue.Property.DESC, is(QueryIssue.Data.DESC)))
                .andExpect(jsonPath(QueryIssue.Property.STATE, is(QueryIssue.Data.STATE)))
                .andExpect(jsonPath(QueryIssue.Property.CREATOR_NUM, is(QueryIssue.Data.CREATOR_NUM.intValue())))
                .andExpect(jsonPath(QueryIssue.Property.ASSIGNEE_NUM, is(QueryIssue.Data.ASSIGNEE_NUM)))
                .andExpect(jsonPath(QueryIssue.Property.CLOSER_NUM, is(QueryIssue.Data.CLOSER_NUM)))
                .andExpect(jsonPath(QueryIssue.Property.RESULT, is(QueryIssue.Data.RESULT)));
    }

}

/**
 * 這裡是一種手法，在建立測試資料之後要驗證時，如果直接在測試方法中編寫驗證的資料，可能會造成資料散亂；
 * 我們可以透過建立常數，讓其他測試方法來引用提高可維護性，並且如果有多個測試類別有相同的資料，可以獨立成一個類別提供引用。
 */
final class QueryIssue {
    static class Property {
        static final String ID = "$.id";
        static final String TITLE = "$.title";
        static final String DESC = "$.desc";
        static final String STATE = "$.state";
        static final String CREATOR_NUM = "$.creatorNum";
        static final String ASSIGNEE_NUM = "$.assigneeNum";
        static final String CLOSER_NUM = "$.closerNum";
        static final String RESULT = "$.result";
    }

    static class Data {
        static final Long ISSUE_ID = 1L;
        static final String TITLE = "BIG";
        static final String DESC = "big issue desc 1";
        static final String STATE = "TODO";
        static final Long CREATOR_NUM = 100L;
        static final String ASSIGNEE_NUM = null;
        static final String CLOSER_NUM = null;
        static final String RESULT = null;
    }
}
```

---
## 案例二、查詢Issue

1. 驗證回傳http status code為200
2. 驗證回傳資料筆數
3. 驗證回傳正確的資訊

```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.rebuilding.day.config.DemoContextConfig;
import com.rebuilding.day.integrationTest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {DemoContextConfig.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("/com/rebuilding/day/testcase/issues.xml")
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
@ActiveProfiles("integrationTest")
public class SearchIssueWhenIssueIsFoundTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private ResultActions searchIssue() throws Exception {
        return mockMvc.perform(
                get("/api/issue/search")
                .param("keyword", Search.SEARCH_KEYWORD)
        );
    }

    /**
     * 1. 驗證回傳http status code為200
     */
    @Test
    public void thenReturnHttpStatusCodeOk() throws Exception {
        searchIssue()
                .andExpect(status().isOk());
    }

    /**
     * 2. 驗證回傳資料筆數
     */
    @Test
    public void thenReturnOneTask() throws Exception {
        searchIssue()
                .andExpect(jsonPath("$", hasSize(1)));
    }

    /**
     * 3. 驗證回傳正確的資訊
     */
    @Test
    public void thenReturnInfo() throws Exception {
        searchIssue()
                .andExpect(jsonPath("$[0].id", is(Search.ID.intValue())))
                .andExpect(jsonPath("$[0].title", is(Search.TITLE)))
                .andExpect(jsonPath("$[0].state", is(Search.STATE)));
    }
}

final class Search {
    static final String SEARCH_KEYWORD = "des";
    static final Long ID = 1L;
    static final String TITLE = "BIG";
    static final String STATE = "TODO";
}
```

以上，本篇兩個測試案例對於讀取的部分進行了基本驗證，下一篇將會介紹當更新資料的時候會遇到的案例。