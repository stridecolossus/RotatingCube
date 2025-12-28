package org.sarge.jove.demo.cube;

import java.util.*;
import java.util.function.Supplier;

import org.sarge.jove.common.Colour;
import org.sarge.jove.control.*;
import org.sarge.jove.platform.desktop.Window;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.present.*;
import org.sarge.jove.platform.vulkan.present.ImageCountSwapchainConfiguration.Policy;
import org.sarge.jove.platform.vulkan.present.SwapchainManager.SwapchainConfiguration;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
class PresentationConfiguration {
	@Bean
	static VulkanSurface surface(Window window, Instance instance, VulkanCoreLibrary library) {
		return new VulkanSurface(window, instance, library);
	}

	@Bean
	static Supplier<VulkanSurface.Properties> properties(VulkanSurface surface, PhysicalDevice device) {
		return surface.properties(device);
	}

	@Bean
	static SwapchainManager swapchain(LogicalDevice device, Supplier<VulkanSurface.Properties> properties) {
		final var builder = new Swapchain.Builder();

		final SwapchainConfiguration[] configuration = {
			new ImageCountSwapchainConfiguration(Policy.MIN),
			new SurfaceFormatSwapchainConfiguration(VkFormat.B8G8R8A8_UNORM, VkColorSpaceKHR.SRGB_NONLINEAR_KHR),
			new ExtentSwapchainConfiguration(),
		};

		return new SwapchainManager(device, properties, builder, List.of(configuration));
	}

	@Bean
	static ColourAttachment colour(SwapchainManager manager) {
		final var attachment = new ColourAttachment(AttachmentDescription.colour(), manager::swapchain);
		attachment.clear(new Colour(0.3f, 0.3f, 0.3f, 1));
		return attachment;
	}

	@Bean
	static RenderPass pass(LogicalDevice dev, ColourAttachment colour) {
		final Subpass subpass = new Subpass(Set.of(), List.of(colour.reference()));
		final var source = new Dependency.Properties(Dependency.VK_SUBPASS_EXTERNAL, Set.of(VkPipelineStageFlags.COLOR_ATTACHMENT_OUTPUT), Set.of());
		final var destination = new Dependency.Properties(subpass, Set.of(VkPipelineStageFlags.COLOR_ATTACHMENT_OUTPUT), Set.of(VkAccessFlags.COLOR_ATTACHMENT_WRITE));
		final var dependency = new Dependency(source, destination, Set.of());

		return new RenderPass.Builder()
				.add(subpass)
				.dependency(dependency)
				.build(dev);
	}

	@Bean
	static Framebuffer.Factory framebuffers(RenderPass pass) {
		return new Framebuffer.Factory(pass);
	}

	@Bean
	static FrameIterator iterator(LogicalDevice device) {
		return new FrameIterator(device, 2);
	}

	@Bean
	static RenderTask render(SwapchainManager manager, Framebuffer.Factory framebuffers, FrameComposer composer, FrameIterator iterator) {
		return new RenderTask(manager, framebuffers, composer, iterator);
	}

	@Bean
	static FrameCounter counter() {
		return new FrameCounter();
	}

	@Bean
	static RenderLoop loop(RenderTask task, Collection<Frame.Listener> listeners) {
		final var tracker = new Frame.Tracker();
		for(var listener : listeners) {
			tracker.add(listener);
		}
		return new RenderLoop(task, tracker);
	}

	@Bean
	static CommandLineRunner start(RenderLoop loop) {
		return _ -> loop.start();
	}
}
