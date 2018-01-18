package com.rebuilding.day.testcase;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.integrationTest.IntegrationTest;
import com.rebuilding.day.service.IssueService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 1. 指定為整合測試
 */
@Category(IntegrationTest.class)
/**
 * 2. 確保整合測試使用Spring Test Framework之功能
 */
@RunWith(SpringJUnit4ClassRunner.class)
/**
 * 3. 使用@ContextConfiguration載入所需要的context
 * 或者使用xml
 * @ContextConfiguration(locations = {"classpath:issue-context.xml"})
 */
@ContextConfiguration(classes = {IssueContext.class})
public class GetIssueTest {
    /**
     * 由配置好的context注入所需的spring bean
     */
    @Autowired
    private IssueService issueService;

    @Test
    public void shouldReturnBigIssue() {
        Issue bigIssue = issueService.getBigIssue();
        String title = bigIssue.getTitle();
        assertThat(title).isEqualTo("BIG");
    }
}
