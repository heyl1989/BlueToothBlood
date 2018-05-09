package com.blue.blueapplication.utils;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundManager {

	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private AudioManager mAudioManager;
	private Context mContext;

	public void init(Context context) {
		mContext = context;
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		mSoundPoolMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public void addSound(int index, int id) {
		mSoundPoolMap.put(index, mSoundPool.load(mContext, id, 1));
	}

	public void playSound(int index) {
		float streamVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_ALARM);
		streamVolume = streamVolume
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (mSoundPoolMap.containsKey(index)) {
			mSoundPool.play(mSoundPoolMap.get(index), streamVolume,
					streamVolume, 1, 0, 1f);
		}
	}

}
