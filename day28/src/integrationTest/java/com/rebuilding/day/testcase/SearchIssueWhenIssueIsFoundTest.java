package com.rebuilding.day.testcase;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
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

import static org.hamcrest.Matchers.hasSize;
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