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
     * 3. 驗證是否為預期資訊
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
     * 4. 驗證資料庫的變化
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