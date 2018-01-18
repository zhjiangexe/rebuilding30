package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Test;

public class IssueServiceTest2 {
    private static final long ID = 1L;

    private IssueService issueService;

    @Before
    public void setup () {
        issueService = new IssueService();
    }

    @Test(expected = EntityNotFoundException.class)
    public void queryById_shouldThrowException () {
        issueService.queryById(ID);
    }
}
