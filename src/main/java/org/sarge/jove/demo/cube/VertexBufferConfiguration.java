package org.sarge.jove.demo.cube;

import java.nio.ByteBuffer;

import org.sarge.jove.common.Bufferable;
import org.sarge.jove.common.Coordinate.Coordinate2D;
import org.sarge.jove.common.Vertex;
import org.sarge.jove.geometry.Point;
import org.sarge.jove.platform.vulkan.VkBufferUsage;
import org.sarge.jove.platform.vulkan.VkMemoryProperty;
import org.sarge.jove.platform.vulkan.common.Command.Pool;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.VulkanBuffer;
import org.sarge.jove.platform.vulkan.memory.AllocationService;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertexBufferConfiguration {
	private static final Bufferable VERTICES = new Bufferable() {
		private final Vertex[] vertices = {
				new Vertex.Builder().position(new Point(-0.5f, +0.5f, 0)).coordinate(Coordinate2D.TOP_LEFT).build(),
				new Vertex.Builder().position(new Point(-0.5f, -0.5f, 0)).coordinate(Coordinate2D.BOTTOM_LEFT).build(),
				new Vertex.Builder().position(new Point(+0.5f, +0.5f, 0)).coordinate(Coordinate2D.TOP_RIGHT).build(),
				new Vertex.Builder().position(new Point(+0.5f, -0.5f, 0)).coordinate(Coordinate2D.BOTTOM_RIGHT).build(),
		};

		@Override
		public int length() {
			return 4 * vertices[0].length();
		}

		@Override
		public void buffer(ByteBuffer buffer) {
			for(Vertex v : vertices) {
				v.buffer(buffer);
			}
		}
	};

	@Bean
	public static VulkanBuffer vbo(LogicalDevice dev, AllocationService allocator, Pool graphics) {
		// Create staging buffer
		final VulkanBuffer staging = VulkanBuffer.staging(dev, allocator, VERTICES);

		// Init VBO memory properties
		final MemoryProperties<VkBufferUsage> props = new MemoryProperties.Builder<VkBufferUsage>()
				.usage(VkBufferUsage.TRANSFER_DST)
				.usage(VkBufferUsage.VERTEX_BUFFER)
				.required(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create VBO
		final VulkanBuffer vbo = VulkanBuffer.create(dev, allocator, staging.length(), props);

		// Copy staging to VBO
		staging.copy(vbo).submitAndWait(graphics);

		// Release staging
		staging.close();

		return vbo;
	}
}
