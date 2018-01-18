package com.rebuilding.day11.emphasizing;

import com.rebuilding.day11.*;

public class Issue {
    private Long id; // issue單號
    private String title; // 標題
    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT

    /**
     * 1. 移除setter，只留下getter
     */
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Creator getCreator() {
        return creator;
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public Closer getCloser() {
        return closer;
    }

    public State getState() {
        return state;
    }

    public Result getResult() {
        return result;
    }

    /**
     * 2. 建立一個內部類別Builder
     */
    public static class Builder {
        /**
         * 2-1. 加入屬性id, title, desc, assignee, creator。
         */
        private Long id;
        private String title;
        private String desc;
        private Creator creator;
        private Assignee assignee;

        /**
         * 2-2. 加入private建構函式到類別裡，避免開發人員自己new物件。
         */
        private Builder() {
        }

        /**
         * 2-3. 加入屬性id, title, desc, assignee, creator設定的方法，並回傳builder的引用
         */
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder creator(Long creatorNum) {
            if (creatorNum != null) {
                this.creator = new Creator(creatorNum);
            }
            return this;
        }

        public Builder assignee(Long assigneeNum) {
            if (assigneeNum != null) {
                this.assignee = new Assignee(assigneeNum);
            }
            return this;
        }

        /**
         * 2-4. 加入build()方法，執行Issue類別的private建構函式，產生Issue物件回傳。
         */
        public Issue build() {
            return new Issue(this);
        }
    }

    /**
     * 3. 加入private建構函式，建立初始狀態為待處理的Issue物件
     */
    private Issue(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.desc = builder.desc;
        this.creator = builder.creator;
        this.assignee = builder.assignee;
        this.closer = null;
        this.state = State.TODO;
        this.result = null;
    }

    /**
     * 4. 加入靜態方法getBuilder()，回傳Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

}
