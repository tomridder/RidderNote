package com.tomridder.ridder_note.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomridder.ridder_note.R;
import com.tomridder.ridder_note.bean.Note;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tomridder.ridder_note.utils.ChangeUtil.longToString;

public class EditNote extends AppCompatActivity
{
    Note oldNote;
    Context mContext;
    private String title;
    private String content;
    private long time;
    private int star;
    @BindView(R.id.note_edit_star)
    ImageView noteEditStar;
    @BindView(R.id.note_edit_back)
    ImageView noteEditBack;
    @BindView(R.id.note_edit_save)
    ImageView noteEditSave;
    @BindView(R.id.note_edit_line)
    TextView noteEditLine;
    @BindView(R.id.note_edit_time)
    TextView noteEditTime;
    @BindView(R.id.note_edit_title)
    EditText noteEditTitle;
    @BindView(R.id.note_edit_line2)
    TextView noteEditLine2;
    @BindView(R.id.note_edit_content)
    EditText noteEditContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        ButterKnife.bind(this);
        show();
        setOnClickListener();
    }

    private void show()
    {
        Intent intent=getIntent();
        if(intent!=null)
        {
            oldNote = (Note)intent.getSerializableExtra("Note");
            noteEditTitle.setText(oldNote.getTitle());
            noteEditContent.setText(oldNote.getContent());
            noteEditTime.setText(DateFormat.format("MM-dd HH:mm:ss",oldNote.getDate()).toString());
            if(oldNote.getStar()==1)
            {
                noteEditStar.setVisibility(View.VISIBLE);
            }else
            {
                noteEditStar.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void setOnClickListener()
    {
         noteEditBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });

         noteEditSave.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {
                 title=noteEditTitle.getText().toString();
                 content=noteEditContent.getText().toString();
                 time=System.currentTimeMillis();
                 Note note=new Note();
                 note.setTitle(title);
                 note.setContent(content);
                 note.setDate(time);
                 note.setStar(oldNote.getStar());
                 note.updateAll("title = ? and content = ? and " +
                                 "date = ? and star = ?",oldNote.getTitle(),oldNote.getContent()
                         ,String.valueOf(oldNote.getDate()),String.valueOf(oldNote.getStar()));

                 final String SERVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/update?";
                 final String data="title="+title+"&date="+longToString(System.currentTimeMillis())+"&content="+content+"&star="+oldNote.getStar()
                         +"&oldTitle="+oldNote.getTitle()+"&oldDate="+longToString(oldNote.getDate())+"&oldContent="+oldNote.getContent()
                         +"&oldStar="+oldNote.getStar();
                 new Thread()
                 {
                     @Override
                     public void run() {
                         super.run();
                         OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                 .connectTimeout(10, TimeUnit.SECONDS)
                                 .writeTimeout(10,TimeUnit.SECONDS)
                                 .readTimeout(10,TimeUnit.SECONDS)
                                 .build();
                         Request request=new Request.Builder()
                                 .url(SERVER_URL+data)
                                 .build();
                         try
                         {
                             Response response=okHttpClient.newCall(request).execute();
                             if(response.isSuccessful())
                             {
                                 Log.i("Note",response.body().toString());
                             }else
                             {
                                 Log.i("Note","failed");
                             }
                         }catch (IOException e)
                         {
                             e.printStackTrace();
                         }

                     }
                 }.start();
                 Intent intent =new Intent(EditNote.this,ShowNote.class);
                 intent.putExtra("Note",note);
                 startActivity(intent);
                 finish();
             }
         });
    }
}

