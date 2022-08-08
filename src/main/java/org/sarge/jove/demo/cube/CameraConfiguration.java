package org.sarge.jove.demo.cube;

import java.nio.ByteBuffer;

import org.sarge.jove.control.*;
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
	public static ResourceBuffer uniform(LogicalDevice dev, AllocationService allocator) {
		// Specify uniform buffer
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.build();

		// Create uniform buffer
		final VulkanBuffer buffer = VulkanBuffer.create(dev, allocator, Matrix.IDENTITY.length(), props);
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
				.row(0, Vector.X)
			    .row(1, Vector.Y.invert())
			    .row(2, Vector.Z)
			    .build();

		return rot.multiply(trans);
	}

	@Bean
	static Matrix projection(Swapchain swapchain) {
		return Projection.DEFAULT.matrix(0.1f, 100, swapchain.extents());
	}

	@Bean
	static RotationAnimation rotation() {
		return new RotationAnimation(new Vector(MathsUtil.HALF, 1, 0).normalize());
	}

	@Bean
	static Animator animator(ApplicationConfiguration cfg, RotationAnimation rot) {
		return new Animator(cfg.getPeriod(), rot);
	}

	@Bean
	public static Player player(Animator animator) {
		final Player player = new Player();
		player.add(animator);
		player.state(Playable.State.PLAY);
		player.repeat(true);
		return player;
	}

	@Bean
	public static FrameListener update(ResourceBuffer uniform, Matrix projection, Matrix view, RotationAnimation rot) {
		final ByteBuffer bb = uniform.buffer();
		return (time, elapsed) -> {
			final Matrix model = rot.rotation().matrix();
			final Matrix matrix = projection.multiply(view).multiply(model);
			matrix.buffer(bb);
			bb.rewind();
		};
	}
}
