package org.sarge.jove.demo.cube;

import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.core.Command.*;
import org.sarge.jove.platform.vulkan.core.VertexBuffer;
import org.sarge.jove.platform.vulkan.pipeline.Pipeline;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.context.annotation.*;

@Configuration
public class RenderConfiguration {
	@Bean
	public static Buffer buffer(Pool graphics, FrameBuffer fb, Pipeline pipeline, DescriptorSet ds, VertexBuffer vbo, Model model) {
		final Buffer buffer = graphics.allocate(1).get(0);

		buffer
			.begin()
				.add(fb.begin())
					.add(pipeline.bind())
					.add(vbo.bind(0))
					.add(ds.bind(pipeline.layout()))
					.add(DrawCommand.of(model))
				.add(FrameBuffer.END)
			.end();

		return buffer;
	}
}
