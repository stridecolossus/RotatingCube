package org.sarge.jove.demo.cube;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import org.sarge.jove.common.Handle;
import org.sarge.jove.control.FrameListener;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.RenderLoop;
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
	public Swapchain swapchain(LogicalDevice dev, Surface surface) {
		return new Swapchain.Builder(dev, surface)
				.count(cfg.getFrameCount())
				.clear(cfg.getBackground())
				.build();
	}

	@Bean
	public static RenderPass pass(LogicalDevice dev, Swapchain swapchain) {
		// Create colour attachment
		final Attachment attachment = new Attachment.Builder()
				.format(swapchain.format())
				.load(VkAttachmentLoadOp.CLEAR)
				.store(VkAttachmentStoreOp.STORE)
				.finalLayout(VkImageLayout.PRESENT_SRC_KHR)
				.build();

		// Create render pass
		return new RenderPass.Builder()
				.subpass()
					.colour(attachment)
					.build()
				.build(dev);
	}

	@Bean
	static FrameSet frames(Swapchain swapchain, RenderPass pass) {
		return new FrameSet(swapchain, pass, List.of());
	}

	@Bean
	static FrameBuilder builder(FrameSet frames, @Qualifier("graphics") Command.Pool pool) {
		return new FrameBuilder(frames::buffer, pool::allocate, VkCommandBufferUsage.ONE_TIME_SUBMIT);
	}

	@Bean
	FrameProcessor processor(Swapchain swapchain, FrameBuilder builder, Collection<FrameListener> listeners) {
		final var proc = new FrameProcessor(swapchain, builder, cfg.getFrameCount());
		listeners.forEach(proc::add);
		return proc;
	}

	@Bean
	public RenderLoop loop(ScheduledExecutorService executor, FrameProcessor proc, RenderSequence seq) {
		final Runnable task = () -> proc.render(seq);
		final RenderLoop loop = new RenderLoop(executor);
		loop.rate(cfg.getFrameRate());
		loop.start(task);
		return loop;
	}
}
