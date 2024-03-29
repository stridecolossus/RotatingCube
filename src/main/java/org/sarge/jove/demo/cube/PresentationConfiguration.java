package org.sarge.jove.demo.cube;

import java.util.*;

import org.sarge.jove.common.Handle;
import org.sarge.jove.control.Frame;
import org.sarge.jove.platform.vulkan.VkImageUsageFlag;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.core.RenderLoop;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
class PresentationConfiguration {
	@Autowired private ApplicationConfiguration cfg;

	@Bean
	public static Surface surface(Handle surface, PhysicalDevice dev) {
		return new Surface(surface, dev);
	}

	@Bean
	SwapchainAdapter adapter(Surface surface, RenderPass pass) {
		final var swapchain = new Swapchain.Builder(surface)
				.count(cfg.getFrameCount())
				.clear(cfg.getBackground())
				.usage(VkImageUsageFlag.TRANSFER_SRC);

		return new SwapchainAdapter(swapchain, pass, List.of());
	}

	@Bean
	public static RenderPass pass(LogicalDevice dev) {
		final var surfaceFormat = Surface.defaultSurfaceFormat();
		final Attachment attachment = Attachment.colour(surfaceFormat.format);
		return new Subpass().colour(attachment).create(dev);
	}

	@Bean
	static FrameComposer composer(@Qualifier("graphics") Command.Pool pool, Command.Sequence sequence) {
		return new FrameComposer(pool, sequence);
	}

	@Bean
	VulkanRenderTask render(FrameComposer composer, SwapchainAdapter swapchain, LogicalDevice dev) {
		final VulkanFrame[] frames = VulkanFrame.array(cfg.getFrameCount(), () -> DefaultVulkanFrame.create(dev));
		return new VulkanRenderTask(composer, swapchain, frames);
	}

	@Bean
	public RenderLoop loop(VulkanRenderTask task, Collection<Frame.Listener> listeners, ApplicationConfiguration cfg) {
		final var loop = new RenderLoop();
		loop.rate(cfg.getFrameRate());
		loop.start(task::render);
		for(var listener : listeners) {
			loop.add(listener);
		}
		return loop;
	}
}
