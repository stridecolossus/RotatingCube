package org.sarge.jove.demo.cube;

import java.util.List;
import java.util.function.Supplier;

import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.VkCommandBufferUsage;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.core.Command.Buffer;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration
public class RenderConfiguration {
	@Bean("pipeline.bind")
	static Command pipeline(Pipeline pipeline) {
		return pipeline.bind();
	}

	@Bean("descriptor.bind")
	static Command descriptor(DescriptorSet set, PipelineLayout layout) {
		return set.bind(layout);
	}

	@Bean("vbo.bind")
	static Command vbo(VertexBuffer vbo) {
		return vbo.bind(0);
	}

	@Bean
	static DrawCommand draw(Model model) {
		return DrawCommand.of(model);
	}

	@Bean
	public static RenderSequence sequence(List<Command> commands) {
		return RenderSequence.of(commands);
	}

	@Bean
	public static Supplier<Buffer> factory(@Qualifier("presentation") Command.Pool pool) {
		return () -> pool.allocate().begin(VkCommandBufferUsage.ONE_TIME_SUBMIT);
	}
}
