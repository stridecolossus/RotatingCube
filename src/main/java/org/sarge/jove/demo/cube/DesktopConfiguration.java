package org.sarge.jove.demo.cube;

import org.sarge.jove.platform.desktop.*;
import org.springframework.context.annotation.*;

@Configuration
class DesktopConfiguration {
	@Bean
	public static Desktop desktop() {
		final Desktop desktop = Desktop.create();
		if(!desktop.isVulkanSupported()) throw new RuntimeException("Vulkan not supported");
		return desktop;
	}

	@Bean
	public static Window window(Desktop desktop, ApplicationConfiguration cfg) {
		return new Window.Builder()
				.title(cfg.getTitle())
				.size(cfg.getDimensions())
				.hint(Window.Hint.CLIENT_API, 0)
				.hint(Window.Hint.RESIZABLE, 0)
				.hint(Window.Hint.VISIBLE, 1)
				.build(desktop);
	}

//	@Autowired
//	void close(Window window) {
//		window.listener(WindowListener.Type.CLOSED, (type, state) -> System.exit(0));
//	}
//
//	@Autowired
//	void pause(RenderLoop loop, Window window) {
//		final WindowListener minimised = (__, state) -> {
//			if(state) {
//				loop.pause();
//			}
//			else {
//				loop.restart();
//			}
//		};
//		window.listener(WindowListener.Type.ICONIFIED, minimised);
//	}
}
