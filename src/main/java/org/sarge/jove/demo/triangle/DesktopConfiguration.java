package org.sarge.jove.demo.triangle;

import org.sarge.jove.common.Dimensions;
import org.sarge.jove.common.Handle;
import org.sarge.jove.platform.desktop.Desktop;
import org.sarge.jove.platform.desktop.Window;
import org.sarge.jove.platform.vulkan.core.Instance;
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
	public static Window window(Desktop desktop) {
		return new Window.Builder()
				.title("TriangleDemo")
				.size(new Dimensions(1024, 768))
				.property(Window.Property.DISABLE_OPENGL)
				.build(desktop);
	}

	@Bean("surfaceHandle")
	public static Handle surface(Instance instance, Window window) {
		return window.surface(instance.handle());
	}
}
