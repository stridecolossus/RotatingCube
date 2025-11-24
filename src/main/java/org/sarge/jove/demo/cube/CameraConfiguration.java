package org.sarge.jove.demo.cube;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.function.IntConsumer;

import org.sarge.jove.common.Dimensions;
import org.sarge.jove.control.*;
import org.sarge.jove.geometry.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.VulkanBuffer;
import org.sarge.jove.platform.vulkan.memory.*;
import org.sarge.jove.platform.vulkan.render.ResourceBuffer;
import org.sarge.jove.util.MathsUtility;
import org.springframework.context.annotation.*;

@Configuration
class CameraConfiguration {
	@Bean
	static VulkanBuffer[] uniformBuffers(Allocator allocator) {
		final var properties = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.optimal(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		final long length = Matrix.LAYOUT.stride();

		final var uniform = new VulkanBuffer[2];
		for(int n = 0; n < 2; ++n) {
			uniform[n] = VulkanBuffer.create(allocator, length, properties);
		}

		return uniform;
	}

	@Bean
	static ResourceBuffer[] uniform(VulkanBuffer[] uniformBuffers) {
		final var uniform = new ResourceBuffer[2];
		for(int n = 0; n < 2; ++n) {
			uniform[n] = new ResourceBuffer(VkDescriptorType.UNIFORM_BUFFER, 0L, uniformBuffers[n]);
		}

		return uniform;
	}

	@Bean
	static IntConsumer update(ResourceBuffer[] uniform, Matrix projection, Matrix view, AxisAngle rotation) {
		final ByteBuffer[] buffers = new ByteBuffer[2];
		for(int n = 0; n < 2; ++n) {
			buffers[n] = uniform[n].buffer().buffer();
		}

		return index -> {
			final Matrix matrix = projection.multiply(view).multiply(rotation.matrix());
			final ByteBuffer bb = buffers[index];
			bb.rewind();
			matrix.buffer(bb);
		};
	}

	@Bean
	static Matrix view() {
		final Matrix translation = new Matrix.Builder(4)
				.identity()
				.column(3, new Vector(0, 0, -2))
				.build();

		final Matrix rotation = new Matrix.Builder(4)
				.identity()
				.row(0, Axis.X)
				.row(1, Axis.Y.invert())
				.row(2, Axis.Z)
				.build();

		// TODO - this STILL feels the wrong way round!
//		return translation.multiply(rotation);
		return rotation.multiply(translation);
	}

	@Bean
	static Matrix projection() {
		return Projection.DEFAULT.matrix(0.1f, 100, new Dimensions(1024, 768)); //  cfg.getDimensions());
	}

	@Bean
	static AxisAngle rotation() {
		final var axis = new Vector(MathsUtility.HALF, 1, 0);
		return new AxisAngle(new Normal(axis), 0);
	}

	@Bean
	static Animator animator(AxisAngle rotation) {
		return new Animator(rotation.animation(), Duration.ofSeconds(2)); // cfg.getPeriod());
	}

	@Bean
	static Player player(Animator animator) {
		final var player = new Player(animator);
		player.play();
		return player;
	}
}
