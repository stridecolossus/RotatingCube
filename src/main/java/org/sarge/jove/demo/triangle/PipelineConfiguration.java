package org.sarge.jove.demo.triangle;

import java.io.IOException;

import org.sarge.jove.platform.vulkan.VkCullMode;
import org.sarge.jove.platform.vulkan.VkShaderStage;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.core.Shader;
import org.sarge.jove.platform.vulkan.core.Shader.ShaderLoader;
import org.sarge.jove.platform.vulkan.pipeline.Pipeline;
import org.sarge.jove.platform.vulkan.pipeline.PipelineLayout;
import org.sarge.jove.platform.vulkan.render.RenderPass;
import org.sarge.jove.platform.vulkan.render.Swapchain;
import org.sarge.jove.util.DataSource;
import org.sarge.jove.util.ResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PipelineConfiguration {
	private final LogicalDevice dev;
	private final ResourceLoader<String, Shader> loader;

	public PipelineConfiguration(LogicalDevice dev, DataSource src) {
		this.dev = dev;
		this.loader = ResourceLoader.of(src, new ShaderLoader(dev));
	}

	@Bean
	public Shader vertex() throws IOException {
		return loader.load("spv.triangle.vert");
	}

	@Bean
	public Shader fragment() throws IOException {
		return loader.load("spv.triangle.frag");
	}

	@Bean
	PipelineLayout pipelineLayout() {
		return new PipelineLayout.Builder(dev).build();
	}

	@Bean
	public Pipeline pipeline(RenderPass pass, Swapchain swapchain, Shader vertex, Shader fragment, PipelineLayout layout) {
		return new Pipeline.Builder()
				.layout(layout)
				.pass(pass)
				.viewport(swapchain.extents())
				.rasterizer()
					.cull(VkCullMode.NONE) // TODO
					.build()
				.shader(VkShaderStage.VERTEX)
					.shader(vertex)
					.build()
				.shader(VkShaderStage.FRAGMENT)
					.shader(fragment)
					.build()
				.build(dev);
	}
}
