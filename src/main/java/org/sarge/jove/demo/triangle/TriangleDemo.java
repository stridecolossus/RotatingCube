package org.sarge.jove.demo.triangle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.sarge.jove.util.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TriangleDemo {
	@Bean
	public static DataSource source() {
		return DataSource.of("./src/main/resources");
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		SpringApplication.run(TriangleDemo.class, args);
	}
}
