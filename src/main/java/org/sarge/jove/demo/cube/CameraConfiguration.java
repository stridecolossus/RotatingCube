package org.sarge.jove.demo.cube;

import java.nio.ByteBuffer;

import org.sarge.jove.control.*;
import org.sarge.jove.geometry.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.sarge.jove.platform.vulkan.render.ResourceBuffer;
import org.sarge.jove.scene.core.Projection;
import org.sarge.jove.util.MathsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
public class CameraConfiguration {
	private @Autowired ApplicationConfiguration cfg;

	@Bean
	public ResourceBuffer uniform(LogicalDevice dev, Allocator allocator) {
		// Specify uniform buffer
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.optimal(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create uniform buffer
		final int len = (2 + cfg.getInstances()) * Matrix.LAYOUT.stride();
		final VulkanBuffer buffer = VulkanBuffer.create(dev, allocator, len, props);
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
	Matrix projection() {
		return Projection.DEFAULT.matrix(0.1f, 100, cfg.getDimensions());
	}

	@Bean
	static MutableRotation rotation() {
		final Vector axis = new Vector(MathsUtil.HALF, 1, 0);
		return new MutableRotation(new Normal(axis));
	}

	@Bean
	Animator animator(MutableRotation rot) {
		return new Animator(rot.animation(), cfg.getPeriod());
	}

	@Bean
	public static Player player(Animator animator) {
		final Player player = new Player(animator);
		player.apply(Playable.State.PLAY);
		return player;
	}

	@Bean
	public Frame.Listener update(ResourceBuffer uniform, Matrix projection, Matrix view, MutableRotation rotation) {
		final int instances = cfg.getInstances();

		final int size = 1 + (int) Math.sqrt(instances - 1);

		final float width = 2f / size;
		final float half = width / 2;

		final Matrix scale = Matrix.scale(half, half, half);

		final ByteBuffer bb = uniform.buffer();
		return frame -> {
			projection.buffer(bb);
			view.buffer(bb);

			// TODO
			// - halfs
			// - build translation array
			// - scale to width
			final Matrix rot = rotation.matrix();
			for(int row = 0; row < size; ++row) {
				for(int col = 0; col < size; ++col) {
					final float x = col * width - half;
					final float y = row * width - half;
    				final Vector v = new Vector(x, y, 0);
    				final Matrix trans = Matrix.translation(v);
    				final Matrix model = trans.multiply(rot).multiply(scale);
    				model.buffer(bb);
				}
			}

			bb.rewind();
		};
	}
}
