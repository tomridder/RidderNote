### 前言
> 本文的内容主要是解析Ridder Note APP 的制作流程，以及代码的具体实现，若有什么不足之处，还请提出建议，附上这个 APP 的 Github 地址 [Ridder Note](https://github.com/tomridder/RidderNote) 欢迎大家 star 和 fork.

Ridder Note可实现功能：

[note的增删改查]

[note分享到QQ WECHAT]

[note备份到服务器]

![Image](https://github.com/tomridder/Ridder-Note/blob/master/3.png)

#### 本文的主要内容
- 查询note的实现（litepal+sqlite）
- StarNote和 UnStarNote切换的实现(Broadcast)
- 备份日记到服务器的实现(接口回调 +handler)


先来一波Note的展示吧，这款 APP 还是非常精美和优雅的
- 增删查改note的效果

![1.gif](https://github.com/tomridder/RidderNote/blob/master/1.gif)

- StarNote和 UnStarNote切换  上翻下翻记事 清空记事后服务器端恢复记事的效果
![2.gif](https://github.com/tomridder/RidderNote/blob/master/2.gif)
## 一、日记查询的实现
#### 1、利用litepal建立表
LitePal是一款开源的Android数据库框架，采用对象关系映射（ORM）模式，将常用的数据库功能进行封装，可以不用写一行SQL语句就可以完成创建表、增删改查的操作。
相关教程可以参见链接[litepal](https://www.jianshu.com/p/8035eb5da7a2)
```
<?xml version="1.0" encoding="utf-8"?>
<litepal>
    <dbname value="Note"></dbname>
    <version value="1"></version>
    <list>
        <mapping class="com.tomridder.ridder_note.bean.Note"></mapping>
    </list>
</litepal>
```
litePal增删改已经非常简单了，这里就介绍下最复杂的查询。

#### 2、按关键字查询note的实现

```
                String key=starEtSearch.getText().toString();
                notes=querylikeStarNotes(key);
                recycleViewStarNoteAdapter =new RecycleViewStarNoteAdapter(R.layout.star_note,notes);
                recycleViewStarNoteAdapter.setOnItemChildClickListener(StarNoteFragment.this);
                starNotesRecyclerView.setAdapter(recycleViewStarNoteAdapter);   
```

```
    private List<Note> querylikeStarNotes(String key)
    {
        List<Note> notes=DataSupport.where("(title like ? or content like ?) and  star = ? ","%"+key+"%","%"+key+"%",1+"").find(Note.class);
        Log.i("note","note size = "+ notes.size());
        return notes;

    }
```

在querylikeStarNotes函数中查询title content中包含字符串key的notes，再传给recycleViewStarNoteAdapter，starNotesRecyclerView重新setAdapter。

#### 3、上翻下翻查询note
```
                Note note=getTheNextUnStarNote(oldNote);
                noteShowTitle.setText(note.getTitle());
                noteShowContent.setText(note.getContent());
                noteShowTime.setText(DateFormat.format("MM-dd HH:mm:ss",note.getDate()).toString());
                oldNote=note;
```

```
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
```
litePal没有查询上一条数据的API，所以这里还是用了sqlite。

getTheNextUnStarNote函数中用while循环定位到当前note所在的游标位置，找到后跳出循环。

接着游标下移一位，返回所在位置的note。如果当前游标已经在最后一位，则将游标移到第一位，返回note。

接着分别setText即可，不要忘了将返回的note赋值给oldNote，用于下次查询。

## 二、StarNote和 UnStarNote切换的实现

```
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
```
```
    class UnStarChangeReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            List<Note> unStarNotes=QueryUnStarNotes();
            recycleViewUnStarNoteAdapter =new RecycleViewUnStarNoteAdapter(R.layout.unstar_note,unStarNotes);
            recycleViewUnStarNoteAdapter.setOnItemChildClickListener(UnStarNoteFragment.this);
            unStarNotesRecyclerView.setAdapter(recycleViewUnStarNoteAdapter);
        }
    }
```
在从StarNote到UnStarNote的转换中，首先在数据库中更改star的值从1到2。

如果更改成功从当前的StarNoteFragment中的recycleView中移除当前的note。

接着发出一条名字为"com.tomridder.UnStarNote"的广播。

UnStarFragment中会注册这条广播，如果接收到则会刷新UnStarNoteFragment的recycleView。从而完成整个效果。

## 三、备份日记到服务器的实现
#### 1、更改日记同步到服务器端的实现
```
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
```
这里只需要用使用okhttp，将SERVER_URL和参数data(包含oldNote的 title,content,date,star的newNote的title,content,date,star )拼接起来，
作为Request的url参数，最后发起请求即可。
#### 2、 从服务器端恢复日记到本地的实现
```
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
```

```
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
                               Intent intent=new Intent("com.tomridder.StarNote");
                               sendBroadcast(intent);
                               Intent intent2=new Intent("com.tomridder.UnStarNote");
                               sendBroadcast(intent2);
                           }
                       }catch (IOException e)
                       {
                           e.printStackTrace();
                       }
                   }
               }.start();
```

首先向SERVER_URL发起请求，得到response后，在ParseDataWithJsonObject中将resposne的JSON串转成jsonArray，

接着利用for循环取出jsonArray中的jsonObject，转换成note对象，一个个写到数据库中。
```
    public interface Callback1
    {
        void fresh();
    }

```

```
 Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    List<Note> unStarNotes=QueryUnStarNotes();
                    recycleViewUnStarNoteAdapter =new RecycleViewUnStarNoteAdapter(R.layout.unstar_note,unStarNotes);
                    recycleViewUnStarNoteAdapter.setOnItemChildClickListener(UnStarNoteFragment.this);
                    unStarNotesRecyclerView.setAdapter(recycleViewUnStarNoteAdapter);
                    Log.i("Note","Received Un");
                    break;


            }
        }
    };
    @Override
    public void fresh() {
        Message message=new Message();
        message.what=1;
        handler.sendMessage(message);
        Log.i("Note","Send Un ");
    }
```
接下来MainNote Activity和 UnStarFragment ， StarFragment的通信方式我采取了接口回调的方式。

MainNote含有Callback对象的List集合，UnStarFragment ， StarFragment分别集成自Callback接口，并实现fresh（）方法。

最后由于不能在子线程 函数fresh（）中刷新recyclerView，我采用了handler来更改view。

这样最后效果实现。

以上便是我写这个 APP 的具体实现思路，以及踩过的一些坑，记录下来，给大家看看，最后附上这个 APP 的 Github 地址 [Ridder Note](https://github.com/tomridder/RidderNote) 欢迎大家 star 和 fork，如果有什么想法或者建议，非常欢迎大家来讨论

-----
### 猜你喜欢
- [Tom-Keylogger](https://github.com/tomridder/Tom-Keylogger)

