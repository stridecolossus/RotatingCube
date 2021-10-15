package org.sarge.jove.demo.cube;

import java.io.IOException;

import org.sarge.jove.common.Bufferable;
import org.sarge.jove.common.ImageData;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.common.Command.Pool;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.VulkanBuffer;
import org.sarge.jove.platform.vulkan.image.ComponentMappingBuilder;
import org.sarge.jove.platform.vulkan.image.Image;
import org.sarge.jove.platform.vulkan.image.ImageCopyCommand;
import org.sarge.jove.platform.vulkan.image.ImageDescriptor;
import org.sarge.jove.platform.vulkan.image.ImageExtents;
import org.sarge.jove.platform.vulkan.image.View;
import org.sarge.jove.platform.vulkan.memory.AllocationService;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.sarge.jove.platform.vulkan.pipeline.Barrier;
import org.sarge.jove.platform.vulkan.render.Sampler;
import org.sarge.jove.platform.vulkan.util.FormatBuilder;
import org.sarge.jove.util.DataSource;
import org.sarge.jove.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextureConfiguration {
	@Autowired private LogicalDevice dev;

	@Bean
	public Sampler sampler() {
		return new Sampler.Builder(dev).build();
	}

	@Bean
	public View texture(AllocationService allocator, DataSource src, Pool graphics) throws IOException {
		// Load texture image
		final var loader = ResourceLoader.of(src, new ImageData.Loader());
		final ImageData image = loader.load("thiswayup.jpg");

		// Determine image format
		final VkFormat format = FormatBuilder.format(image.layout());
//		final VkFormat format = VkFormat.R8G8B8A8_UNORM;
//		System.out.println("*********** format="+format);

		// Create descriptor
		final ImageDescriptor descriptor = new ImageDescriptor.Builder()
				.type(VkImageType.IMAGE_TYPE_2D)
				.aspect(VkImageAspect.COLOR)
				.extents(new ImageExtents(image.size()))
				.format(format)
				.build();

		// Init image memory properties
		final var props = new MemoryProperties.Builder<VkImageUsage>()
				.usage(VkImageUsage.TRANSFER_DST)
				.usage(VkImageUsage.SAMPLED)
				.required(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create texture
		final Image texture = new Image.Builder()
				.descriptor(descriptor)
				.properties(props)
				.build(dev, allocator);

		// Prepare texture
		new Barrier.Builder()
				.source(VkPipelineStage.TOP_OF_PIPE)
				.destination(VkPipelineStage.TRANSFER)
				.barrier(texture)
					.newLayout(VkImageLayout.TRANSFER_DST_OPTIMAL)
					.destination(VkAccess.TRANSFER_WRITE)
					.build()
				.build()
				.submitAndWait(graphics);

		// Create staging buffer
		final Bufferable data = Bufferable.of(image.bytes());
		final VulkanBuffer staging = VulkanBuffer.staging(dev, allocator, data);

		// Copy staging to texture
		new ImageCopyCommand.Builder(texture)
				.buffer(staging)
				.layout(VkImageLayout.TRANSFER_DST_OPTIMAL)
				.build()
				.submitAndWait(graphics);

		// Release staging
		staging.close();

		// Transition to sampled image
		new Barrier.Builder()
			.source(VkPipelineStage.TRANSFER)
			.destination(VkPipelineStage.FRAGMENT_SHADER)
			.barrier(texture)
				.oldLayout(VkImageLayout.TRANSFER_DST_OPTIMAL)
				.newLayout(VkImageLayout.SHADER_READ_ONLY_OPTIMAL)
				.source(VkAccess.TRANSFER_WRITE)
				.destination(VkAccess.SHADER_READ)
				.build()
			.build()
			.submitAndWait(graphics);

		// Build component mapping for the image
		final VkComponentMapping mapping = ComponentMappingBuilder.build(image.mapping());

		// Create texture view
		return new View.Builder(texture)
				.mapping(mapping)
				.build();
	}
}
