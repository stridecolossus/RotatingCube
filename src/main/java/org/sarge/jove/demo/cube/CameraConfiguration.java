package org.sarge.jove.demo.cube;

import java.nio.ByteBuffer;

import org.sarge.jove.control.*;
import org.sarge.jove.geometry.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.core.Projection;
import org.sarge.jove.util.MathsUtil;
import org.springframework.context.annotation.*;

@Configuration
public class CameraConfiguration {
	@Bean
	public static ResourceBuffer uniform(LogicalDevice dev) {
		// Specify uniform buffer
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.optimal(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create uniform buffer
		final VulkanBuffer buffer = VulkanBuffer.create(dev, Matrix4.LENGTH, props);
		return new ResourceBuffer(buffer, VkDescriptorType.UNIFORM_BUFFER, 0);
	}

	@Bean
	static Matrix view() {
		final Matrix trans = new Matrix.Builder()
				.identity()
				.column(3, new Vector(0, 0, -2))
				.build();

		final Matrix rot = new Matrix.Builder()
				.identity()
				.row(0, Axis.X)
			    .row(1, Axis.Y.invert())
			    .row(2, Axis.Z)
			    .build();

		return rot.multiply(trans);
	}

	@Bean
	static Matrix projection(Swapchain swapchain) {
		return Projection.DEFAULT.matrix(0.1f, 100, swapchain.extents());
	}

	@Bean
	static RotationAnimation rotation() {
		final Normal axis = new Vector(MathsUtil.HALF, 1, 0).normalize();
		return new RotationAnimation(axis);
	}

	@Bean
	static Animator animator(RotationAnimation rot, ApplicationConfiguration cfg) {
		return new BoundAnimator(rot, cfg.getPeriod());
	}

	@Bean
	public static Player player(Animator animator) {
		final Player player = new Player();
		player.add(animator);
		player.play();
		return player;
	}

	@Bean
	public static Frame.Listener update(ResourceBuffer uniform, Matrix projection, Matrix view, RotationAnimation rot) {
		final ByteBuffer bb = uniform.buffer();
		return () -> {
			final Matrix model = rot.rotation().matrix();
			final Matrix matrix = projection.multiply(view).multiply(model);
			matrix.buffer(bb);
			bb.rewind();
		};
	}
}
