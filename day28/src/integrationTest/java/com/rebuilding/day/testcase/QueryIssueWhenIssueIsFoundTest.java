package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.rebuilding.day.config.DemoContextConfig;
import com.rebuilding.day.integrationTest.IntegrationTest;
import com.rebuilding.day.integrationTest.IntegrationTestContext;
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
public class QueryIssueWhenIssueIsFoundTest {

    static class IssueProperty {
        static final String ID = "$.id";
        static final String TITLE = "$.title";
        static final String DESC = "$.desc";
        static final String STATE = "$.state";
        static final String CREATOR_NUM = "$.creatorNum";
        static final String ASSIGNEE_NUM = "$.assigneeNum";
        static final String CLOSER_NUM = "$.closerNum";
        static final String RESULT = "$.result";
    }

    static class QueryIssueData {
        static final Long ISSUE_ID = 1L;
        static final String TITLE = "BIG";
        static final String DESC = "big issue desc";
        static final String STATE = "TODO";
        static final Long CREATOR_NUM = 100L;
        static final String ASSIGNEE_NUM = null;
        static final String CLOSER_NUM = null;
        static final String RESULT = null;
    }

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private ResultActions queryIssue() throws Exception {
        return mockMvc.perform(get("/api/issue/{id}", QueryIssueData.ISSUE_ID));
    }

    @Test
    public void thenReturnHttpStatusCodeOk() throws Exception {
        queryIssue()
                .andExpect(status().isOk());
    }

    @Test
    public void thenReturnInfo() throws Exception {
        queryIssue()
                .andExpect(jsonPath(IssueProperty.ID, is(QueryIssueData.ISSUE_ID.intValue())))
                .andExpect(jsonPath(IssueProperty.TITLE, is(QueryIssueData.TITLE)))
                .andExpect(jsonPath(IssueProperty.DESC, is(QueryIssueData.DESC)))
                .andExpect(jsonPath(IssueProperty.STATE, is(QueryIssueData.STATE)))
                .andExpect(jsonPath(IssueProperty.CREATOR_NUM, is(QueryIssueData.CREATOR_NUM.intValue())))
                .andExpect(jsonPath(IssueProperty.ASSIGNEE_NUM, is(QueryIssueData.ASSIGNEE_NUM)))
                .andExpect(jsonPath(IssueProperty.CLOSER_NUM, is(QueryIssueData.CLOSER_NUM)))
                .andExpect(jsonPath(IssueProperty.RESULT, is(QueryIssueData.RESULT)));
    }

}