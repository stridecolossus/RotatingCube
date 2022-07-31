package org.sarge.jove.demo.cube;

import java.util.function.Consumer;

import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.VkCommandBufferUsage;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.pipeline.Pipeline;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration
public class RenderConfiguration {
	@Bean
	static Consumer<Command.Buffer> recorder(Pipeline pipeline, DescriptorSet set, VertexBuffer vbo, Model model) {
		return buffer -> buffer
				.add(pipeline.bind())
				.add(set.bind(pipeline.layout()))
				.add(vbo.bind(0))
				.add(DrawCommand.of(model));
	}

	@Bean
	public static RenderSequence sequence(@Qualifier("presentation") Command.Pool pool, Consumer<Command.Buffer> recorder) {
		return new RenderSequence(pool::allocate, recorder, VkCommandBufferUsage.ONE_TIME_SUBMIT);
	}
}
