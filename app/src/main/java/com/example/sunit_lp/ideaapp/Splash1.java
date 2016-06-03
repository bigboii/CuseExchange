package com.example.sunit_lp.ideaapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash1 extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash2);
		/*ourSong = MediaPlayer.create(Splash.this, R.raw.song_back);
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean music =getPrefs.getBoolean("checkbox", true);
		if(music == true)
		ourSong.start();*/
		Thread timer = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				finally
				{
					Intent intent = new Intent(Splash1.this,MainActivity.class);
					startActivity(intent);

				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		//ourSong.release();
		finish();
	}
}
