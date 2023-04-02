package org.sarge.jove.demo.cube;

import java.io.*;

import org.sarge.jove.common.Rectangle;
import org.sarge.jove.io.*;
import org.sarge.jove.model.Mesh;
import org.sarge.jove.platform.vulkan.VkShaderStage;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.context.annotation.*;

@Configuration
class PipelineConfiguration {
	private final LogicalDevice dev;
	private final ResourceLoaderAdapter<InputStream, Shader> loader;

	PipelineConfiguration(LogicalDevice dev, DataSource classpath) {
		this.dev = dev;
		this.loader = new ResourceLoaderAdapter<>(classpath, new Shader.Loader(dev));
	}

	@Bean
	Shader vertex() throws IOException {
		return loader.load("spv.cube.vert");
	}

	@Bean
	Shader fragment() throws IOException {
		return loader.load("spv.cube.frag");
	}

	@Bean
	PipelineLayout pipelineLayout(DescriptorSet.Layout layout) {
		return new PipelineLayout.Builder()
				.add(layout)
				.build(dev);
	}

	@Bean
	public Pipeline pipeline(RenderPass pass, Shader vertex, Shader fragment, PipelineLayout layout, Mesh cube, ApplicationConfiguration cfg) {
		return new GraphicsPipelineBuilder(pass)
				.viewport(new Rectangle(cfg.getDimensions()))
				.shader(new ProgrammableShaderStage(VkShaderStage.VERTEX, vertex))
				.shader(new ProgrammableShaderStage(VkShaderStage.FRAGMENT, fragment))
				.input()
					.add(cube.layout())
					.build()
				.assembly()
					.topology(cube.primitive())
					.build()
				.build(dev, layout);
	}
}
