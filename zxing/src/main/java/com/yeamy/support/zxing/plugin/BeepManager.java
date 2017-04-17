package com.yeamy.support.zxing.plugin;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public final class BeepManager {

	private SoundPool soundPool;
	private float volume = -1;
	private int soundID;
	private int resId;

	/**
	 * @param resId the resource ID of beep
	 */
	public BeepManager(int resId) {
		this.resId = resId;
	}

	public void initVolume(Context context) {
		if (volume == -1) {
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			volume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.5f;
		}
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	@TargetApi(21)
	@SuppressWarnings("deprecation")
	private SoundPool buildPlayer(Context context) {
		initVolume(context);
		SoundPool soundPool;
		if (Build.VERSION.SDK_INT >= 21) {
			SoundPool.Builder builder = new SoundPool.Builder();
			builder.setMaxStreams(1);
			AudioAttributes attrs = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC)
					.build();
			builder.setAudioAttributes(attrs);
			soundPool = builder.build();
		} else {
			soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		}
		soundID = soundPool.load(context, resId, 1);

		return soundPool;
	}

	public void onResume(Context context) {
		if (soundPool == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
			// so we now play on the music stream.
			// activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			soundPool = buildPlayer(context);
		}
	}

	public void onPause() {
		close();
	}

	public void play() {
		if (soundPool != null) {
			soundPool.play(soundID, volume, volume, 1, 0, 1);
		}
	}

	public void close() {
		if (soundPool != null) {
			soundPool.release();
			soundPool = null;
		}
	}

}
