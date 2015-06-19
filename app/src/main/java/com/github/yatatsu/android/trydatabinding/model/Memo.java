package com.github.yatatsu.android.trydatabinding.model;


/**
 * メモのモデル
 */
public class Memo {

    public enum Status {
        NotYet,
        Done,
        Archived,
        ;
    }

    public Memo() {
        this.status = Status.NotYet;
        this.views = 0;
    }

    public static Memo newInstance(String title, String body) {
        Memo item = new Memo();
        item.setTitle(title);
        item.setBody(body);
        return item;
    }

    private String title;
    private String body;
    private Status status;
    private int views;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
