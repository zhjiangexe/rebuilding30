package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IssueServiceTest3 {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Long ID = 1L;

    private IssueService issueService;

    @Before
    public void createSystemUnderTest() {
        issueService = new IssueService();
    }

    @Test
    public void findById_ShouldThrowException() {
        thrown.expect(EntityNotFoundException.class);
        issueService.queryById(ID);
    }
}
