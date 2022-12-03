package org.sarge.jove.demo.cube;

import org.sarge.jove.model.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.springframework.context.annotation.*;

@Configuration
public class VertexBufferConfiguration {
	@Bean
	public static DefaultMesh cube() {
		// TODO - strip normals and colours
		return new CubeBuilder().build();
	}

	@Bean
	public static VertexBuffer vbo(LogicalDevice dev, DefaultMesh cube, Command.Pool graphics) {
		// Create staging buffer
		final var vertices = cube.buffer().vertices();
		final VulkanBuffer staging = VulkanBuffer.staging(dev, vertices);

		// Init VBO properties
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.TRANSFER_DST)
				.usage(VkBufferUsageFlag.VERTEX_BUFFER)
				.required(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create destination
		final VulkanBuffer buffer = VulkanBuffer.create(dev, staging.length(), props);

		// Copy to destination
		staging.copy(buffer).submit(graphics);
		staging.destroy();

		// Create VBO
		return new VertexBuffer(buffer);
	}
}
