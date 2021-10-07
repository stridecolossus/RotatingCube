package org.sarge.jove.demo.cube;

import org.sarge.jove.common.Handle;
import org.sarge.jove.platform.vulkan.VkQueueFlag;
import org.sarge.jove.platform.vulkan.api.VulkanLibrary;
import org.sarge.jove.platform.vulkan.common.Queue;
import org.sarge.jove.platform.vulkan.common.ValidationLayer;
import org.sarge.jove.platform.vulkan.core.Instance;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.PhysicalDevice;
import org.sarge.jove.platform.vulkan.core.PhysicalDevice.Selector;
import org.sarge.jove.platform.vulkan.core.Surface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DeviceConfiguration {
	private final Selector graphics = Selector.of(VkQueueFlag.GRAPHICS);
	private final Selector presentation;

	public DeviceConfiguration(Handle surface) {
		presentation = Selector.of(surface);
	}

	@Bean
	public PhysicalDevice physical(Instance instance) {
		return PhysicalDevice
				.devices(instance)
				.filter(graphics)
				.filter(presentation)
				.findAny()
				.orElseThrow(() -> new RuntimeException("No suitable physical device available"));
	}

	@Bean
	public static Surface surface(Handle handle, PhysicalDevice dev) {
		return new Surface(handle, dev);
	}

	@Bean
	public LogicalDevice device(PhysicalDevice dev) {
		return new LogicalDevice.Builder(dev)
				.extension(VulkanLibrary.EXTENSION_SWAP_CHAIN)
				.layer(ValidationLayer.STANDARD_VALIDATION)
				.queue(graphics.family())
				.queue(presentation.family())
				.build();
	}

	@Bean
	public Queue graphics(LogicalDevice dev) {
		return dev.queue(graphics.family());
	}

	@Bean
	public Queue presentation(LogicalDevice dev) {
		return dev.queue(presentation.family());
	}
}
