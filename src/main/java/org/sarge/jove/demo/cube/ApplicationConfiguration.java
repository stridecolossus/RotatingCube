package org.sarge.jove.demo.cube;

import org.sarge.jove.common.Colour;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class ApplicationConfiguration {
	private String title;
	private int frames;
	private int rate;
	private Colour col = Colour.BLACK;
	private long period;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getFrameCount() {
		return frames;
	}

	public void setFrameCount(int frames) {
		this.frames = frames;
	}

	public int getFrameRate() {
		return rate;
	}

	public void setFrameRate(int rate) {
		this.rate = rate;
	}

	public Colour getBackground() {
		return col;
	}

	public void setBackground(float[] col) {
		this.col = Colour.of(col);
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}
}
