package org.sarge.jove.demo.cube;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.sarge.jove.common.Dimensions;
import org.sarge.jove.platform.vulkan.VkAttachmentLoadOp;
import org.sarge.jove.platform.vulkan.VkAttachmentStoreOp;
import org.sarge.jove.platform.vulkan.VkImageLayout;
import org.sarge.jove.platform.vulkan.VkPresentModeKHR;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.Surface;
import org.sarge.jove.platform.vulkan.render.Attachment;
import org.sarge.jove.platform.vulkan.render.FrameBuffer;
import org.sarge.jove.platform.vulkan.render.RenderPass;
import org.sarge.jove.platform.vulkan.render.Swapchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PresentationConfiguration {
	@Autowired private LogicalDevice dev;
	@Autowired private ApplicationConfiguration cfg;

	@Bean
	public Swapchain swapchain(Surface surface) {
		return new Swapchain.Builder(dev, surface)
				.count(cfg.getFrameCount())
				.clear(cfg.getBackground())
				.mode(VkPresentModeKHR.MAILBOX_KHR)
				.build();
	}

	@Bean
	public RenderPass pass() {
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
	public static List<FrameBuffer> buffers(Swapchain swapchain, RenderPass pass) {
		final Dimensions extents = swapchain.extents();
		return swapchain
				.views()
				.stream()
				.map(view -> FrameBuffer.create(pass, extents, List.of(view)))
				.collect(toList());
	}
}
