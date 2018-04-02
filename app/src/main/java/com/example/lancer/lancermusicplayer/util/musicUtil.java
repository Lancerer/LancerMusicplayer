package com.example.lancer.lancermusicplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.lancer.lancermusicplayer.bean.Info;

import java.util.ArrayList;


public class musicUtil {
    Info info;//音乐信息类
    public ArrayList<Info> InfoList;//将音乐信息填充到该集合中

    public ArrayList<Info> getMusicInfo(Context context) {
        InfoList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null
                , null);
        if (cursor != null && cursor.getCount() > 0) {
            Info info = null;
            while (cursor.moveToNext()) {
                info = new Info();
                info.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                info.setTitle((cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
                info.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                //  info.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                InfoList.add(info);
            }
        }

        return InfoList;
    }
}
