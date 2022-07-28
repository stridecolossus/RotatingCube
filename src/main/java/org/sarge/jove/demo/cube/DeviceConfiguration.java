package org.sarge.jove.demo.cube;

import org.sarge.jove.common.Handle;
import org.sarge.jove.platform.vulkan.VkQueueFlag;
import org.sarge.jove.platform.vulkan.common.Queue;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice.RequiredQueue;
import org.sarge.jove.platform.vulkan.core.PhysicalDevice.Selector;
import org.sarge.jove.platform.vulkan.memory.*;
import org.sarge.jove.platform.vulkan.util.ValidationLayer;
import org.springframework.context.annotation.*;

@Configuration
class DeviceConfiguration {
	private final Selector graphics = Selector.of(VkQueueFlag.GRAPHICS);
	private final Selector presentation;

	public DeviceConfiguration(Handle surface) {
		presentation = Selector.of(surface);
	}

	@Bean
	public PhysicalDevice physical(Instance instance) {
		return new PhysicalDevice.Enumerator(instance)
				.devices()
				.filter(graphics)
				.filter(presentation)
				.findAny()
				.orElseThrow(() -> new RuntimeException("No suitable physical device available"));
	}

	@Bean
	public LogicalDevice device(PhysicalDevice dev) {
		return new LogicalDevice.Builder(dev)
				.extension(VulkanLibrary.EXTENSION_SWAP_CHAIN)
				.layer(ValidationLayer.STANDARD_VALIDATION)
				.queue(new RequiredQueue(graphics.select(dev)))
				.queue(new RequiredQueue(presentation.select(dev)))
				.build();
	}

	private static Command.Pool pool(LogicalDevice dev, Selector selector) {
		final Queue.Family family = selector.select(dev.parent());
		final Queue queue = dev.queue(family);
		return Command.Pool.create(dev, queue);
	}

	@Bean
	public Command.Pool graphics(LogicalDevice dev) {
		return pool(dev, graphics);
	}

	@Bean
	public Command.Pool presentation(LogicalDevice dev) {
		return pool(dev, presentation);
	}

	@Bean
	public static AllocationService service(LogicalDevice dev) {
		final MemorySelector selector = MemorySelector.create(dev);
		final Allocator allocator = new DefaultAllocator(dev);
		return new AllocationService(selector, allocator);
	}
}
