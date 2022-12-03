package org.sarge.jove.demo.cube;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import org.sarge.jove.common.Handle;
import org.sarge.jove.control.Frame;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.FrameBuffer.Group;
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
	public Swapchain swapchain(LogicalDevice dev, Surface surface) {
		return new Swapchain.Builder(surface)
				.count(cfg.getFrameCount())
				.clear(cfg.getBackground())
				.usage(VkImageUsageFlag.TRANSFER_SRC)
				.build(dev);
	}

	@Bean
	public static RenderPass pass(LogicalDevice dev, Swapchain swapchain) {
		final Attachment attachment = Attachment.colour(swapchain.format());
		return new Subpass().colour(attachment).create(dev);
	}

	@Bean
	static Group frames(Swapchain swapchain, RenderPass pass) {
		return new Group(swapchain, pass, List.of());
	}

	@Bean
	static FrameBuilder builder(Group group, @Qualifier("graphics") Command.Pool pool) {
		return new FrameBuilder(group::buffer, pool::allocate, VkCommandBufferUsage.ONE_TIME_SUBMIT);
	}

	@Bean
	FrameProcessor processor(Swapchain swapchain, FrameBuilder builder, Collection<Frame.Listener> listeners) {
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
