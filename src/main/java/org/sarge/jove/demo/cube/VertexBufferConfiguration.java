package org.sarge.jove.demo.cube;

import org.sarge.jove.model.CubeBuilder;
import org.sarge.jove.model.Model;
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
	/*
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
	*/

	@Bean
	public static Model cube() {
		return new CubeBuilder().build();
	}

	@Bean
	public static VulkanBuffer vbo(LogicalDevice dev, AllocationService allocator, Pool graphics, Model model) {
		// Create staging buffer
		final VulkanBuffer staging = VulkanBuffer.staging(dev, allocator, model.vertices());

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
