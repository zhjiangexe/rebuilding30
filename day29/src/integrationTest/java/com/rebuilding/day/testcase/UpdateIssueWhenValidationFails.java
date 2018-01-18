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
