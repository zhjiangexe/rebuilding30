package com.rebuilding.day10.dataCentral;

import com.rebuilding.day10.*;

/**
 * 1. 建立一個final類別，不讓其他開發人遠可以繼承此類別，類別名稱則以建立的物件為開頭，並以Builder為後綴
 */
public class IssueBuilder {
    /**
     * 2. 將Issue類別中的屬性加入到Builder中
     */
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    /**
     * 3. 增加沒有參數的建構子到建立的類別
     */
    public IssueBuilder() {
    }

    /**
     * 4. 為各個屬性建立設置其屬性值的方法，並且回傳this實現對builder的引用，方法名稱可以直接以屬性名稱當方法名title(String title)、或者使用withTitle(String title)
     */
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

    public IssueBuilder closer(Long closerNum) {
        this.closer = new Closer(closerNum);
        return this;
    }

    public IssueBuilder state(State state) {
        this.state = state;
        return this;
    }

    public IssueBuilder result(Result result) {
        this.result = result;
        return this;
    }

    /**
     * 5. 建立build()方法，它不傳遞任何參數，並且新增Issue物件及設置屬性值之後回傳
     */
    public Issue build() {
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
