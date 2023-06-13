package com.Dao;

public class Info {
    private String id;

    private String title;

    private String describe;

    private String content;

    private String author;

    public Info(String id, String title, String author, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.describe = description;
    }



    @Override
    public String toString() {
        return "Info{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", describe='" + describe + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
