package org.sarge.jove.demo.cube;

import org.sarge.jove.foreign.DefaultRegistry;
import org.sarge.jove.platform.desktop.Desktop;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.springframework.context.annotation.*;

@Configuration
class VulkanConfiguration {
	@Bean
	static VulkanCoreLibrary library() {
		return Vulkan.create();
	}

	@Bean
	static Instance instance(VulkanCoreLibrary lib, Desktop desktop) { // , ApplicationConfiguration cfg) {
		final var instance = new Instance.Builder()
				.name("Rotating Cube Demo") // cfg.getTitle())
				.extension(DiagnosticHandler.EXTENSION)
				.extensions(desktop.extensions())
				.layer(DiagnosticHandler.STANDARD_VALIDATION)
				.build(lib);

		return instance;
	}

	@Bean
	static DiagnosticHandler diagnostics(Instance instance) {
		return new DiagnosticHandler.Builder().build(instance, DefaultRegistry.create());
	}

	@Bean
	static Allocator allocator(LogicalDevice dev, PhysicalDevice physical) {
		final var types = MemoryType.enumerate(physical.memory());
		return new Allocator(dev, types);
	}
}
