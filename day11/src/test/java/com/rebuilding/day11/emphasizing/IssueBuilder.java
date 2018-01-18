package com.rebuilding.day11.emphasizing;

import com.rebuilding.day11.Closer;
import com.rebuilding.day11.Result;
import com.rebuilding.day11.State;
import com.rebuilding.day11.builderPattern.Issue;
import org.junit.Test;

import java.lang.reflect.Field;

public class IssueBuilder {

    private static final Long NOT_IN_USE = -1L;

    private static final String NOT_IMPORTANT = "NOT IMPORTANT";

    private Long id; // issue單號

    private String title = NOT_IMPORTANT; // 標題
    private String desc; // 內容描述

    private Long creatorNum = NOT_IN_USE; // 建立人
    private Long assigneeNum; // 負責人
    private Long closerNum; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    public IssueBuilder() {
    }

    public static Issue createTodoIssueWithoutAssignee() {
        return new IssueBuilder()
                .withStateTodo()
                .build();
    }

    public static Issue createTodoIssueWithAssignee(Long assigneeNum) {
        return new IssueBuilder()
                .assignee(assigneeNum)
                .withStateTodo()
                .build();
    }

    public static Issue createResolvedIssue(Long closerNum) {
        return new IssueBuilder()
                .assignee(NOT_IN_USE)
                .withResultResolved(closerNum)
                .build();
    }

    public IssueBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public IssueBuilder title(String title) {
        this.title = title;
        return this;
    }

    public IssueBuilder desc(String desc) {
        this.desc = desc;
        return this;
    }

    public IssueBuilder creator(Long creatorNum) {
        this.creatorNum = creatorNum;
        return this;
    }

    public IssueBuilder assignee(Long assigneeNum) {
        this.assigneeNum = assigneeNum;
        return this;
    }

    public IssueBuilder withStateTodo() {
        this.state = State.TODO;
        return this;
    }

    public IssueBuilder withStateInProgress() {
        this.state = State.IN_PROGRESS;
        return this;
    }

    public IssueBuilder withResultResolved(Long closerNum) {
        this.closerNum = closerNum;
        this.state = State.DONE;
        this.result = Result.RESOLVED;
        return this;
    }

    public IssueBuilder withResultReject(Long closerNum) {
        this.closerNum = closerNum;
        this.state = State.DONE;
        this.result = Result.REJECT;
        return this;
    }

    public Issue build() {
        Issue issue = Issue.getBuilder()
                .id(id)
                .title(title)
                .desc(desc)
                .creator(creatorNum)
                .assignee(assigneeNum)
                .build();

        if (closerNum != null) {
            setFieldValue(issue, "closer", new Closer(closerNum));
        }

        setFieldValue(issue, "state", state);
        setFieldValue(issue, "result", result);
        return issue;
    }

    private void setFieldValue(Issue target, String fieldName, Object fieldValue) {
        try {
            Field targetField = target.getClass().getDeclaredField(fieldName);
            targetField.setAccessible(true);
            targetField.set(target, fieldValue);
        } catch (Exception ex) {
            throw new RuntimeException(
                    String.format("錯誤，無法設定屬性：%s", fieldName),
                    ex
            );
        }
    }
}
