package com.tomridder.ridder_note.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tomridder.ridder_note.R;
import com.tomridder.ridder_note.adapter.ViewPagerAdapter;
import com.tomridder.ridder_note.bean.Note;
import com.tomridder.ridder_note.fragment.StarNoteFragment;
import com.tomridder.ridder_note.fragment.UnStarNoteFragment;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tomridder.ridder_note.utils.ParseJsonData.ParseDataWithJsonObject;

public class MainNote extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayoutStarNote;
    private LinearLayout linearLayoutUnStarNote;
    private ViewPager viewPager;
    private List<Fragment> list;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView iv_add;
    private FloatingActionButton fabAdd;
    Note note1;
    Note note2;
    static  List<Callback1> callback2List=new ArrayList<>();


    public static void setCallback1(Callback1 callback1)
    {

        callback2List.add(callback1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId())
       {
           case R.id.download:
               final String SERVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/getAll";
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
                               .url(SERVER_URL)
                               .build();
                       try
                       {
                           Response response=okHttpClient.newCall(request).execute();
                           if(response.isSuccessful())
                           {
                               String data=response.body().string();
                               ParseDataWithJsonObject(data);
//                               Intent intent=new Intent("com.tomridder.StarNote");
//                               sendBroadcast(intent);
//                               Intent intent2=new Intent("com.tomridder.UnStarNote");
//                               sendBroadcast(intent2);

                               for(int i=0;i<callback2List.size();i++)
                               {
                                   callback2List.get(i).fresh();
                               }
                           }
                       }catch (IOException e)
                       {
                           e.printStackTrace();
                       }
                   }
               }.start();
               break;
           case R.id.deleteAll:
               DataSupport.deleteAll(Note.class);
//               Intent intent=new Intent("com.tomridder.StarNote");
//               sendBroadcast(intent);
//               Intent intent2=new Intent("com.tomridder.UnStarNote");
//               sendBroadcast(intent2);
               for(int i=0;i<callback2List.size();i++)
               {
                   callback2List.get(i).fresh();
               }
               break;
       }
        return true;
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_note);
        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        linearLayoutStarNote.setOnClickListener(this);
        linearLayoutUnStarNote.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        list=new ArrayList<>();
        list.add(new StarNoteFragment());
        list.add(new UnStarNoteFragment());
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        SharedPreferences sharedPreferences=this.getSharedPreferences("share",MODE_PRIVATE);
        boolean isFirstRun=sharedPreferences.getBoolean("isFirstRun",true);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(isFirstRun)
        {
            initData();;
            editor.putBoolean("isFirstRun",false);
            editor.commit();
        }
    }

    private void initData()
    {
              note1 = new Note("Welcome", "Welcom to Ridder Note,you can write all you want in here at any time and any place.", System.currentTimeMillis(), 1);
              note2 = new Note("Welcome", "Welcom to Ridder Note,you can write all you want in here at any time and any place.", System.currentTimeMillis(), 2);
              note1.save();
              note2.save();
    }


    private void initView()
    {
        linearLayoutStarNote=(LinearLayout)findViewById(R.id.ll_starnote);
        linearLayoutUnStarNote=(LinearLayout)findViewById(R.id.ll_unstarnote);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
       // iv_add=(ImageView)findViewById(R.id.iv_add);
        fabAdd=(FloatingActionButton)findViewById(R.id.fab_add);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ll_starnote:
                viewPager.setCurrentItem(0);
                linearLayoutStarNote.setBackgroundColor(getResources().getColor(R.color.orangered));
                linearLayoutUnStarNote.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                break;
            case R.id.ll_unstarnote:
                viewPager.setCurrentItem(1);
                linearLayoutStarNote.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                linearLayoutUnStarNote.setBackgroundColor(getResources().getColor(R.color.orangered));
                break;
//            case R.id.iv_add:
//                Intent intent=new Intent(MainNote.this,NewNote.class);
//                startActivity(intent);
//                break;
            case R.id.fab_add:
                Intent intent=new Intent(MainNote.this,NewNote.class);
                startActivity(intent);
                break;
        }
    }

    public class MyPageChangeListener implements ViewPager.OnPageChangeListener
    {
        public MyPageChangeListener()
        {
            super();
        }

        @Override
        public void onPageSelected(int position) {
            switch (position)
            {
                case 0:
                    linearLayoutStarNote.setBackgroundColor(getResources().getColor(R.color.orangered));
                    linearLayoutUnStarNote.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                    break;
                case 1:
                    linearLayoutStarNote.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                    linearLayoutUnStarNote.setBackgroundColor(getResources().getColor(R.color.orangered));
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    }
    public interface Callback1
    {
        void fresh();
    }
}
