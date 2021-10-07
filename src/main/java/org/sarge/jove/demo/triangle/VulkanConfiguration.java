package org.sarge.jove.demo.triangle;

import org.sarge.jove.platform.desktop.Desktop;
import org.sarge.jove.platform.vulkan.api.VulkanLibrary;
import org.sarge.jove.platform.vulkan.common.ValidationLayer;
import org.sarge.jove.platform.vulkan.core.Instance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class VulkanConfiguration {
	@Bean
	public static VulkanLibrary library() {
		return VulkanLibrary.create();
	}

	@Bean
	public static Instance instance(VulkanLibrary lib, Desktop desktop) {
		return new Instance.Builder()
				.name("TriangleDemo")
				.extension(VulkanLibrary.EXTENSION_DEBUG_UTILS)
				.extensions(desktop.extensions())
				.layer(ValidationLayer.STANDARD_VALIDATION)
				.build(lib)
				.handler()
				.init()
				.attach();
	}
}
