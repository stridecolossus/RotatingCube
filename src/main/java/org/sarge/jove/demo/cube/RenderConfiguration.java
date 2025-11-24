package org.sarge.jove.demo.cube;

import java.util.List;
import java.util.function.IntConsumer;

import org.sarge.jove.model.Mesh;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.pipeline.Pipeline;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration
class RenderConfiguration {
//	@Bean("pipeline.bind")
//	static Command pipeline(Pipeline pipeline) {
//		return pipeline.bind();
//	}
//
//	@Bean("descriptor.bind")
//	static Command descriptor(Collection<DescriptorSet> sets, PipelineLayout layout) {
//		// TODO
//		final var first = sets.iterator().next();
//		return first.bind(layout);
//	}

	@Bean
	static DrawCommand draw(LogicalDevice device, Mesh mesh) { // , ApplicationConfiguration cfg) {
		return new DrawCommand.Builder()
				.vertexCount(mesh.count())
				.build(device);
	}

	@Bean
	static RenderSequence sequence(Pipeline pipeline, List<DescriptorSet> sets, IntConsumer update, VertexBuffer vbo, DrawCommand draw) {

		final Command bindPipeline = pipeline.bind();

		final Command[] bindDescriptorSet = sets
				.stream()
				.map(set -> set.bind(pipeline.layout()))
				.toArray(Command[]::new);

		final Command bindVertexBuffer = vbo.bind(0);

		final RenderSequence sequence = (index, buffer) -> {

			update.accept(index);

			buffer.add(bindPipeline);
			buffer.add(bindVertexBuffer);
			buffer.add(bindDescriptorSet[index]);
			buffer.add(draw);
		};

		return sequence;
	}

	@Bean
	static FrameComposer composer(@Qualifier("graphics") Command.Pool pool, RenderSequence sequence) {
		return new FrameComposer(pool, sequence);
	}
}
