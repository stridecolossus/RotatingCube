package org.sarge.jove.demo.cube;

import org.sarge.jove.platform.vulkan.VkQueueFlags;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice.Builder.RequiredQueue;
import org.sarge.jove.platform.vulkan.core.PhysicalDevice.Selector;
import org.sarge.jove.platform.vulkan.core.WorkQueue.Family;
import org.sarge.jove.platform.vulkan.present.*;
import org.springframework.context.annotation.*;

@Configuration
@DependsOn("diagnostics")
class DeviceConfiguration {
	private final Selector graphics;
	private final Selector presentation;

	public DeviceConfiguration(VulkanSurface surface) {
		this.graphics = Selector.queue(VkQueueFlags.GRAPHICS, VkQueueFlags.TRANSFER);
		this.presentation = new Selector(surface::isPresentationSupported);
	}

	@Bean
	public PhysicalDevice physical(Instance instance) {
		return PhysicalDevice.enumerate(instance)
				.filter(graphics)
				.filter(presentation)
				.findAny()
				.orElseThrow(() -> new RuntimeException("No suitable physical device available"));
	}

	@Bean
	public LogicalDevice device(PhysicalDevice device, VulkanCoreLibrary library) {
		return new LogicalDevice.Builder(device)
				.extension(Swapchain.EXTENSION)
				.layer(DiagnosticHandler.STANDARD_VALIDATION)
				.queue(new RequiredQueue(graphics.family(device)))
				.build(library);
	}

	private static Command.Pool pool(LogicalDevice device, PhysicalDevice physical, Selector selector) {
		final Family family = selector.family(physical);
		final WorkQueue queue = device.queue(family);
		return Command.Pool.create(device, queue);
	}

	@Bean
	public Command.Pool graphics(LogicalDevice dev, PhysicalDevice physical) {
		return pool(dev, physical, graphics);
	}

	@Bean
	public Command.Pool presentation(LogicalDevice dev, PhysicalDevice physical) {
		return pool(dev, physical, presentation);
	}
}
