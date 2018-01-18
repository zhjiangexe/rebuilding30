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

    public class QueryAll {
        public class WhenTwoIssueAreFound {

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

            @Test
            public void thenReturnHttpStatusCodeOk() throws Exception {
                mockMvc.perform(get("/api/issue"))
                        .andExpect(status().isOk());
            }

            @Test
            public void thenReturnTwoIssue() throws Exception {
                mockMvc.perform(get("/api/issue"))
                        .andExpect(jsonPath("$", hasSize(2)));
            }

            @Test
            public void thenReturnCorrectInfo() throws Exception {
                ResultActions result = mockMvc.perform(get("/api/issue"));
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
}
