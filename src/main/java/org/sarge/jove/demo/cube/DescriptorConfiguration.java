package org.sarge.jove.demo.cube;

import java.util.List;

import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.image.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.DescriptorSet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
public class DescriptorConfiguration {
	@Autowired private LogicalDevice dev;
	@Autowired private ApplicationConfiguration cfg;

	private final Binding samplerBinding = new Binding.Builder()
			.binding(0)
			.type(VkDescriptorType.COMBINED_IMAGE_SAMPLER)
			.stage(VkShaderStage.FRAGMENT)
			.build();

	private final Binding uniformBinding = new Binding.Builder()
		    .binding(1)
		    .type(VkDescriptorType.UNIFORM_BUFFER)
		    .stage(VkShaderStage.VERTEX)
		    .build();

	@Bean
	public Layout layout() {
		return Layout.create(dev, List.of(samplerBinding, uniformBinding));
	}

	@Bean
	public Pool pool() {
		final int count = cfg.getFrameCount();
		return new Pool.Builder()
				.add(VkDescriptorType.COMBINED_IMAGE_SAMPLER, count)
				.add(VkDescriptorType.UNIFORM_BUFFER, count)
				.max(count)
				.build(dev);
	}

	@Bean
	public DescriptorSet descriptor(Pool pool, Layout layout, Sampler sampler, View texture, ResourceBuffer uniform) {
		final DescriptorSet set = pool.allocate(layout).iterator().next();
		set.entry(samplerBinding).set(sampler.resource(texture));
		set.entry(uniformBinding).set(uniform);
		DescriptorSet.update(dev, List.of(set));
		return set;
	}

//	@Bean
//	public List<DescriptorSet> descriptors(Pool pool, Layout layout, Sampler sampler, View texture, VulkanBuffer uniform) {
//		// Allocate descriptor set per frame-buffer
//		final var descriptors = pool.allocate(layout, cfg.getFrameCount());
//
//		// Init resources
//		DescriptorSet.set(descriptors, samplerBinding, sampler.resource(texture));
//		DescriptorSet.set(descriptors, uniformBinding, uniform.uniform());
//		DescriptorSet.update(dev, descriptors);
//
//		return descriptors;
//	}
}
