package org.sarge.jove.demo.cube;

import org.sarge.jove.model.*;
import org.sarge.jove.model.Mesh.DataBuffer;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.springframework.context.annotation.*;

@Configuration
class VertexBufferConfiguration {
	@Bean
	static Mesh cube() {
		return new CubeBuilder().size(0.4f).build();
	}

	/*
	@Bean
	static Mesh mesh() {

		final Vertex[] vertices = {
    		new Vertex(new Point(-0.5f, -0.5f, 0), Coordinate2D.TOP_LEFT),
    		new Vertex(new Point(-0.5f, +0.5f, 0), Coordinate2D.BOTTOM_LEFT),
    		new Vertex(new Point(+0.5f, -0.5f, -0.25f), Coordinate2D.TOP_RIGHT),
    		new Vertex(new Point(+0.5f, +0.5f, -0.25f), Coordinate2D.BOTTOM_RIGHT),
    	};

		final var mesh = new VertexMesh(Primitive.TRIANGLE_STRIP, List.of(Point.LAYOUT, Coordinate2D.LAYOUT));

		for(Vertex v : vertices) {
			mesh.add(v);
		}

		return mesh;
	}
	*/

	@Bean
	static VulkanBuffer vertices(Allocator allocator, Mesh mesh, Command.Pool graphics) {
		// Create staging buffer
		final DataBuffer vertices = mesh.vertices();
		final VulkanBuffer staging = VulkanBuffer.staging(allocator, vertices.length());
		vertices.buffer(staging.buffer());

		// Init VBO properties
		final var props = new MemoryProperties.Builder<VkBufferUsageFlags>()
				.usage(VkBufferUsageFlags.TRANSFER_DST)
				.usage(VkBufferUsageFlags.VERTEX_BUFFER)
				.required(VkMemoryPropertyFlags.DEVICE_LOCAL)
				.build();

		// Create destination
		final VulkanBuffer buffer = VulkanBuffer.create(allocator, vertices.length(), props);

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
