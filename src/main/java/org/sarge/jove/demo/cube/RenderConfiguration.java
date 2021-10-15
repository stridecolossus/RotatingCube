package org.sarge.jove.demo.cube;

import java.util.List;
import java.util.Set;

import org.sarge.jove.platform.vulkan.common.Command.Buffer;
import org.sarge.jove.platform.vulkan.common.Command.Pool;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.Semaphore;
import org.sarge.jove.platform.vulkan.core.VulkanBuffer;
import org.sarge.jove.platform.vulkan.core.Work;
import org.sarge.jove.platform.vulkan.pipeline.Pipeline;
import org.sarge.jove.platform.vulkan.render.DescriptorSet;
import org.sarge.jove.platform.vulkan.render.DrawCommand;
import org.sarge.jove.platform.vulkan.render.FrameBuffer;
import org.sarge.jove.platform.vulkan.render.Swapchain;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RenderConfiguration {
	@Bean
	public static List<Buffer> sequence(List<FrameBuffer> frames, Pipeline pipeline, VulkanBuffer vbo, List<DescriptorSet> sets, Pool graphics) {
		// Allocate command for each frame
		final int count = frames.size();
		final List<Buffer> buffers = graphics.allocate(count);

		// Record render sequence
		for(int n = 0; n < count; ++n) {
			final FrameBuffer fb = frames.get(n);
			final DescriptorSet ds = sets.get(n);
			buffers
				.get(n)
				.begin()
					.add(fb.begin())
					.add(pipeline.bind())
					.add(vbo.bindVertexBuffer())
					.add(ds.bind(pipeline.layout()))
					.add(DrawCommand.draw(4))
					.add(FrameBuffer.END)
				.end();
		}

		return buffers;
	}

	@Bean
	public static ApplicationRunner render(LogicalDevice dev, Swapchain swapchain, List<Buffer> render, Pool presentation) {
		return args -> {
			final long start = System.currentTimeMillis();
			while(true) {
				// Stop after a second or so
				if(System.currentTimeMillis() - start > 1000) {
					break;
				}

				// Start next frame
				final Semaphore semaphore = Semaphore.create(dev);
				final int index = swapchain.acquire(semaphore, null);

				// Render frame
				final Buffer frame = render.get(index);
				Work.of(frame).submit(null);
				frame.pool().waitIdle();

				// Present frame
				// TODO - present should accept frame index?
				swapchain.present(presentation.queue(), Set.of(semaphore));
				presentation.waitIdle();
				semaphore.close();
			}
		};
	}
}
