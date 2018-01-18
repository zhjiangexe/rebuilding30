package com.rebuilding.day16.repository;

import com.rebuilding.day16.entity.Issue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueRepositoryFakeTest {

    private static final Long ID = 1L;
    private static final String TITLE = "ISSUE";

    /**
     * 這裡不再透過方法@Before setup()建立新的IssueRepositoryFake物件
     */
    private IssueRepository repository = new IssueRepositoryFake();

    @Before
    public void setup() {
    }

    /**
     * 可以看到Issue的建立與保存，不需任何的配置，就如同原本真的物件
     */
    @Test
    public void saveAndFindIssue() {
        Issue issue = new Issue();
        issue.setTitle(TITLE);

        Issue saved = repository.save(issue);

        assertThat(saved.getId()).isEqualTo(ID);
        assertThat(saved.getTitle()).isEqualTo(TITLE);

        Issue found = repository.findById(saved.getId());

        assertThat(found.getId()).isEqualTo(ID);
        assertThat(found.getTitle()).isEqualTo(TITLE);
    }
}
