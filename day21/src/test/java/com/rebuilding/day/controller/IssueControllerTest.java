package com.rebuilding.day.controller;

import com.rebuilding.day.config.TestContextConfig;
import com.rebuilding.day.service.IssueService;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;

//@RunWith(HierarchicalContextRunner.class)
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

}
