package org.sarge.jove.demo.cube;

import java.util.List;

import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.render.*;
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
}
