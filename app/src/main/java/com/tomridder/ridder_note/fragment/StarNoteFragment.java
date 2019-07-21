package com.tomridder.ridder_note.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tomridder.ridder_note.R;
import com.tomridder.ridder_note.activity.EditNote;
import com.tomridder.ridder_note.activity.MainNote;
import com.tomridder.ridder_note.activity.ShowNote;
import com.tomridder.ridder_note.adapter.RecycleViewStarNoteAdapter;
import com.tomridder.ridder_note.bean.Note;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tomridder.ridder_note.utils.ChangeUtil.longToString;

public class StarNoteFragment extends Fragment implements MainNote.Callback1, BaseQuickAdapter.OnItemChildClickListener
{
    @BindView(R.id.rv_star_notes)
    RecyclerView starNotesRecyclerView;
    @BindView(R.id.star_et_search)
    EditText starEtSearch;
    Unbinder unbinder;
    private List<Note> notes;
    private RecycleViewStarNoteAdapter recycleViewStarNoteAdapter;
    private IntentFilter intentFilter;
    private StarChangeReceiver starChangeReceiver;

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 2:
                    List<Note> starNotes=QueryStarNotes();
                    recycleViewStarNoteAdapter =new RecycleViewStarNoteAdapter(R.layout.star_note,starNotes);
                    recycleViewStarNoteAdapter.setOnItemChildClickListener(StarNoteFragment.this);
                    starNotesRecyclerView.setAdapter(recycleViewStarNoteAdapter);
                    Log.i("Note","Received ");
                    break;
                    default:
                        break;
            }
        }
    };

    @Override
    public void fresh()
    {
        Message message=new Message();
        message.what=2;
        handler.sendMessage(message);
        Log.i("Note","Send ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View inflate=inflater.inflate(R.layout.fragment_starnote,container,false);
        unbinder=ButterKnife.bind(this,inflate);
        MainNote.setCallback1(this);
        return  inflate;
    }


    class StarChangeReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
           // Toast.makeText(getContext(),"received",Toast.LENGTH_LONG).show();
            List<Note> starNotes=QueryStarNotes();
            recycleViewStarNoteAdapter =new RecycleViewStarNoteAdapter(R.layout.star_note,starNotes);
            recycleViewStarNoteAdapter.setOnItemChildClickListener(StarNoteFragment.this);
            starNotesRecyclerView.setAdapter(recycleViewStarNoteAdapter);
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainNote.setCallback1(this);
        starNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         List<Note> starNotes=QueryStarNotes();
        recycleViewStarNoteAdapter =new RecycleViewStarNoteAdapter(R.layout.star_note,starNotes);
        recycleViewStarNoteAdapter.setOnItemChildClickListener(this);
        starNotesRecyclerView.setAdapter(recycleViewStarNoteAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.tomridder.StarNote");
        starChangeReceiver=new StarChangeReceiver();
        getActivity().registerReceiver(starChangeReceiver,intentFilter);

        starEtSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key=starEtSearch.getText().toString();
                notes=querylikeStarNotes(key);
                recycleViewStarNoteAdapter =new RecycleViewStarNoteAdapter(R.layout.star_note,notes);
                recycleViewStarNoteAdapter.setOnItemChildClickListener(StarNoteFragment.this);
                starNotesRecyclerView.setAdapter(recycleViewStarNoteAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private List<Note> querylikeStarNotes(String key)
    {
        List<Note> notes=DataSupport.where("(title like ? or content like ?) and  star = ? ","%"+key+"%","%"+key+"%",1+"").find(Note.class);
        Log.i("note","note size = "+ notes.size());
        return notes;

    }

    private List<Note> QueryStarNotes()
    {

       List<Note> notes= DataSupport.where("star =?","1")
               .order("date desc")
               .find(Note.class);

          return notes;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainNote.setCallback1(this);
        List<Note> starNotes=QueryStarNotes();
        recycleViewStarNoteAdapter =new RecycleViewStarNoteAdapter(R.layout.star_note,starNotes);
        recycleViewStarNoteAdapter.setOnItemChildClickListener(this);
        starNotesRecyclerView.setAdapter(recycleViewStarNoteAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position)
    {
        int id=view.getId();
        Intent intent;
        Note note;
        switch (id)
        {
            case R.id.iv_edit:
                note=(Note)adapter.getData().get(position);
                Intent intent1=new Intent(getActivity(), EditNote.class);
                intent1.putExtra("Note",note);
                startActivity(intent1);
                break;

            case R.id.iv_delete:
                note=(Note)adapter.getData().get(position);
                int result= DataSupport.deleteAll(Note.class,"title = ? and content = ? and date = ? and star = ?",note.getTitle(),note.getContent()
                        , String.valueOf(note.getDate()),String.valueOf(note.getStar()));
                if(result>0)
                {
                    recycleViewStarNoteAdapter.remove(position);
                }
                Log.i("tag","result"+result);
                break;

            case R.id.iv_star2:
                note=(Note)adapter.getData().get(position);
                Note note1=new Note();
                note1.setStar(2);
                int result1=note1.updateAll("title = ? and content = ? and " +
                                "date = ? and star = ?",note.getTitle(),note.getContent()
                        ,String.valueOf(note.getDate()),String.valueOf(note.getStar()));
                Log.i("note","result1 "+result1);
                if(result1>0)
                {
                    recycleViewStarNoteAdapter.remove(position);
                }
                intent=new Intent("com.tomridder.UnStarNote");
                getContext().sendBroadcast(intent);
                break;

            case R.id.rl_content:
                note=(Note)adapter.getData().get(position);
                Intent intent2=new Intent(getActivity(), ShowNote.class);
                intent2.putExtra("Note",note);
                startActivity(intent2);
                break;
            case R.id.tv_star:
                note=(Note)adapter.getData().get(position);
                Note note2=new Note();
                note2.setStar(2);
                int result2=note2.updateAll("title = ? and content = ? and " +
                                "date = ? and star = ?",note.getTitle(),note.getContent()
                        ,String.valueOf(note.getDate()),String.valueOf(note.getStar()));
                Log.i("note","result1 "+result2);
                if(result2>0)
                {
                    recycleViewStarNoteAdapter.remove(position);
                }

                final String SEVER_URL="http://coder.struggling-bird.cn:8761/weixin/note/update?";
                final String data="title="+note.getTitle()+"&date="+longToString(note.getDate())+"&content="+note.getContent()+"&star="+"2"
                        +"&oldTitle="+note.getTitle()+"&oldDate="+longToString(note.getDate())+"&oldContent="+note.getContent()
                        +"&oldStar="+"1";
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        super.run();
                        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
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

                intent=new Intent("com.tomridder.UnStarNote");
                getContext().sendBroadcast(intent);
                break;
            case R.id.tv_delete:
                note=(Note)adapter.getData().get(position);
                int result3= DataSupport.deleteAll(Note.class,"title = ? and content = ? and date = ? and star = ?",note.getTitle(),note.getContent()
                        , String.valueOf(note.getDate()),String.valueOf(note.getStar()));
                if(result3>0)
                {
                    recycleViewStarNoteAdapter.remove(position);
                }
                Log.i("tag","result"+result3);

                final String SERVER_URL1="http://coder.struggling-bird.cn:8761/weixin/note/delete?";
                final String data1="title="+note.getTitle()+"&date="+longToString(note.getDate())+"&content="+note.getContent()+"&star="+"1";
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
                                .url(SERVER_URL1+data1)
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
                            Log.i("Note","IOException");
                        }

                    }
                }.start();
                
                break;


        }

    }
}
