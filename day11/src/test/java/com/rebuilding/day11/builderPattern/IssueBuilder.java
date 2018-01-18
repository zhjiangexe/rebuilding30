package com.rebuilding.day11.builderPattern;

import com.rebuilding.day11.Closer;
import com.rebuilding.day11.Result;
import com.rebuilding.day11.State;

import java.lang.reflect.Field;

public class IssueBuilder {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    /**
     * 1. 將屬性creator, assignee, closer修改為creatorNum, assigneeNum, closerNum
     */
    private Long creatorNum; // 建立人
    private Long assigneeNum; // 負責人
    private Long closerNum; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    public IssueBuilder() {
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

    /**
     * 2. 並將方法creator(Long creatorNum), assignee(Long assigneeNum)內部設值方式，從this.creator = new Creator(creatorNum)修改為this.creatorNum = creatorNum
     */
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

    /**
     * 3. 將withResultResolved(Long closerNum), withResultReject(Long closerNum)內部設值的方式this.closerNum = new Closer(closerNum)修改為this.closerNum = closerNum
     */
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

    /**
     * 4. 修改build()，透過Issue類別所提供的Builder來建立Issue物件。
     */
    public Issue build() {
        Issue issue = Issue.getBuilder()
                .id(id)
                .title(title)
                .desc(desc)
                .creator(creatorNum)
                .assignee(assigneeNum)
                .build();

        /**
         * 5-2. 使用setFieldValue()方法，來設定closer, state, result
         */
        if (closerNum != null) {
            setFieldValue(issue, "closer", new Closer(closerNum));
        }

        setFieldValue(issue, "state", state);
        setFieldValue(issue, "result", result);
        return issue;
    }

    /**
     * 5-1. 建立共用反射設值的方法setFieldValue(Issue target, String fieldName, Object fieldValue)
     */
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
