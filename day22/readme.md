# Spring MVC Controller Unit Test《二》

上一篇介紹了如何編寫Rest Controller單元測試的初始配置，本篇接續研究怎麼進行發送request及驗證response。

## 待測系統 & 單元測試的配置

系統內建立了一個IssueController，它負責處理兩個request：
1. 查出所有的Issue：GET /api/issue -> List< Issue > queryAll
2. 查出指定的Issue：GET /api/issue/{id} -> Issue queryById
```java
package com.rebuilding.day.controller;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Issue> queryAll() {
        return issueService.queryAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Issue queryById(@PathVariable Long id) {
        return issueService.queryById(id);
    }
}
```

Controller單元測試的配置，請參考以下程式碼及說明：
```java
package com.rebuilding.day.controller;

import org.junit.runner.RunWith;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import com.rebuilding.day.config.TestContextConfig;
import org.junit.Before;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.rebuilding.day.service.IssueService;
import static org.mockito.Mockito.mock;

/**
* 1. 建立相對應的IssueControllerTest類別，為了編寫巢狀單元測試，加上了註解@RunWith(HierarchicalContextRunner.class)
**/
@RunWith(HierarchicalContextRunner.class)
public class IssueControllerTest {

    /**
    * 2. 準備將會配置IssueController的MockMvc屬性
    **/
    private MockMvc mockMvc;

    /**
    * 3. 準備IssueController的相依屬性
    **/
    private IssueService issueService;

    /**
    * 4. 配置MockMvc
    **/
    @Before
    public void setup() {
        /**
        * 4-1. 建立Controller物件
        **/
        IssueController issueController = new IssueController();
        /**
        * 4-2. mock相依物件，此物件會當成Stub及Mock
        **/
        issueService = mock(IssueService.class);
        /**
        * 4-3. 將mock的IssueService注入IssueController中。
        * P.S. 由於在Spring框架中主要使用@Autowird來注入屬性；而且我們也不想要為了測試還特別編寫建構函式或setter，所以此處透過反射工具將其物件直接注入到指定屬性。
        * ReflectionTestUtils.setField(目標物件, "目標物件之指定屬性", 相依物件)
        **/
        ReflectionTestUtils.setField(issueController, "issueService", issueService);
        /**
        * 4-4. 依照上一篇的方式，建立並配置代替IssueController的MockMvc物件。
        **/
        mockMvc = MockMvcBuilders.standaloneSetup(issueController)
                .setMessageConverters(TestContextConfig.objectMapperHttpMessageConverter())
                .build();
    }
}
```

接下來，讓我們試著為這兩個處理情境編寫單元測試吧。

## <情境一>查出所有的Issue
驗證方法QueryAll通過以下期望：
1. 驗證回傳http status code 200。
2. 驗證回傳指定數量的issue。
3. 驗證回傳正確的資訊。

請參考以下程式碼及說明：
```java
package com.rebuilding.day.controller;

import com.rebuilding.day.config.TestContextConfig;
import com.rebuilding.day.entity.Creator;
import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.entity.State;
import com.rebuilding.day.service.IssueService;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(HierarchicalContextRunner.class)
public class IssueControllerTest {

    private IssueService issueService;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        issueService = mock(IssueService.class);
        IssueController issueController = new IssueController();
        ReflectionTestUtils.setField(issueController, "issueService", issueService);

        mockMvc = MockMvcBuilders.standaloneSetup(issueController)
                .setMessageConverters(TestContextConfig.objectMapperHttpMessageConverter())
                .build();
    }

    /**
    * 1. 第二層類別，以驗證的方法名稱命名
    **/
    public class QueryAll {
        /**
        * 2. 第三層類別，以測試案例命名
        **/
        public class WhenTwoIssueAreFound {
            /**
            * 3. 依照測試案例進行配置，這裡偽造方法issueService.queryAll()使其回傳指定的issue陣列
            **/
            @Before
            public void returnTwoIssue() {
                Issue issue1 = new Issue();
                issue1.setId(1L);
                issue1.setTitle("first");
                issue1.setState(State.TODO);

                Issue issue2 = new Issue();
                issue2.setId(2L);
                issue2.setTitle("second");
                issue2.setState(State.TODO);

                given(issueService.queryAll())
                        .willReturn(Arrays.asList(issue1, issue2));
            }

            /**
            * 4. 驗證回傳http status code 200。
            **/
            @Test
            public void thenReturnHttpStatusCodeOk() throws Exception {
                /**
                * 透過物件mockMvc方法perform，發動GET /api/issue，最後使用andExpect來驗證
                **/
                mockMvc.perform(get("/api/issue"))
                        .andExpect(status().isOk());
            }

            /**
            * 4. 驗證回傳指定數量的issue。
            **/
            @Test
            public void thenReturnTwoIssue() throws Exception {
                /**
                * 這裡將會同時使用jsonPath，hamcrest語法，請參考本篇最後附的文件連結
                **/
                mockMvc.perform(get("/api/issue"))
                        .andExpect(jsonPath("$", hasSize(2)));
            }

            /**
            * 5. 驗證回傳正確的資訊。
            **/
            @Test
            public void thenReturnCorrectInfo() throws Exception {
                ResultActions result = mockMvc.perform(get("/api/issue"));
                /**
                * 一開始編寫過程可能有些不確定，可以透過方法ResultActions.andReturn()檢查回傳的物件。
                **/
                String contentAsString = result.andReturn().getResponse().getContentAsString();
                System.out.println(contentAsString);
                result.andExpect(jsonPath("$[0].id", is(1)))
                        .andExpect(jsonPath("$[0].title", is("first")))
                        .andExpect(jsonPath("$[0].state", is(State.TODO.toString())))
                        .andExpect(jsonPath("$[1].id", is(2)))
                        .andExpect(jsonPath("$[1].title", is("second")))
                        .andExpect(jsonPath("$[1].state", is(State.TODO.toString())));
            }
        }
    }
}
```

## <情境二>查出指定的Issue
驗證方法QueryById通過以下期望：
1. 驗證回傳http status code 200。
2. 驗證回傳正確的資訊。

這邊僅列出第二層類別，請參考以下程式碼：
```java
public class QueryById {
    public class WhenIssueIsFound {
        @Before
        public void setup() {
            Issue issue = new Issue();
            issue.setId(3L);
            issue.setCreator(new Creator(100L));
            issue.setTitle("third");
            issue.setDesc("desc");
            issue.setState(State.TODO);

            given(issueService.queryById(3L)).willReturn(issue);
        }

        @Test
        public void thenReturnHttpStatusCodeOk() throws Exception {
            /**
            * 這裡傳入參數@PathVariable
            * 可以看到方法org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get有支援
            **/
            mockMvc.perform(get("/api/issue/{id}", 3L))
                    .andExpect(status().isOk());
        }

        @Test
        public void thenReturnCorrectInfo() throws Exception {
            mockMvc.perform(get("/api/issue/{id}", 3L))
                    .andExpect(jsonPath("id", is(3)))
                    .andExpect(jsonPath("title", is("third")))
                    .andExpect(jsonPath("desc", is("desc")))
                    .andExpect(jsonPath("state", is(State.TODO.toString())))
                    .andExpect(jsonPath("creator.memberNum", is(100)));
        }
    }
}
```

> 編寫的過程中注意，因為這部分import的靜態方法會有些同名方法的問題，所以必須確認是否來自正確的類別，如get，content，jsonPath，is等方法。
> jsonPath語法參考：[https://github.com/jayway/JsonPath](https://github.com/jayway/JsonPath)
> hamcrest語法參考：[http://hamcrest.org/JavaHamcrest/javadoc/1.3/](http://hamcrest.org/JavaHamcrest/javadoc/1.3/)


其實spring controller的測試做法不難，但我們必須先要有一些前置知識，譬如使用測試替身來獨立待測系統、如何配置及使用MockMvc、易於維護的巢狀寫法、定義需求及測試案例等等，才容易寫出好的測試程式。
以上，本篇說明了如何使用mockMvc物件發送request及驗證response；下一篇，繼續說明其他驗證的情境。