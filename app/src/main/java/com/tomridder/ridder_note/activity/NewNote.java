package com.tomridder.ridder_note.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomridder.ridder_note.R;
import com.tomridder.ridder_note.bean.Note;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tomridder.ridder_note.utils.ChangeUtil.longToString;

public class NewNote extends AppCompatActivity implements View.OnClickListener
{

    Context mContext;
    EditText mTitle,mContent;
    ImageView mBack,mSave;
    TextView mTime;
    int star=2;
    private  String title,content;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        mContext=this;
        initId();
        setOnListener();
    }

    private void setOnListener()
    {
        mBack.setOnClickListener(this);
        mSave.setOnClickListener(this);
//        mStarIt.setOnClickListener(this);
    }

    private void initId()
    {
        mBack=(ImageView)findViewById(R.id.note_new_back);
        mSave=(ImageView)findViewById(R.id.note_new_save);
        mTitle=(EditText)findViewById(R.id.note_new_et_title);
        mContent=(EditText)findViewById(R.id.note_new_et_content);
        mTime=(TextView)findViewById(R.id.note_new_time);
//        mStarIt=(ImageView)findViewById(R.id.note_new_starIt);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.note_new_back:
                finish();
                break;

            case R.id.note_new_save:
                title=mTitle.getText().toString();
                content=mContent.getText().toString();
                Note note=new Note(title,content,System.currentTimeMillis(),star);
                note.save();
                new Thread()
                {
                    @Override
                    public void run() {
                        super.run();
                        final String SERVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/insert?";
                        final String data="title="+title+"&date="+longToString(System.currentTimeMillis())+"&content="+content+"&star="+star;
                        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                .writeTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(10,TimeUnit.SECONDS)
                                .connectTimeout(10,TimeUnit.SECONDS)
                                .build();
                        final Request request=new Request.Builder()
                                .url(SERVER_URL+data)
                                .build();
                        Response response=null;
                        try
                        {
                            response=okHttpClient.newCall(request).execute();
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
                finish();
                break;
//            case R.id.note_new_starIt:
//
//                break;
        }
    }

}
