package org.sarge.jove.demo.cube;

import java.util.List;

import org.sarge.jove.common.Handle;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.context.annotation.*;

@Configuration
class PresentationConfiguration {
	@Bean
	public static Surface surface(Handle surface, PhysicalDevice dev) {
		return new Surface(surface, dev);
	}

	@Bean
	public static Swapchain swapchain(LogicalDevice dev, Surface surface, ApplicationConfiguration cfg) {
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
		final Subpass subpass = Subpass.of(attachment);
		return RenderPass.create(dev, List.of(subpass));
	}

	// TODO - list?
	@Bean
	public static FrameBuffer frame(Swapchain swapchain, RenderPass pass) {
		return new FrameBuffer.Builder()
				.pass(pass)
				.extents(swapchain.extents())
				.build(swapchain.attachments())
				.get(0);
	}
}
