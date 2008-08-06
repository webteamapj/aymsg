package org.if_itb.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView
{
	private int[] imageIds;
	private int currentIndex;
	private long lastUpdateTime;
	private long interval = 300;
	private volatile boolean active;

	public AnimatedImageView(Context context, int[] imageIds)
	{
		super(context);
		this.imageIds = imageIds;
		this.setImageResource(imageIds[0]);
		this.start();
	}

	private void updateImage()
	{
		long time = System.currentTimeMillis();
		if (time - this.lastUpdateTime > this.interval)
		{
			this.lastUpdateTime = time;
			this.currentIndex = (this.currentIndex + 1) % this.imageIds.length;
			this.setImageResource(this.imageIds[this.currentIndex]);
			
		}
	}

	public void start()
	{
		this.active = true;
		this.setVisibility(VISIBLE);
	}

	public void stop()
	{
		this.active = false;
		this.setVisibility(INVISIBLE);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		this.updateImage();
		if (this.active)
		{
			this.invalidate();
		}
	}
}