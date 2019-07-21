package com.tomridder.ridder_note.mondle;

import android.database.Cursor;
import android.util.Log;

import com.tomridder.ridder_note.bean.Note;

import org.litepal.crud.DataSupport;

public class function
{
    public static Note getTheLastUnStarNote(Note oldNote)
    {
        Note note;
        Cursor c= DataSupport.findBySQL("select * from Note " +
                "where  star =?",String.valueOf(oldNote.getStar()));
        while(c.moveToNext())
        {
            Log.i("note","title ="+oldNote.getTitle()+"content ="+oldNote.getContent()+"date ="+String.valueOf(oldNote.getDate())+
                    "star = "+String.valueOf(oldNote.getStar()));
            String title2=c.getString(c.getColumnIndex("title"));
            String content2=c.getString(c.getColumnIndex("content"));
            if(title2.equals(oldNote.getTitle())&&content2.equals(oldNote.getContent()))
            {
                break;
            }
        }

        if(  c.moveToPrevious()==true)
        {
            String title2 = c.getString(c.getColumnIndex("title"));
            String content2 = c.getString(c.getColumnIndex("content"));
            long date2 = c.getLong(c.getColumnIndex("date"));
            int star2 = c.getInt(c.getColumnIndex("star"));
            note = new Note(title2, content2, date2, star2);
            Log.i("note","moveToPrevious"+"title ="+note.getTitle()+"content ="+note.getContent()+"date ="+String.valueOf(note.getDate())+
                    "star = "+String.valueOf(note.getStar()));
        }
        else
        {
            c.moveToLast();
            String title2 = c.getString(c.getColumnIndex("title"));
            String content2 = c.getString(c.getColumnIndex("content"));
            long date2 = c.getLong(c.getColumnIndex("date"));
            int star2 = c.getInt(c.getColumnIndex("star"));
            note = new Note(title2, content2, date2, star2);
            Log.i("note","moveToLast"+"title ="+note.getTitle()+"content ="+note.getContent()+"date ="+String.valueOf(note.getDate())+
                    "star = "+String.valueOf(note.getStar()));
        }
        return note;
    }
    public static Note getTheNextUnStarNote(Note oldNote)
    {
        Note note;
        Cursor c= DataSupport.findBySQL("select * from Note " +
                "where  star =?",String.valueOf(oldNote.getStar()));
        while(c.moveToNext())
        {
            Log.i("note","title ="+oldNote.getTitle()+"content ="+oldNote.getContent()+"date ="+String.valueOf(oldNote.getDate())+
                    "star = "+String.valueOf(oldNote.getStar()));
            String title2=c.getString(c.getColumnIndex("title"));
            String content2=c.getString(c.getColumnIndex("content"));
            if(title2.equals(oldNote.getTitle())&&content2.equals(oldNote.getContent()))
            {
                break;
            }
        }

        if(  c.moveToNext()==true)
        {
            String title2 = c.getString(c.getColumnIndex("title"));
            String content2 = c.getString(c.getColumnIndex("content"));
            long date2 = c.getLong(c.getColumnIndex("date"));
            int star2 = c.getInt(c.getColumnIndex("star"));
            note = new Note(title2, content2, date2, star2);
            Log.i("note","moveToPrevious"+"title ="+note.getTitle()+"content ="+note.getContent()+"date ="+String.valueOf(note.getDate())+
                    "star = "+String.valueOf(note.getStar()));
        }
        else
        {
            c.moveToFirst();
            String title2 = c.getString(c.getColumnIndex("title"));
            String content2 = c.getString(c.getColumnIndex("content"));
            long date2 = c.getLong(c.getColumnIndex("date"));
            int star2 = c.getInt(c.getColumnIndex("star"));
            note = new Note(title2, content2, date2, star2);
            Log.i("note","moveToLast"+"title ="+note.getTitle()+"content ="+note.getContent()+"date ="+String.valueOf(note.getDate())+
                    "star = "+String.valueOf(note.getStar()));
        }
        return note;
    }
}
