package org.sarge.jove.demo.cube;

import java.io.IOException;
import java.nio.file.Paths;

import org.sarge.jove.common.Rectangle;
import org.sarge.jove.model.Mesh;
import org.sarge.jove.platform.vulkan.VkShaderStageFlags;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.pipeline.Shader.ShaderLoader;
import org.sarge.jove.platform.vulkan.pipeline.VertexInputStage.VertexBinding;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
class PipelineConfiguration {
	@Autowired
	private LogicalDevice device;

	@Bean
	Shader vertex() throws IOException {
		final var loader = new ShaderLoader(device);
		return loader.load(Paths.get("src/main/resources/spv.quad.uniform.vert"));
	}

	@Bean
	Shader fragment() throws IOException {
		final var loader = new ShaderLoader(device);
		return loader.load(Paths.get("src/main/resources/spv.quad.texture.frag"));
	}

	@Bean
	PipelineLayout pipelineLayout(DescriptorSet.Layout layout) {
		return new PipelineLayout.Builder()
				.add(layout)
				.build(device);
	}

	@Bean
	Pipeline pipeline(RenderPass pass, Shader vertex, Shader fragment, PipelineLayout layout, Mesh mesh) { // ApplicationConfiguration cfg) {
		final VertexBinding binding = VertexBinding.of(0, 0, mesh.layout());
		final var builder = new GraphicsPipelineBuilder();
		builder.pass(pass);
		builder.layout(layout);
		builder.viewport().viewportAndScissor(new Rectangle(1024, 768));
		builder.input().add(binding);
		builder.assembly().topology(mesh.primitive());
		builder.shader(new ProgrammableShaderStage(VkShaderStageFlags.VERTEX, vertex));
		builder.shader(new ProgrammableShaderStage(VkShaderStageFlags.FRAGMENT, fragment));
		return builder.build(device);
	}
}
