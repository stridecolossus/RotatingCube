package org.sarge.jove.demo.cube;

import static org.sarge.jove.platform.vulkan.VkAccessFlags.*;
import static org.sarge.jove.platform.vulkan.VkImageLayout.*;
import static org.sarge.jove.platform.vulkan.VkImageUsageFlags.*;
import static org.sarge.jove.platform.vulkan.VkPipelineStageFlags.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.image.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.sarge.jove.platform.vulkan.pipeline.Barrier;
import org.sarge.jove.platform.vulkan.pipeline.Barrier.BarrierType.ImageBarrier;
import org.sarge.jove.util.*;
import org.springframework.context.annotation.*;

@Configuration
class TextureConfiguration {
	@Bean
	Sampler sampler(LogicalDevice device) {
		return new Sampler.Builder().build(device);
	}

	@Bean
	View texture(Allocator allocator, Command.Pool graphics) throws IOException {
		// Load image
		final var loader = new NativeImageLoader();
		final ImageData image = loader.load(Paths.get("../Data/thiswayup.jpg"));
		final VkFormat format = FormatBuilder.format(image);

		// Init descriptor
		final var descriptor = new Image.Descriptor.Builder()
				.type(VkImageType.TYPE_2D)
				.aspect(VkImageAspectFlags.COLOR)
				.extents(image.size())
				.format(format)
				.build();

		// Init memory
		final var properties = new MemoryProperties.Builder<VkImageUsageFlags>()
				.usage(TRANSFER_DST)
				.usage(SAMPLED)
				.required(VkMemoryPropertyFlags.DEVICE_LOCAL)
				.build();

		// Create texture
		final Image texture = new DefaultImage.Builder()
				.descriptor(descriptor)
				.properties(properties)
				.build(allocator);

		// Prepare texture
		new Barrier.Builder()
				.source(TOP_OF_PIPE)
				.destination(TRANSFER)
				.add(Set.of(), Set.of(TRANSFER_WRITE), new ImageBarrier(texture, UNDEFINED, TRANSFER_DST_OPTIMAL))
				.build(allocator.device())
				.submit(graphics);

		// Create staging buffer
		final var staging = new VulkanBuffer.Factory(allocator).staging(image.data());

		// Copy staging to texture
		new ImageTransferCommand.Builder()
				.buffer(staging)
				.image(texture)
				.layout(TRANSFER_DST_OPTIMAL)
				.region(image)
				.build()
				.submit(graphics);

		// Release staging
   		staging.destroy();

		// Transition to sampled image
		new Barrier.Builder()
				.source(TRANSFER)
				.destination(FRAGMENT_SHADER)
				.add(Set.of(TRANSFER_WRITE), Set.of(SHADER_READ), new ImageBarrier(texture, TRANSFER_DST_OPTIMAL, SHADER_READ_ONLY_OPTIMAL))
				.build(allocator.device())
				.submit(graphics);

		// Create image view
		return new View.Builder()
				.mapping(ComponentMapping.of(image.channels()))
				.release()
				.build(allocator.device(), texture);
	}
}
