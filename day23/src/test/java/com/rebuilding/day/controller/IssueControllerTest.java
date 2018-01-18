package com.rebuilding.day.controller;

import com.rebuilding.day.config.TestContextConfig;
import com.rebuilding.day.config.WebTestUtil;
import com.rebuilding.day.entity.Creator;
import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.entity.State;
import com.rebuilding.day.service.IssueService;
import com.rebuilding.day.web.ErrorHandleAdvice;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.rebuilding.day.config.TestContextConfig.convertObjectToJsonBytes;
import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .setControllerAdvice(new ErrorHandleAdvice())
                .setMessageConverters(TestContextConfig.objectMapperHttpMessageConverter())
                .build();

    }

    public class Create {
        Issue input;

        public class WhenFieldsAreEmpty {
            @Before
            public void createEmptyInput() {
                input = new Issue();
                input.setTitle("");
                input.setDesc("");
            }

            @Test
            public void thenReturnHttpStatusCodeBadRequest() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(input))
                ).andExpect(status().isBadRequest());
            }

            @Test
            public void thenReturnValidationErrorsAsJson() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(input))
                )
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
            }

            @Test
            public void thenReturnOneValidationError() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(input))
                )
                        .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
            }

            @Test
            public void thenReturnValidationErrorAboutEmptyTitle() throws Exception {
                ResultActions perform = mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(input))
                );
                String contentAsString = perform.andReturn().getResponse().getContentAsString();
                System.out.println(contentAsString);
                perform
                        .andExpect(
                                jsonPath("$.fieldErrors[0].field", is("title"))
                        )
                        .andExpect(
                                jsonPath("$.fieldErrors[0].errorCode", is("NotBlank"))
                        );
            }

            @Test
            public void thenNotCreateIssue() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(WebTestUtil.convertObjectToJsonBytes(input))
                );
                verify(issueService, never()).create(isA(Issue.class));
            }
        }

        public class WhenAllFieldsAreValid {
            @Before
            public void setup() {
                createValidInput();
                returnCreated();
            }

            private void createValidInput() {
                input = new Issue();
                input.setTitle("inputTitle");
                input.setDesc("inputDesc");
            }

            private void returnCreated() {
                Issue created = new Issue();
                created.setId(1L);
                created.setTitle("respTitle");
                created.setDesc("respDesc");
                created.setState(State.TODO);
                created.setCreator(new Creator(100L));
                given(issueService.create(isA(Issue.class))).willReturn(created);
            }

            @Test
            public void thenReturnHttpStatusCodeCreated() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(WebTestUtil.convertObjectToJsonBytes(input)))
                        .andExpect(status().isCreated());
            }

            @Test
            public void thenReturnCorrectInfo() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(WebTestUtil.convertObjectToJsonBytes(input)))
                        .andExpect(jsonPath("$.creator.memberNum", is(100)))
                        .andExpect(jsonPath("$.id", is(1)))
                        .andExpect(jsonPath("$.title", is("respTitle")))
                        .andExpect(jsonPath("$.desc", is("respDesc")))
                        .andExpect(jsonPath("$.state", is(State.TODO.toString())));
            }

            @Test
            public void thenCreateIssueWithoutId() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(WebTestUtil.convertObjectToJsonBytes(input)));

                verify(issueService, times(1))
                        .create(
                                assertArg(issue -> assertThat(issue.getId()).isNull())
                        );
            }

            @Test
            public void thenCreateIssueWithCorrectTitle() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(WebTestUtil.convertObjectToJsonBytes(input)));

                verify(issueService, times(1))
                        .create(
                                assertArg(issue -> assertThat(issue.getTitle()).isEqualTo("inputTitle"))
                        );
            }

            @Test
            public void thenCreateIssueWithCorrectDesc() throws Exception {
                mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(WebTestUtil.convertObjectToJsonBytes(input)));

                verify(issueService, times(1))
                        .create(
                                assertArg(issue -> assertThat(issue.getDesc()).isEqualTo("inputDesc"))
                        );
            }
        }
    }

}
