# 整合測試《六》- 寫

## 待測系統介紹

這裡以IssueController的方法Update做為案例，請看以下程式碼：
```java
package com.rebuilding.day.controller;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Issue update(@PathVariable Long id,
                        @Valid @RequestBody Issue issue) {
        issue.setId(id);

        Issue updated = issueService.update(issue);

        return updated;
    }
}
```

## 案例三、更新Issue失敗

1. 驗證https status code為400，錯誤的請求，這裡是因為資料不正確所造成
2. 驗證找到一個錯誤訊息
3. 驗證找到錯誤訊息為title欄位不可為空
4. 驗證資料庫資料沒有變化

```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.rebuilding.day.config.DemoContextConfig;
import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.integrationTest.IntegrationTest;
import com.rebuilding.day.integrationTest.IntegrationTestContext;
import com.rebuilding.day.integrationTest.TestContextUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IntegrationTestContext.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("/com/rebuilding/day/testcase/issues.xml")
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
@ActiveProfiles("integrationTest")
public class UpdateIssueWhenValidationFails {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 模擬發送PUT: /api/issue/1
     * requestBody: { id: 1, title: "" }
     */
    private ResultActions updateIssueWithBlankTitle() throws Exception {
        Issue issue = new Issue();
        issue.setId(UpdateIssue.ID);
        issue.setTitle("");

        return mockMvc.perform(put("/api/issue/{id}", UpdateIssue.ID.intValue())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestContextUtil.convertObjectToJsonBytes(issue)));
    }

    /**
     * 1. 驗證https status code為400，錯誤的請求
     */
    @Test
    public void thenReturnHttpStatusCode400() throws Exception {
        updateIssueWithBlankTitle()
                .andExpect(status().isBadRequest());
    }

    /**
     * 2. 驗證找到一個錯誤訊息
     */
    @Test
    public void thenReturnOneError() throws Exception {
        updateIssueWithBlankTitle()
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }

    /**
     * 3. 驗證找到錯誤訊息為title欄位不可為空
     */
    @Test
    public void thenReturnErrorAsBlankTitle() throws Exception {
        updateIssueWithBlankTitle()
                .andExpect(jsonPath(
                        "$.fieldErrors[?(@.field == 'title')].errorCode",
                        contains("NotBlank")
                ));
    }

    /**
     * 4. 驗證資料庫資料沒有變化
     */
    @Test
    @ExpectedDatabase(value = "/com/rebuilding/day/testcase/issues.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void thenDoNotUpdateIssue() throws Exception {
        updateIssueWithBlankTitle();
    }
}

final class UpdateIssue {
    static final Long ID = 1L;
}
```

---

## 案例三、更新Issue成功

1. 驗證https status code為200
2. 驗證是否為預期資訊
3. 驗證資料庫的變化，title與desc有變化

```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.integrationTest.IntegrationTest;
import com.rebuilding.day.integrationTest.IntegrationTestContext;
import com.rebuilding.day.integrationTest.TestContextUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IntegrationTestContext.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("/com/rebuilding/day/testcase/issues.xml")
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
@ActiveProfiles("integrationTest")
public class UpdateIssueWhenValidationOk {

    private static final String UPDATE_TITLE = "update title";
    private static final String UPDATE_DESC = "update desc";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 模擬發送PUT: /api/issue/1
     * requestBody: { id: 1, title: "update title", desc: "update desc" }
     */
    private ResultActions updateIssueWithBlankTitle() throws Exception {
        Issue issue = new Issue();
        issue.setId(UpdateIssue.ID);
        issue.setTitle(UPDATE_TITLE);
        issue.setDesc(UPDATE_DESC);

        return mockMvc.perform(put("/api/issue/{id}", UpdateIssue.ID.intValue())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestContextUtil.convertObjectToJsonBytes(issue)));
    }

    /**
     * 1. 驗證https status code為200
     */
    @Test
    public void thenReturnHttpStatusCode200() throws Exception {
        updateIssueWithBlankTitle()
                .andExpect(status().isOk());
    }

    /**
     * 2. 驗證是否為預期資訊
     */
    @Test
    public void thenReturnInfo() throws Exception {
        updateIssueWithBlankTitle()
                .andExpect(jsonPath(Update.Property.ID, is(Update.Data.ISSUE_ID.intValue())))
                .andExpect(jsonPath(Update.Property.TITLE, is(UPDATE_TITLE)))
                .andExpect(jsonPath(Update.Property.DESC, is(UPDATE_DESC)))
                .andExpect(jsonPath(Update.Property.STATE, is(Update.Data.STATE)))
                .andExpect(jsonPath(Update.Property.CREATOR_NUM, is(Update.Data.CREATOR_NUM.intValue())))
                .andExpect(jsonPath(Update.Property.ASSIGNEE_NUM, nullValue()))
                .andExpect(jsonPath(Update.Property.CLOSER_NUM, nullValue()))
                .andExpect(jsonPath(Update.Property.RESULT, nullValue()));
    }

    /**
     * 3. 驗證資料庫的變化，title與desc有變化
     */
    @Test
    @ExpectedDatabase(value = "update-issue-should-update-title-and-desc.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void thenUpdateTitleAndDesc() throws Exception {
        updateIssueWithBlankTitle();
    }
}

final class Update {
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

寫在最後，本篇跟前篇總共編寫了四個案例，這些測試只是範例，它們並沒有覆蓋所有可能性的案例；回到現實，實際上在編寫測試程式的時候，我們對於需求本身要有全面性的了解，對於使用的、提供的元件要清楚它們的行為，才有辦法寫出不多不少且適當的測試程式。