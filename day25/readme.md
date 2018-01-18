# 整合測試《二》Spring Test DbUnit

spring-test-dbunit提供了DbUnit與Spring Test Framework的整合，主要有以下關鍵：
1. 使用@DatabaseSetup來初始化資料庫
2. 使用@ExpectedDatabase為資料庫內容編寫斷言
3. 使用@DbUnitConfiguration來指定配置

首先引入必要的套件
build.gradle
```java
integrationTestCompile(
     'org.dbunit:dbunit:2.5.4',
     'com.github.springtestdbunit:spring-test-dbunit:1.3.0'
)
```

## 配置Spring Test DbUnit

在一般狀況中Spring Test Framework已經註冊了預設的Listener，但要使用Spring test DbUnit時，我們必須註冊新的DbUnitTestExecutionListener。
```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.rebuilding.day.integrationTest.IntegrationTest;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
/**
* 為了整合Spring與DbUnit除了註冊新的DbUnitTestExecutionListener以外，也必須重新註冊所有Listener
* DependencyInjectionTestExecutionListener提供解析及依賴注入的支援
* TransactionalTestExecutionListener提供解析@Transaction、@NotTransactional、@Rollback等交易管理
**/
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@ContextConfiguration(classes = {PersistenceContext.class})
public class FindIssueByIdTest {
}
```

上述設定啟用了Spring Test DbUnit，如果我們使用預設的配置，DbUnit：
- 通過使用名為dataSource或dbUnitDatabaseConnection的bean來跟資料庫連線。
- 使用FlatXmlDataSetLoader類別載入data sets

除了預設的配置以外，如果要自訂DbUnit的配置，應該怎麼辦呢？

# 自訂DbUnit配置

首先介紹註解@DbUnitConfiguration，它包含以下幾個屬性：
- databaseConnection為DbUnit提供資料庫連接
- dataSetLoader為設定載入器類別，Spring Test DbUnit預設是FlatXmlDataSetLoader進行DataSet的載入
- dataSetLoaderBean為設定載入器名稱
- databaseOperationLookup為設定資料庫操作的類別

這裡我們編寫一個自訂的dataSetLoader，實作：
當DataSet中遇到字串"\[empty_string\]"時，將其轉為空字串""
```java
package com.rebuilding.day.testcase;
import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.springframework.core.io.Resource;
/**
 * 1. 繼承FlatXmlDataSetLoader的自訂資料集合載入器MyDataSetLoader
 */
public class MyDataSetLoader extends FlatXmlDataSetLoader {
    /**
     * 2. 覆寫方法createDataSet，這邊是當取得的DataSet中遇到字串"[empty_string]"時，將其轉為空字串""
     */
    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        IDataSet flatXmlDataSet = super.createDataSet(resource);
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(flatXmlDataSet);
        replacementDataSet.addReplacementObject("[empty_string]", "");
        return replacementDataSet;
    }
}
```

準備DbUnit - DataSet
issue.xml
```xml
<dataset>
    <issue id="1"
           title="BIG"
           desc="test desc 1"
    />
    <issue id="2"
           title="[empty_string]"
           desc="test desc 2"
    />
</dataset>
```

PersistenceContext設定了DataSource以及IssueRepository相依的jdbcTemplate，這裡採用H2資料庫
```java
package com.rebuilding.day.config;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan({"com.rebuilding.day"})
@PropertySource("classpath:application.properties")
public class PersistenceContext {

    @Bean
    public DataSource dataSource(Environment env) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("db.driver"));
        dataSource.setUrl(env.getRequiredProperty("db.url"));
        dataSource.setUsername(env.getRequiredProperty("db.username"));
        dataSource.setPassword(env.getRequiredProperty("db.password"));
        return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:changelog/changelog.xml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
```

接下來透過@DbUnitConfiguration，來載入自訂的MyDataSetLoader
```java
package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.integrationTest.IntegrationTest;
import com.rebuilding.day.integrationTest.IntegrationTestContext;
import com.rebuilding.day.repository.IssueRepository;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
/**
* 載入context，包含IssueRepository所需的相依
**/
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
/**
* 整合DbUnitTestExecutionListener，並載入DataSet
**/
@DatabaseSetup("issue.xml")
/**
* 自訂DbUnitConfigration，載入了自訂的DataSetLoader
**/
@DbUnitConfiguration(dataSetLoader = MyDataSetLoader.class)
public class FindIssueByIdTest {
    private final static Long ID = 1L;
    @Autowired
    private IssueRepository repository;
    @Test
    public void shouldReturn(){
        Issue issue = repository.findById(ID);
        assertThat(issue).isNotNull();
    }
}
```

本篇介紹了整合測試中，需要整合資料庫及測試資料的情境時，我們可以透過DbUnit來實現。