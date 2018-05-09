package com.blue.blueapplication.utils;

import android.content.Context;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by jucyzhang on 4/2/14.
 */
public class PicassoFunctions {
  private volatile static Picasso picasso;

  public static final Picasso getPicasso(Context context) {
    if (picasso == null) {
      synchronized (PicassoFunctions.class) {
        if (picasso == null) {
          context = context.getApplicationContext();
          Picasso.Builder builder = new Picasso.Builder(context);
          builder.downloader(new OkHttpDownloader(context));
          picasso = builder.build();
        }
      }
    }
    return picasso;
  }
}
