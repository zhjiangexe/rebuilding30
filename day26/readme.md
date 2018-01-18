# 整合測試《三》WebApplicationContext & Profile

# WebApplicationContext

如果整合測試包含了Controller，則透過設定WebApplicationContext來達成。

與前篇相同名稱的類別FindIssueByIdTest，但在這裡我們試著載入了WebApplicationContext來執行，請參考下列程式碼及說明：
```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.rebuilding.day.config.PersistenceContext;
import com.rebuilding.day.integrationTest.IntegrationTest;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
/**
* 1. 透過註解@WebAppConfiguration載入WebApplicationContext的實體
**/
@WebAppConfiguration
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        /**
        * 2. 加入監聽器ServletTestExecutionListener，提供模擬servelt api支援
        **/
        ServletTestExecutionListener.class
})
@DatabaseSetup("issue.xml")
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
public class FindIssueByIdTest {

    /**
    * 3. 由Spring Test注入WebApplicationContext實體
    **/
    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
    * 4. 要編寫controller測試所需的mockMvc
    **/
    private MockMvc mockMvc;

    /**
    * 5. 透過方法MockMvcBuilders.webAppContextSetup載入WebApplicationContext，生成mockMvc物件
    **/
    @Before
    public void configureSystemUnderTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }
}

```

經過以上的步驟，mockMvc是基於webApplicationContext所建立的物件，所以可以使用它來編寫web應用的整合測試了。

---

# Profile

一個應用程式在不同的環境通常有不同的配置元件，比如說測試機的資料庫與正式機的資料庫一定不同，那就會用到Profile來提供不同的配置。

但是整合測試需要使用Profile主要希望是能夠更快速，更不易出錯的元件來代替真正的元件，如：
- 加速測試。譬如，對於資料庫配置改成用像是H2這種記憶體資料庫。
- 避免測試時帶有無法控制的相依。譬如使用mock取代向外部服務發送http request。
- 避免使用真實的時間。當有相依於時間的情況出現，每次驗證的結果可能會不一樣。

以下示範使用Spring提供的Profile功能，區分並使用不同環境的配置。

首先，透過@Profile("註冊名稱")來設定要執行的元件
```java
package com.rebuilding.day.config;

import org.springframework.context.annotation.*;

@Configuration
@Import(PersistenceContext.class)
public class DemoContextConfig {
    @Profile("prod")
    @Configuration
    @PropertySource("classpath:application-prod.properties")
    static class ApplicationProperties {
    }

    @Profile("integrationTest")
    @Configuration
    @PropertySource("classpath:application-test.properties")
    static class IntegrationTestProperties {
    }

    @Profile("prod")
    @Bean
    String prod() {
        return "prod";
    }

    @Profile("integrationTest")
    @Bean
    String test() {
        return "test";
    }
}
```

設定完各個Profile要使用的元件之後，使用@ActiveProfiles來指定要執行的Profile
```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.rebuilding.day.config.PersistenceContext;
import com.rebuilding.day.integrationTest.IntegrationTest;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        ServletTestExecutionListener.class
})
@DatabaseSetup("issue.xml")
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
/**
* 指定整合測試使用integrationTest Profile的配置
**/
@ActiveProfiles("integrationTest")
public class FindIssueByIdTest {
}
```