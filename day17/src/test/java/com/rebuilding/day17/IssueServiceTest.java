package com.rebuilding.day17;

import com.rebuilding.day17.entity.Issue;
import com.rebuilding.day17.repository.IssueRepository;
import com.rebuilding.day17.repository.imp.IssueRepositoryImp;
import com.rebuilding.day17.service.IssueService;
import com.rebuilding.day17.service.imp.IssueServiceImp;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.rebuilding.day17.TestDoubles.stub;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

public class IssueServiceTest {
    private IssueService spyService;
    private IssueRepository mockRepository;

    @Before
    public void setup() {
        mockRepository = stub(IssueRepository.class);
        Issue issue = new Issue();
        issue.setTitle("FIRST ISSUE");
        given(mockRepository.save(any())).willReturn(issue);
        spyService = spy(new IssueServiceImp(mockRepository));
    }

    @Test
    public void testSave() {

        Map<String ,String> map = new HashMap<>();
        map.put("title", "first Issue");

        Issue saved = spyService.findOrSave(map);
        assertThat(saved.getTitle()).isEqualTo("FIRST ISSUE");
    }
}
