package org.sarge.jove.demo.cube;

import java.util.*;

import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.image.*;
import org.sarge.jove.platform.vulkan.image.Sampler.SamplerResource;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.DescriptorSet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
public class DescriptorConfiguration {
	@Autowired
	private LogicalDevice device;

	private final Binding samplerBinding = new Binding.Builder()
        	.binding(0)
        	.type(VkDescriptorType.COMBINED_IMAGE_SAMPLER)
        	.stage(VkShaderStageFlags.FRAGMENT)
        	.build();

    private final Binding uniformBinding = new Binding.Builder()
            .binding(1)
            .type(VkDescriptorType.UNIFORM_BUFFER)
            .stage(VkShaderStageFlags.VERTEX)
            .build();

    @Bean
    Layout layout() {
    	return Layout.create(device, List.of(samplerBinding, uniformBinding), Set.of());
    }

    // TODO - count ~ frames in flight
    @Bean
    Pool pool() {
    	return new Pool.Builder()
    			.add(VkDescriptorType.COMBINED_IMAGE_SAMPLER, 2)
    			.add(VkDescriptorType.UNIFORM_BUFFER, 2)
    			.build(device);
    }

    @Bean
    static SamplerResource samplerResource(Sampler sampler, View texture) {
    	return sampler.new SamplerResource(texture);
    }

    // TODO - count ~ frames in flight
    @Bean
    List<DescriptorSet> descriptor(Layout layout, Pool pool, SamplerResource sampler, ResourceBuffer[] uniformBuffers) {
    	final List<DescriptorSet> sets = pool.allocate(2, layout); // TODO
    	DescriptorSet.set(sets, samplerBinding, sampler);
    	for(int n = 0; n < 2; ++n) {
    		sets.get(n).set(uniformBinding, uniformBuffers[n]);
    	}
    	//DescriptorSet.set(sets, uniformBinding, uniform);
    	DescriptorSet.update(device, sets);
    	return sets;
    }
}
