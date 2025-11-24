package org.sarge.jove.demo.cube;

import java.time.Duration;
import java.util.*;

import org.sarge.jove.common.Colour;
import org.sarge.jove.control.*;
import org.sarge.jove.platform.desktop.Window;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.image.ClearValue.ColourClearValue;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.context.annotation.*;

@Configuration
class PresentationConfiguration {
//	@Autowired private ApplicationConfiguration cfg;

	@Bean
	static VulkanSurface surface(Window window, Instance instance, VulkanCoreLibrary lib) {
		return new VulkanSurface(window, instance, lib);
	}

	@Bean
	static VulkanSurface.Properties properties(VulkanSurface surface, PhysicalDevice device) {
		return surface.properties(device);
	}

	@Bean
	static SwapchainFactory swapchain(LogicalDevice dev, VulkanSurface.Properties properties) {
		final var builder = new Swapchain.Builder()
				.init(properties.capabilities())
				.format(new SurfaceFormatWrapper(VkFormat.B8G8R8A8_UNORM, VkColorSpaceKHR.SRGB_NONLINEAR_KHR));

		return new SwapchainFactory(dev, properties, builder, List.of());
	}

	@Bean
	static Attachment colour(SwapchainFactory swapchain) {
		return Attachment.colour(swapchain.swapchain().format());
	}

	@Bean
	static RenderPass pass(LogicalDevice dev, Attachment colour) {
		final Subpass subpass = new Subpass.Builder()
				.colour(colour)
				.build();

		final var source = new Dependency.Properties(Dependency.VK_SUBPASS_EXTERNAL, Set.of(VkPipelineStage.COLOR_ATTACHMENT_OUTPUT), Set.of());
		final var destination = new Dependency.Properties(subpass, Set.of(VkPipelineStage.COLOR_ATTACHMENT_OUTPUT), Set.of(VkAccess.COLOR_ATTACHMENT_WRITE));
		final var dependency = new Dependency(source, destination, Set.of());

		return new RenderPass.Builder()
				.add(subpass)
				.dependency(dependency)
				.build(dev);
	}

	@Bean
	static Framebuffer.Group framebuffers(SwapchainFactory swapchain, RenderPass pass, Attachment colour) {
		final var group = new Framebuffer.Group(swapchain.swapchain(), pass, null);
		group.clear(colour, new ColourClearValue(new Colour(0.3f, 0.3f, 0.3f, 1)));
		return group;
	}

	@Bean
	static RenderTask render(FrameComposer composer, SwapchainFactory swapchain, Framebuffer.Group framebuffers) {
		return new RenderTask(swapchain, framebuffers, composer);
	}

	@Bean
	static FrameCounter counter() {
		return new FrameCounter();
	}

	@Bean
	static Frame.Listener watch(FrameCounter counter) {
		return Frame.Listener.periodic(Duration.ofSeconds(1), _ -> System.out.println(counter));
	}

	@Bean
	static Frame.Listener terminate() {
		return Frame.Listener.periodic(Duration.ofSeconds(5), _ -> System.exit(0));
	}

	@Bean
	static RenderLoop loop(RenderTask task, Collection<Frame.Listener> listeners) { //, ApplicationConfiguration cfg) {
		final var tracker = new Frame.Tracker();
		for(var listener : listeners) {
			tracker.add(listener);
		}

		final var loop = new RenderLoop(task, tracker);
		loop.start();

		return loop;
	}
}
