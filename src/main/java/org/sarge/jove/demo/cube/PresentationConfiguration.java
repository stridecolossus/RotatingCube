package org.sarge.jove.demo.cube;

import java.time.Duration;
import java.util.*;

import org.sarge.jove.common.Colour;
import org.sarge.jove.control.*;
import org.sarge.jove.platform.desktop.Window;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.image.ClearValue.ColourClearValue;
import org.sarge.jove.platform.vulkan.present.*;
import org.sarge.jove.platform.vulkan.present.SwapchainManager.SwapchainConfiguration;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.Attachment.AttachmentType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
class PresentationConfiguration {
	@Bean
	static VulkanSurface surface(Window window, Instance instance, VulkanCoreLibrary library) {
		return new VulkanSurface(window, instance, library);
	}

	@Bean
	static VulkanSurface.Properties properties(VulkanSurface surface, PhysicalDevice device) {
		return surface.properties(device);
	}

	@Bean
	static SwapchainManager swapchain(LogicalDevice dev, VulkanSurface.Properties properties) {
		final var builder = new Swapchain.Builder();

		final SwapchainConfiguration[] configuration = {
			new SurfaceFormatSwapchainConfiguration(VkFormat.B8G8R8A8_UNORM, VkColorSpaceKHR.SRGB_NONLINEAR_KHR),
		};

		return new SwapchainManager(dev, properties, builder, List.of(configuration));
	}

	@Bean
	static Attachment colour(SwapchainManager manager) {
		final var description = AttachmentDescription.colour(manager.swapchain().format());
		final var attachment = new Attachment(AttachmentType.COLOUR, description, manager.views());
		attachment.clear(new ColourClearValue(new Colour(0.3f, 0.3f, 0.3f, 1)));
		return attachment;
	}

	@Bean
	static RenderPass pass(LogicalDevice dev, Attachment colour) {
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
	static RenderTask render(SwapchainManager manager, Framebuffer.Factory framebuffers, FrameComposer composer) {
		return new RenderTask(manager, framebuffers, composer);
	}

	@Bean
	static FrameCounter counter() {
		return new FrameCounter();
	}

	// TODO
	@Bean
	static Frame.Listener watch(FrameCounter counter) {
		return Frame.Listener.periodic(Duration.ofSeconds(1), _ -> System.out.println(counter));
	}

	@Bean
	static RenderLoop loop(RenderTask task, Collection<Frame.Listener> listeners) {
		final var tracker = new Frame.Tracker();		// TODO
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
