package org.sarge.jove.demo.cube;

import org.sarge.jove.geometry.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.Projection;
import org.sarge.jove.util.MathsUtil;
import org.springframework.context.annotation.*;

@Configuration
public class CameraConfiguration {
	@Bean
	public static ResourceBuffer uniform(LogicalDevice dev, AllocationService allocator, Swapchain swapchain) {
		// Specify uniform buffer
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.build();

		// Create uniform buffer
		final VulkanBuffer buffer = VulkanBuffer.create(dev, allocator, Matrix.IDENTITY.length(), props);
		final ResourceBuffer uniform = new ResourceBuffer(buffer, VkDescriptorType.UNIFORM_BUFFER, 0);

		// TODO - model rotation
		final Matrix x = Rotation.matrix(Vector.X, MathsUtil.toRadians(30));
		final Matrix y = Rotation.matrix(Vector.Y, MathsUtil.toRadians(30));
		final Matrix model = x.multiply(y);
//		final Matrix model = Matrix.IDENTITY;

		// Init view transform
		final Matrix trans = new Matrix.Builder()
				.identity()
				.column(3, new Vector(0, 0, -2))
				.build();
		final Matrix rot = new Matrix.Builder()
				.identity()
				.row(0, Vector.X)
			    .row(1, Vector.Y.invert())
			    .row(2, Vector.Z)
			    .build();
		final Matrix view = rot.multiply(trans);

		// Init projection
		final Matrix projection = Projection.DEFAULT.matrix(0.1f, 100, swapchain.extents());

		final Matrix matrix = projection.multiply(view).multiply(model);
		matrix.buffer(uniform.buffer());

		return uniform;
	}
}
