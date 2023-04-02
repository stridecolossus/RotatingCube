package org.sarge.jove.demo.cube;

import java.time.Duration;

import org.sarge.jove.common.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class ApplicationConfiguration {
	private String title;
	private int width, height;
	private int frames;
	private int rate;
	private Colour col = Colour.BLACK;
	private Duration period;
	private int instances = 1;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Dimensions getDimensions() {
		return new Dimensions(width, height);
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

	public Duration getPeriod() {
		return period;
	}

	public void setPeriod(Duration period) {
		this.period = period;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}
}
