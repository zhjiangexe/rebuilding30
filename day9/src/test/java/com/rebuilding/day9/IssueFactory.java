package com.rebuilding.day9;

/**
 * 1. 建立final public類別，避免開發人員繼承，此ObjectMother通常以建立物件的名稱加上Factory來命名。
 */
final public class IssueFactory {
    /**
     * 2. 因為我們只使用ObjectMother的方法，所以加入private建構函式到類別裡，避免開發人員自己new物件。
     */
    private IssueFactory() {

    }

    /**
     * 3. 接下來，將原本測試類別的工廠方法改成public static並搬移至ObjectMother。
     */
    public static Issue createTodoIssueWithoutAssignee(Long id, String title, String desc, Long creatorNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.TODO);
        return issue;
    }

    public static Issue createTodoIssueWithAssignee(Long id, String title, String desc, Long creatorNum, Long assigneeNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.TODO);
        issue.setAssignee(new Assignee(assigneeNum));
        return issue;
    }

    public static Issue createDoneIssueWithResolved(Long id, String title, String desc, Long creatorNum, Long assigneeNum, Long closerNum) {
        Issue issue = new Issue();
        issue.setId(id);
        issue.setTitle(title);
        issue.setDesc(desc);
        issue.setCreator(new Creator(creatorNum));

        issue.setState(State.DONE);
        issue.setAssignee(new Assignee(assigneeNum));
        issue.setCloser(new Closer(closerNum));
        issue.setResult(Result.RESOLVED);
        return issue;
    }
}
