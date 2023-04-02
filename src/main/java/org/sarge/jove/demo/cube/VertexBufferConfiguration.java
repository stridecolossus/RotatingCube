package org.sarge.jove.demo.cube;

import org.sarge.jove.model.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.springframework.context.annotation.*;

@Configuration
public class VertexBufferConfiguration {
	@Bean
	static Mesh cube() {
		return new CubeBuilder().size(0.4f).build();
	}

	@Bean
	static VertexBuffer vbo(LogicalDevice dev, Allocator allocator, Mesh cube, Command.Pool graphics) {
		// Create staging buffer
		final VulkanBuffer staging = VulkanBuffer.staging(dev, allocator, cube.vertices());

		// Init VBO properties
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.TRANSFER_DST)
				.usage(VkBufferUsageFlag.VERTEX_BUFFER)
				.required(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create destination
		final VulkanBuffer buffer = VulkanBuffer.create(dev, allocator, staging.length(), props);

		// Copy to destination
		staging.copy(buffer).submit(graphics);
		staging.destroy();

		// Create VBO
		return new VertexBuffer(buffer);
	}
}
