package org.sarge.jove.demo.cube;

import java.nio.file.Paths;
import java.util.Collection;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.builder.*;
import org.sarge.jove.common.TransientObject;
import org.sarge.jove.io.*;
import org.sarge.jove.platform.desktop.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RotatingCubeDemo {
	@Autowired private LogicalDevice dev;

	private final RenderLoop loop = new RenderLoop();

	@Bean
	public static DataSource classpath() {
		return new ClasspathDataSource();
	}

	@Bean
	public static DataSource data() {
		return FileDataSource.home(Paths.get("workspace/Demo/Data"));
	}

	@Bean
	CommandLineRunner runner(Desktop desktop) {
		return args -> {
			while(loop.isRunning()) {
				desktop.poll();
				Thread.sleep(50);
			}
		};
	}

	@Bean("render-task")
	static Runnable render(FrameProcessor presenter, RenderSequence seq, Collection<FrameCounter.Listener> listeners, Runnable animation) {
		final Runnable task = () -> {
			presenter.next().render(seq);
			animation.run(); // TODO
		};
		final FrameCounter counter = new FrameCounter(task);
		listeners.forEach(counter::add);
//		counter.add((time, elapsed) -> System.out.println(counter));
		return counter;
	}

	@Autowired
	void start(@Qualifier("render-task") Runnable render) {
		loop.start(render);
	}

	@PreDestroy
	void destroy() {
		loop.close();
		dev.waitIdle();
	}

	@SuppressWarnings("static-method")
	@Autowired
	void listener(Window window, ApplicationContext ctx) {
		window.keyboard().keyboard().bind(button -> SpringApplication.exit(ctx));
		//Button.handler(loop::stop));
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
