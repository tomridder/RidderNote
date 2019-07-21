package com.tomridder.ridder_note.utils;

import com.tomridder.ridder_note.bean.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import static com.tomridder.ridder_note.utils.ChangeUtil.stringToLong;

public class ParseJsonData
{
    public static void ParseDataWithJsonObject(String response)
    {
        try
        {
            JSONArray notes=new JSONArray(response);
            DataSupport.deleteAll(Note.class);
            for(int i=0;i<notes.length();i++)
            {
                JSONObject note=notes.getJSONObject(i);
                String title=note.getString("title");
                String content=note.getString("content");
                String date=note.getString("date");
                int star=note.getInt("star");
                Note note1=new Note(title,content,stringToLong(date),star);
                note1.save();
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
