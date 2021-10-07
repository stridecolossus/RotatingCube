package org.sarge.jove.demo.triangle;

import java.util.List;

import org.sarge.jove.common.Colour;
import org.sarge.jove.platform.vulkan.VkAttachmentLoadOp;
import org.sarge.jove.platform.vulkan.VkAttachmentStoreOp;
import org.sarge.jove.platform.vulkan.VkImageLayout;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.Surface;
import org.sarge.jove.platform.vulkan.image.View;
import org.sarge.jove.platform.vulkan.render.Attachment;
import org.sarge.jove.platform.vulkan.render.FrameBuffer;
import org.sarge.jove.platform.vulkan.render.RenderPass;
import org.sarge.jove.platform.vulkan.render.Swapchain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PresentationConfiguration {
	@Bean
	public static Swapchain swapchain(LogicalDevice dev, Surface surface) {
		return new Swapchain.Builder(dev, surface)
				.count(2)
				.clear(new Colour(0.3f, 0.3f, 0.3f, 1))
				.build();
	}

	@Bean
	public static RenderPass pass(LogicalDevice dev) {
		// Create colour attachment
		final Attachment attachment = new Attachment.Builder()
				.format(Swapchain.DEFAULT_FORMAT)
				.load(VkAttachmentLoadOp.CLEAR)
				.store(VkAttachmentStoreOp.STORE)
				.finalLayout(VkImageLayout.PRESENT_SRC_KHR)
				.build();

		// Create render pass
		return new RenderPass.Builder()
				.subpass()
					.colour(attachment, VkImageLayout.COLOR_ATTACHMENT_OPTIMAL)
					.build()
				.build(dev);
	}

	@Bean
	public static FrameBuffer frame(Swapchain swapchain, RenderPass pass) {
		// TODO
		final View view = swapchain.views().iterator().next();
		return FrameBuffer.create(pass, swapchain.extents(), List.of(view));
	}
}
