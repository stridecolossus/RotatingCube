package org.sarge.jove.demo.cube;

import org.sarge.jove.platform.desktop.Desktop;
import org.sarge.jove.platform.vulkan.api.VulkanLibrary;
import org.sarge.jove.platform.vulkan.common.ValidationLayer;
import org.sarge.jove.platform.vulkan.core.Instance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class VulkanConfiguration {
	@Bean
	public static VulkanLibrary library() {
		return VulkanLibrary.create();
	}

	@Bean
	public static Instance instance(VulkanLibrary lib, Desktop desktop, @Value("application.title") String title) {
		return new Instance.Builder()
				.name(title)
				.extension(VulkanLibrary.EXTENSION_DEBUG_UTILS)
				.extensions(desktop.extensions())
				.layer(ValidationLayer.STANDARD_VALIDATION)
				.build(lib)
				.handler()
				.init()
				.attach();
	}
}
