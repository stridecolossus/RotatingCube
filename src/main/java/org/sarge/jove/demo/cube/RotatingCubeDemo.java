package org.sarge.jove.demo.cube;

import org.apache.commons.lang3.builder.*;
import org.sarge.jove.common.TransientNativeObject;
import org.sarge.jove.io.*;
import org.sarge.jove.platform.vulkan.VkPipelineStage;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.render.Swapchain;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RotatingCubeDemo {
	@Bean
	public static DataSource classpath() {
		return new ClasspathDataSource();
	}

	@Bean
	public static DataSource data() {
		return new FileDataSource("../Data");
	}

	@Bean
	static CommandLineRunner runner(LogicalDevice dev, Swapchain swapchain, Command.Buffer buffer) {
		return args -> {

			final Semaphore available = Semaphore.create(dev);
			final int index = swapchain.acquire(available, null);

			final Semaphore ready = Semaphore.create(dev);
			new Work.Builder(buffer.pool())
					.add(buffer)
					.wait(available, VkPipelineStage.TOP_OF_PIPE)
					.signal(ready)
					.build()
					.submit(null);

			swapchain.present(buffer.pool().queue(), index, ready);

			Thread.sleep(1000L);

			dev.waitIdle();

			available.destroy();
			ready.destroy();
		};
	}

//	@Bean
//	public static Runnable update(Matrix matrix, VulkanBuffer uniform, ApplicationConfiguration cfg) {
//		final long period = cfg.getPeriod();
//		final long start = System.currentTimeMillis();
//		return () -> {
//			final long time = System.currentTimeMillis() - start;
//			final float angle = (time % period) * MathsUtil.TWO_PI / period;
//			final Matrix h = Matrix.rotation(Vector.Y, angle);
//			final Matrix v = Matrix.rotation(Vector.X, MathsUtil.toRadians(30));
//			final Matrix model = h.multiply(v);
//			final Matrix m = matrix.multiply(model);
//			uniform.load(m);
//		};
//	}
//
//	@Bean
//	public static KeyListener listener(Window window, Application app) {
//		// Create key listener
//		final KeyListener listener = (ptr, key, scancode, action, mods) -> {
//		    if(key == 256) {
//		    	app.stop();
//		    }
//		};
//
//		// Register listener
//		final Desktop desktop = window.desktop();
//		desktop.library().glfwSetKeyCallback(window.handle(), listener);
//
//		return listener;
//	}

	@Bean
	static DestructionAwareBeanPostProcessor destroyer() {
		return new DestructionAwareBeanPostProcessor() {
			@Override
			public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
				if(bean instanceof TransientNativeObject obj) {
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
