package org.example.websocket;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author renchao
 * @since v1.0
 * @see SpringApplicationBuilder
 */
@SpringBootApplication
public class App/* extends SpringBootServletInitializer*/ {

	// public static void main(String[] args) {
	// 	SpringApplication.run(App.class, args);
	// }

	public static void main(String[] args) {
		new SpringApplicationBuilder(App.class).bannerMode(Banner.Mode.OFF).run(args);
	}

	// @Override
	// protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	// 	return application.sources(App.class);
	// }

}
