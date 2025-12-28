package org.sarge.jove.demo.cube;

import org.sarge.jove.geometry.Normal;
import org.sarge.jove.model.*;
import org.sarge.jove.model.Mesh.MeshData;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.springframework.context.annotation.*;

@Configuration
class VertexBufferConfiguration {
	@Bean
	static VulkanBuffer.Factory factory(Allocator allocator) {
		return new VulkanBuffer.Factory(allocator);
	}

	@Bean
	static Mesh cube() {
		return new Cube()
				.build(0.4f)
				.remove(Normal.LAYOUT);
	}

	@Bean
	static VulkanBuffer vertices(VulkanBuffer.Factory factory, Mesh mesh, Command.Pool graphics) {
		// Create staging buffer
		final MeshData vertices = mesh.vertices();
		final VulkanBuffer staging = factory.staging(vertices.length());
		vertices.buffer(staging.buffer());

		// Init VBO properties
		final var props = new MemoryProperties.Builder<VkBufferUsageFlags>()
				.usage(VkBufferUsageFlags.TRANSFER_DST)
				.usage(VkBufferUsageFlags.VERTEX_BUFFER)
				.required(VkMemoryPropertyFlags.DEVICE_LOCAL)
				.build();

		// Create destination
		final VulkanBuffer buffer = factory.create(vertices.length(), props);

		// Copy to destination
		staging
				.copy(buffer)
				.submit(graphics);

		// Release staging
		staging.destroy();

		return buffer;
	}

	@Bean
	static VertexBuffer vbo(VulkanBuffer vertices) {
		return new VertexBuffer(vertices);
	}
}
