package org.sarge.jove.demo.cube;
import java.util.Collection;
import java.util.function.Consumer;

import org.sarge.jove.common.TransientObject;
import org.sarge.jove.control.Button.ButtonEvent;
import org.sarge.jove.control.RenderLoop;
import org.sarge.jove.platform.desktop.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class RotatingCubeDemo {
	@Autowired
	void listener(Window window, RenderLoop loop, LogicalDevice device) {
		final Consumer<ButtonEvent> stop = _ -> {
			loop.stop();
			device.waitIdle();
			System.exit(0);
		};
		window.keyboard().bind(stop);
	}

	@Bean
	static DestructionAwareBeanPostProcessor destroyer() {
		return new DestructionAwareBeanPostProcessor() {
			@Override
			public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
				switch(bean) {
    				case TransientObject object -> destroy(object);

    				case TransientObject[] array -> {
    					for(var object : array) {
    						destroy(object);
    					}
    				}

    				case Collection<?> collection -> {
    					for(var object : collection) {
    						if(object instanceof TransientObject trans) {
    							destroy(trans);
    						}
    					}
    				}

    				default -> {
    					// Ignored
    				}
				}
			}

			private static void destroy(TransientObject obj) {
				if(!obj.isDestroyed()) {
					obj.destroy();
				}
			}
		};
	}

	public static void main(String[] args) throws InterruptedException {
		final var context = SpringApplication.run(RotatingCubeDemo.class, args);

		final Desktop desktop = context.getBean(Desktop.class);
		while(true) {
			desktop.poll();
		}
	}
}
