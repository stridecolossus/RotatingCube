package org.sarge.jove.demo.cube;

import java.util.List;

import org.sarge.jove.platform.vulkan.VkDescriptorType;
import org.sarge.jove.platform.vulkan.VkShaderStage;
import org.sarge.jove.platform.vulkan.common.Resource;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.image.View;
import org.sarge.jove.platform.vulkan.render.DescriptorSet;
import org.sarge.jove.platform.vulkan.render.DescriptorSet.Binding;
import org.sarge.jove.platform.vulkan.render.DescriptorSet.Layout;
import org.sarge.jove.platform.vulkan.render.DescriptorSet.Pool;
import org.sarge.jove.platform.vulkan.render.Sampler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DescriptorConfiguration {
	@Autowired private LogicalDevice dev;
	@Autowired private ApplicationConfiguration cfg;

	private final Binding binding = new Binding.Builder()
			.type(VkDescriptorType.COMBINED_IMAGE_SAMPLER)
			.stage(VkShaderStage.FRAGMENT)
			.build();

	@Bean
	public Layout layout() {
		return Layout.create(dev, List.of(binding));
	}

	@Bean
	public Pool pool() {
		final int count = cfg.getFrameCount();
		return new Pool.Builder()
				.add(VkDescriptorType.COMBINED_IMAGE_SAMPLER, count)
				.max(count)
				.build(dev);
	}

	@Bean
	public List<DescriptorSet> descriptors(Pool pool, Layout layout, Sampler sampler, View texture) {
		// Allocate descriptor set per frame-buffer
		final var descriptors = pool.allocate(layout, cfg.getFrameCount());

		// Init sampler
		final Resource res = sampler.resource(texture);
		DescriptorSet.set(descriptors, binding, res);
		DescriptorSet.update(dev, descriptors);

		return descriptors;
	}
}
