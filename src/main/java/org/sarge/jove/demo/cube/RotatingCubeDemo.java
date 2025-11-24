package org.sarge.jove.demo.cube;

import java.util.Collection;

import org.sarge.jove.common.TransientObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class RotatingCubeDemo {
//	@Autowired private LogicalDevice dev;

//	@Bean
//	static CommandLineRunner runner(Desktop desktop) {
//		return args -> {
//			while(true) {
//				desktop.poll();
//			}
//		};
//	}

//	@jakarta.annotation.PreDestroy
//	void destroy() {
//		loop.stop();
//		dev.waitIdle();
//	}

//	@Autowired
//	void screenshot(Window window, Swapchain swapchain, AllocationService allocator, Command.Pool graphics) {
//		final Consumer<Button<?>> task = button -> {
//			if(button.action() != Button.Action.PRESS) {
//				return;
//			}
//
//			final CaptureTask helper = new CaptureTask(allocator, graphics);
//			final Image screenshot = helper.capture(swapchain);
//			System.out.println(screenshot);
////			System.exit(0);
//		};
//		window.keyboard().keyboard().bind(task::accept);
//	}

//	@Autowired
//	void listener(Window window) {
//		window.keyboard().keyboard().bind(button -> System.exit(0));
//	}

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

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(RotatingCubeDemo.class, args);
	}
}
