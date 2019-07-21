package com.tomridder.ridder_note.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class Note extends DataSupport implements Serializable
{
    private String title;
    private String content;
    private long date;
    private int star;

    public Note ()
    {

    }
    public Note(String title, String content, long date, int star)
    {

        this.title = title;
        this.content = content;
        this.date = date;
        this.star = star;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }

    public long getDate() {
        return date;
    }

    public Note setDate(long date) {
        this.date = date;
        return this;
    }

    public int getStar() {
        return star;
    }

    public Note setStar(int star) {
        this.star = star;
        return this;
    }
}
