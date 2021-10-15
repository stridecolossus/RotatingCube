package org.sarge.jove.demo.cube;

import org.sarge.jove.common.Dimensions;
import org.sarge.jove.common.Handle;
import org.sarge.jove.platform.desktop.Desktop;
import org.sarge.jove.platform.desktop.Window;
import org.sarge.jove.platform.vulkan.core.Instance;
import org.sarge.jove.platform.vulkan.core.Surface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
				.size(new Dimensions(1024, 768))
				.property(Window.Property.DISABLE_OPENGL)
				.build(desktop);
	}

	@Bean
	public static Surface surface(Instance instance, Window window) {
		final Handle handle = window.surface(instance.handle());
		return new Surface(handle, instance);
	}
}
