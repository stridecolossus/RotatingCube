package org.sarge.jove.demo.cube;

import java.time.Duration;
import java.util.Collection;

import org.sarge.jove.common.TransientObject;
import org.sarge.jove.control.Frame;
import org.sarge.jove.platform.desktop.Desktop;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class RotatingCubeDemo {

//	@Autowired
//	void listener(Window window) {
//		window.keyboard().keyboard().bind(button -> System.exit(0));
//	}

	// TODO
	@Bean
	static Frame.Listener terminate() {
		return Frame.Listener.periodic(Duration.ofSeconds(5), _ -> System.exit(0));
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

//	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		final var context = SpringApplication.run(RotatingCubeDemo.class, args);

		final Desktop desktop = context.getBean(Desktop.class);
		while(true) {
			desktop.poll();
		}
	}
}
