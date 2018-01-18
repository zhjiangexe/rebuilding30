package com.rebuilding.day6;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class IssueServiceTest4 {
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
        thrown.expect(hasProperty("id", is(ID)));
        issueService.queryById(ID);
    }
}
