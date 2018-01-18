package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class IssueServiceTest {
    private static final long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup () {
        issueService = new IssueService();
    }

    @Test
    public void queryById_shouldThrowException () {
        Throwable throwable = catchThrowable(() -> issueService.queryById(ID));
        assertThat(throwable)
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("找不到實體");
    }
}
