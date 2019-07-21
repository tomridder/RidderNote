package com.tomridder.ridder_note.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tomridder.ridder_note.R;
import com.tomridder.ridder_note.bean.Note;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.shaohui.bottomdialog.BottomDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tomridder.ridder_note.mondle.function.getTheLastUnStarNote;
import static com.tomridder.ridder_note.mondle.function.getTheNextUnStarNote;
import static com.tomridder.ridder_note.utils.ChangeUtil.longToString;
import static com.tomridder.ridder_note.utils.PlatformUtil.shareQQ;
import static com.tomridder.ridder_note.utils.PlatformUtil.shareWechatFriend;

public class ShowNote extends AppCompatActivity {

    @BindView(R.id.note_show_back)
    ImageView noteShowBack;
    @BindView(R.id.note_show_up)
    ImageView noteShowUp;
    @BindView(R.id.note_show_down)
    ImageView noteShowDown;
    @BindView(R.id.note_show_line)
    TextView noteShowLine;
    @BindView(R.id.note_show_time)
    TextView noteShowTime;
    @BindView(R.id.note_show_star)
    ImageView noteShowStar;
    @BindView(R.id.note_show_title)
    TextView noteShowTitle;
    @BindView(R.id.note_show_line2)
    TextView noteShowLine2;
    @BindView(R.id.note_show_content)
    TextView noteShowContent;
    @BindView(R.id.note_new_line3)
    TextView noteNewLine3;
    @BindView(R.id.note_show_delete)
    ImageView noteShowDelete;
    @BindView(R.id.note_show_edit)
    ImageView noteShowEdit;
    @BindView(R.id.note_show_change_star)
    ImageView noteShowChangeStar;
    @BindView(R.id.note_show_share)
    ImageView noteShowShare;
    @BindView(R.id.down)
    LinearLayout down;

    private Context mContext=this;
    private Note oldNote;
    private String title1,content1;
    private long date1;
    private int star1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_note);
        ButterKnife.bind(this);
        show();
        setOnClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    private void show()
    {
        Intent intent=getIntent();
        if(intent!=null)
        {
            oldNote=(Note)intent.getSerializableExtra("Note");
            title1=oldNote.getTitle();
            content1=oldNote.getContent();
            date1=oldNote.getDate();
            star1=oldNote.getStar();
            noteShowTitle.setText(title1);
            noteShowContent.setText(content1);
            noteShowTime.setText(DateFormat.format("MM-dd HH:mm:ss",date1));
            if(star1==2)
            {
                noteShowStar.setVisibility(View.INVISIBLE);
            }else
            {
                noteShowStar.setVisibility(View.VISIBLE);
            }

        }
    }
    public static Drawable tintDrawable(@NonNull Drawable drawable, int color)
    {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
    public  void setOnClickListener()
    {
        noteShowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noteShowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               Note note=getTheLastUnStarNote(oldNote);
               noteShowTitle.setText(note.getTitle());
               noteShowContent.setText(note.getContent());
               noteShowTime.setText(DateFormat.format("MM-dd HH:mm:ss",note.getDate()).toString());
               oldNote=note;
            }
        });

        noteShowDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Note note=getTheNextUnStarNote(oldNote);
                noteShowTitle.setText(note.getTitle());
                noteShowContent.setText(note.getContent());
                noteShowTime.setText(DateFormat.format("MM-dd HH:mm:ss",note.getDate()).toString());
                oldNote=note;
            }
        });

        noteShowEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(ShowNote.this,EditNote.class);
                intent.putExtra("Note",oldNote);
                startActivity(intent);
                finish();
            }
        });

        noteShowDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                 Drawable drawable=tintDrawable(getResources().getDrawable(R.drawable.ic_delete_forever_white_24dp),getResources().getColor(R.color.purple));
                  new MaterialDialog.Builder(mContext)
                        .title("Delete")// 标题
                        .content("Are you sure to delete this note?")// 内容
                        .positiveText("Yes")
                        .negativeText("Cancel")
                        .icon(drawable)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DataSupport.deleteAll(Note.class,"title = ? and content = ? and date = ? and star = ?",
                                        oldNote.getTitle(),oldNote.getContent()
                                        , String.valueOf(oldNote.getDate()),String.valueOf(oldNote.getStar()));
                                Toast.makeText(mContext,"Delete Succeed",Toast.LENGTH_SHORT).show();
                                final String SERVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/delete?";
                                final String data="title="+title1+"&date="+ longToString(date1)+"&content="+content1+"&star="+star1;
                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        super.run();
                                        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                                .connectTimeout(10, TimeUnit.SECONDS)
                                                .readTimeout(10,TimeUnit.SECONDS)
                                                .writeTimeout(10,TimeUnit.SECONDS)
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
                                                Log.i("Note","Delete Failed");
                                            }
                                        }catch (IOException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                }.start();
                                finish();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();// 显





            }
        });

        noteShowChangeStar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                me.shaohui.bottomdialog.BottomDialog.create(getSupportFragmentManager())
                        .setLayoutRes(R.layout.bottom_star)
                        .setViewListener(new BottomDialog.ViewListener() {
                            @Override
                            public void bindView(View v)
                            {
                              v.findViewById(R.id.ll_starit).setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v)
                                  {
//                                      String title=oldNote.getTitle();
//                                      String content=oldNote.getContent();
//                                      String date=String.valueOf(oldNote.getDate());
//                                      String content1="Title"+title+'\n'+
//                                              "Date"+date+'\n'+
//                                              "Content"+content+'\n';
//                                      com.tomridder.ridder_note.utils.PlatformUtil.shareQQ(mContext,content1);
                                    Note note=new Note();
                                    note.setStar(1);
                                    int result=note.updateAll("title = ? and content = ? and " +
                                            "date = ? and star = ?",oldNote.getTitle(),oldNote.getContent()
                                    ,String.valueOf(oldNote.getDate()),"2");
                                    Log.i("note","result  :"+result);
                                    if(result>0)
                                    {
                                        noteShowStar.setVisibility(View.VISIBLE);
                                    }
                                    final String SEVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/update?";
                                    final String data="title="+oldNote.getTitle()+"&date="+longToString(oldNote.getDate())+"&content="+oldNote.getContent()+"&star="+"1"
                                            +"&oldTitle="+oldNote.getTitle()+"&oldDate="+longToString(oldNote.getDate())+"&oldContent="+oldNote.getContent()
                                            +"&oldStar="+"2";
                                    new Thread()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            super.run();
                                            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                                    .connectTimeout(10,TimeUnit.SECONDS)
                                                    .writeTimeout(10,TimeUnit.SECONDS)
                                                    .readTimeout(10,TimeUnit.SECONDS)
                                                    .build();
                                            Request request=new Request.Builder()
                                                    .url(SEVER_URL+data)
                                                    .build();
                                            try
                                            {
                                                Response response=okHttpClient.newCall(request).execute();
                                                if(response.isSuccessful())
                                                {
                                                    Log.i("Note",response.body().string());
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
                                  }
                              });

                              v.findViewById(R.id.ll_unstarit).setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      Note note1=new Note();
                                      note1.setStar(2);
//                                      List<Note> notelist=DataSupport.where("title = ? and content = ? and " +
//                                                              "date = ? and star = ?",oldNote.getTitle(),oldNote.getContent()
//                                                      ,String.valueOf(oldNote.getDate()),String.valueOf(oldNote.getStar())).find(Note.class);
//                                      Log.i("note","number of note"+notelist.size());
                                      int result1=note1.updateAll("title = ? and content = ? and " +
                                                      "date = ? and star = ?",oldNote.getTitle(),oldNote.getContent()
                                              ,String.valueOf(oldNote.getDate()),"1"
                                              );
                                      Log.i("note","result 1 :"+result1);
                                     if(result1>0)
                                     {
                                         noteShowStar.setVisibility(View.INVISIBLE);
                                     }
                                      final String SEVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/update?";
                                      final String data="title="+oldNote.getTitle()+"&date="+longToString(oldNote.getDate())+"&content="+oldNote.getContent()+"&star="+"2"
                                              +"&oldTitle="+oldNote.getTitle()+"&oldDate="+longToString(oldNote.getDate())+"&oldContent="+oldNote.getContent()
                                              +"&oldStar="+"1";
                                      new Thread()
                                      {
                                          @Override
                                          public void run()
                                          {
                                              super.run();
                                              OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                                      .connectTimeout(10,TimeUnit.SECONDS)
                                                      .writeTimeout(10,TimeUnit.SECONDS)
                                                      .readTimeout(10,TimeUnit.SECONDS)
                                                      .build();
                                              Request request=new Request.Builder()
                                                      .url(SEVER_URL+data)
                                                      .build();
                                              try
                                              {
                                                  Response response=okHttpClient.newCall(request).execute();
                                                  if(response.isSuccessful())
                                                  {
                                                      Log.i("Note",response.body().string());
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

                                  }
                              });
                            }
                        })
                        .setDimAmount(0.1f)
                        .setCancelOutside(true)
                        .show();

            }
        });
        noteShowShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                me.shaohui.bottomdialog.BottomDialog.create(getSupportFragmentManager())
                        .setLayoutRes(R.layout.bottom_share)
                        .setViewListener(new BottomDialog.ViewListener()
                        {
                            @Override
                            public void bindView(View v)
                            {
                                v.findViewById(R.id.ll_share_qq).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                      String title=oldNote.getTitle();
                                      String content=oldNote.getContent();
                                      String date=DateFormat.format("MM-dd hh:mm",oldNote.getDate()).toString();
                                      String content1="Title: "+title+'\n'+
                                              "Date: "+date+'\n'+
                                              "Content: "+content+'\n';
                                      shareQQ(mContext,content1);


                                    }
                                });
                                v.findViewById(R.id.ll_share_wechat).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String title=oldNote.getTitle();
                                        String content=oldNote.getContent();
                                        String date=DateFormat.format("MM-dd hh:mm",oldNote.getDate()).toString();
                                        String content1="Title: "+title+'\n'+
                                                "Date: "+date+'\n'+
                                                "Content: "+content+'\n';
                                       shareWechatFriend(mContext,content1);
                                    }
                                });
                            }
                        })
                        .setDimAmount(0.1f)
                        .setCancelOutside(true)
                        .show();
            }
        });
    }


}
