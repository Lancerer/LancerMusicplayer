package com.example.lancer.lancermusicplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lancer.lancermusicplayer.bean.Info;
import com.example.lancer.lancermusicplayer.util.musicUtil;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    @InjectView(R.id.musicListView)
    ListView musicListView;  //lsitview展示数据的
    @InjectView(R.id.seekBar)
    SeekBar mSeekBar;    //seekbar 进度条显示
    @InjectView(R.id.current_time_tv)   //播放时的时间
            TextView mCurrentTimeTv;
    @InjectView(R.id.total_time_tv)
    TextView mTotalTimeTv;    //歌曲总时间
    @InjectView(R.id.previous)
    ImageView up;     //上一曲按钮
    @InjectView(R.id.play_pause)
    ImageView play;    //播放暂停按钮
    @InjectView(R.id.next)
    ImageView next;    //下一曲按钮
    @InjectView(R.id.now)
    TextView now;       //当前播放歌曲名称
    private musicUtil util;  //音乐工具类，用于获取手机上的音乐
    private ArrayList<Info> musicList;  //装了音乐信息的listView
    private MusicAdapter adapter;
    private int currentposition;    //当前音乐播放位置
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            int progress = mediaPlayer.getCurrentPosition();
            mSeekBar.setProgress(progress);
            mCurrentTimeTv.setText(parseTime(progress));
            // 继续定时发送数据
            updateProgress();
            return true;
        }
    });

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);//初始化数据
        mediaPlayer = new MediaPlayer();
        up.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);//监听音乐播放完毕事件，自动下一曲
        getWindow().setEnterTransition(new Explode().setDuration(1000));//转场动画
        getWindow().setExitTransition(new Explode().setDuration(1000));
        initData();
    }


    /**
     * 初始化数据
     */
    private void initData() {
        util = new musicUtil();
        musicList = new ArrayList<>();
        musicList = util.getMusicInfo(this);
        adapter = new MusicAdapter();
        musicListView.setAdapter(adapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentposition = position;     //获取当前点击条目的位置
                changeMusic(currentposition);   //切歌
                play.setImageResource(R.drawable.pause);
                String title = musicList.get(currentposition).getTitle();
                now.setText(title);       //展示当前播放的歌名

            /*    setNotification(currentposition);*/
            }
        });

    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    /**
     * 上一曲下一曲点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = new ViewHolder();
        if (view.getId() == R.id.previous) {//上一曲
            changeMusic(--currentposition);
            play.setImageResource(R.drawable.pause);
            String title = musicList.get(currentposition).getTitle();
            now.setText(title); //展示上一曲的歌名
        } else if (view.getId() == R.id.play_pause) {//暂停/播放
            // 首次点击播放按钮，默认播放第0首
            if (mediaPlayer == null) {
                changeMusic(0);
                String title = musicList.get(currentposition+1).getTitle();
                now.setText(title);
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }
            }
        } else if (view.getId() == R.id.next) {// 下一首
            changeMusic(++currentposition);
            play.setImageResource(R.drawable.pause);
            String title = musicList.get(currentposition).getTitle();
            now.setText(title);//展示下一首的歌名

        }
    }

    private void changeMusic(int position) {
        if (position < 0) {
            currentposition = position = musicList.size() - 1;
        } else if (position > musicList.size() - 1) {
            currentposition = position = 0;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            // 切歌之前先重置，释放掉之前的资源
            mediaPlayer.reset();
            // 设置播放源
            mediaPlayer.setDataSource(musicList.get(position).getUrl());
            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();

            // 开始播放
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSeekBar.setProgress(0);//将进度条初始化
        mSeekBar.setMax(mediaPlayer.getDuration());//设置进度条最大值为歌曲总时间
        mTotalTimeTv.setText(parseTime(mediaPlayer.getDuration()));//显示歌曲总时长

        updateProgress();//更新进度条
    }

    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        Message msg = Message.obtain();// 获取一个现成的消息
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = mediaPlayer.getCurrentPosition();
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }

    /**
     * 时间格式化
     *
     * @param oldTime
     * @return
     */
    private String parseTime(int oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");// 时间格式
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }

    private static final int INTERNAL_TIME = 1000;// 音乐进度间隔时间

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    // 当手停止拖拽进度条时执行该方法
    // 获取拖拽进度
    // 将进度对应设置给MediaPlayer
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        mediaPlayer.seekTo(progress);
    }

    //自动播放下一曲
    @Override
    public void onCompletion(MediaPlayer mp) {
        changeMusic(++currentposition);
        String title = musicList.get(currentposition).getTitle();
        now.setText(title);//展示下一首的歌名
    }

    public Bitmap getArtAlbum(long audioId) {
        String str = "content://media/external/audio/media/" + audioId + "/albumart";
        Uri uri = Uri.parse(str);
        ParcelFileDescriptor pfd = null;
        try {
            pfd = this.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            return null;
        }
        Bitmap bm;
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            bm = BitmapFactory.decodeFileDescriptor(fd);
            return bm;
        }
        return null;
    }

    //音乐播放器的适配器
    public class MusicAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Info getItem(int position) {
            return musicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.music_item, null);
                viewHolder = new ViewHolder();
                viewHolder.video_imageView = convertView.findViewById(R.id.video_imageView);
                viewHolder.video_title = convertView.findViewById(R.id.video_title);
                viewHolder.video_singer = convertView.findViewById(R.id.video_singer);
               /* viewHolder.video_duration = convertView.findViewById(R.id.video_duration);*/
                //  viewHolder.video_size = convertView.findViewById(R.id.video_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Info item = getItem(position);
            viewHolder.video_singer.setText("歌手:" + item.getArtist());
            // viewHolder.video_size.setText((int) item.getSize());
            viewHolder.video_title.setText("歌名:" + item.getTitle());
           /* viewHolder.video_duration.setText(item.getDuration());*/
            if (getArtAlbum(item.getAbulm_id()) == null) {
                viewHolder.video_imageView.setImageResource(R.drawable.music);
            } else {
                viewHolder.video_imageView.setImageBitmap(getArtAlbum(item.getAbulm_id()));
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView video_imageView;
        TextView video_title;
        TextView video_singer;
      /*  TextView video_duration;
        TextView video_size;*/
    }
}
