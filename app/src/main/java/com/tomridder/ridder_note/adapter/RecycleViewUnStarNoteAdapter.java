package com.tomridder.ridder_note.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tomridder.ridder_note.R;
import com.tomridder.ridder_note.bean.Note;

import java.util.List;

public class RecycleViewUnStarNoteAdapter extends BaseQuickAdapter<Note,BaseViewHolder>
{
    public RecycleViewUnStarNoteAdapter(int layoutResId, @Nullable List<Note> data)
    {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Note item)
    {
        if(!TextUtils.isEmpty(item.getTitle()))
        {
//            View view = helper.getView(R.id.tv_title);
            helper.setText(R.id.tv_title,item.getTitle());
        }
        if(item.getDate()==0)
        {
            helper.setText(R.id.tv_date," ");
        }else
        {
            CharSequence format= DateFormat.format("MM-dd HH:mm:ss",item.getDate());
            helper.setText(R.id.tv_date,format.toString());
        }
        if(!TextUtils.isEmpty(item.getContent()))
        {
            helper.setText(R.id.tv_content,item.getContent());
        }else
        {
            helper.setText(R.id.tv_content," ");
        }
//        if(item.getStar()!=0)
//        {
//            helper.setVisible(R.id.iv_star, true);
//        }else
//        {
//            helper.setVisible(R.id.iv_star, false);
//        }
        helper.addOnClickListener(R.id.iv_edit)
                .addOnClickListener(R.id.iv_star2)
                .addOnClickListener(R.id.iv_delete)
                .addOnClickListener(R.id.rl_content)
                .addOnClickListener(R.id.tv_star)
                .addOnClickListener(R.id.tv_delete);
    }
}
