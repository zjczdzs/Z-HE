package com.Dao;

public class Result {
    //String id;
    String title;
    String author;
    String content;

    public String getTitle() {
        return title;
    }

//    public String getId() {
//        return id;
//    }
//
//    public String setId() {
//        return id;
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Result{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
