package org.sarge.jove.demo.cube;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.builder.*;
import org.sarge.jove.common.TransientObject;
import org.sarge.jove.control.Button;
import org.sarge.jove.io.*;
import org.sarge.jove.platform.desktop.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RotatingCubeDemo {
	private final AtomicBoolean running = new AtomicBoolean(true); // TODO - toggle handler

	@Bean
	public static DataSource classpath() {
		return new ClasspathDataSource();
	}

	@Bean
	public static DataSource data() {
		return FileDataSource.home(Paths.get("workspace/Demo/Data"));
	}

	@Bean
	CommandLineRunner runner(LogicalDevice dev, FramePresenter presenter, RenderSequence seq, Runnable update, Desktop desktop) {
		return args -> {
			while(running.get()) {
				update.run();
				presenter.render(seq);
				desktop.poll();
			}
			dev.waitIdle();
		};
	}

	@Autowired
	void listener(Window window) {
		window.keyboard().keyboard().bind(Button.handler(() -> running.set(false)));
		// TODO - messy handler -> Button.handler(running); => specialised toggle handler
	}

	@Bean
	static DestructionAwareBeanPostProcessor destroyer() {
		return new DestructionAwareBeanPostProcessor() {
			@Override
			public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
				if(bean instanceof TransientObject obj && !obj.isDestroyed()) {
					obj.destroy();
				}
			}
		};
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		SpringApplication.run(RotatingCubeDemo.class, args);
	}
}
