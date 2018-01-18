package com.rebuilding.day10.stateful;

import com.rebuilding.day10.*;

public class IssueBuilder {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
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

    public IssueBuilder creator(Long creatorNum) {
        this.creator = new Creator(creatorNum);
        return this;
    }

    public IssueBuilder assignee(Long assigneeNum) {
        this.assignee = new Assignee(assigneeNum);
        return this;
    }

    /**
     * 1. 建立方法withStateTodo()，描述Issue狀態為待處理。
     */
    public IssueBuilder withStateTodo() {
        this.state = State.TODO;
        return this;
    }

    /**
     * 2. 建立方法withStateInProgress()，描述Issue狀態為執行中。
     */
    public IssueBuilder withStateInProgress() {
        this.state = State.IN_PROGRESS;
        return this;
    }

    /**
     * 3. withResultResolved(Long closerNum)，描述Issue已解決，並帶入結案人
     */
    public IssueBuilder withResultResolved(Long closerNum) {
        this.closer = new Closer(closerNum);
        this.state = State.DONE;
        this.result = Result.RESOLVED;
        return this;
    }

    /**
     * 3. withResultReject(Long closerNum)，描述Issue被駁回，並帶入結案人
     */
    public IssueBuilder withResultReject(Long closerNum) {
        this.closer = new Closer(closerNum);
        this.state = State.DONE;
        this.result = Result.REJECT;
        return this;
    }

    public Issue build() {
        /**
         * 4. 額外的，如果要確保Issue不能沒有creator，則修改build()執行時要做判斷。
         */
        if (creator == null) {
            throw new NullPointerException(
                    "You cannot create a task without creator"
            );
        }
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(creator);
        issue.setAssignee(assignee);
        issue.setCloser(closer);
        issue.setState(state);
        issue.setResult(result);
        return issue;
    }
}
