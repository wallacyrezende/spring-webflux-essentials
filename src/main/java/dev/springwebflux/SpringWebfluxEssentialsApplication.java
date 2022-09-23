package dev.springwebflux;

import java.util.zip.InflaterInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxEssentialsApplication {

	static {
		BlockHound.install(builder ->
				builder.allowBlockingCallsInside("java.util.UUID", "randomUUID")
						.allowBlockingCallsInside("java.io.InputStream", "readNBytes")
						.allowBlockingCallsInside("java.io.FilterInputStream", "read"));
	}

	public static void main(String[] args) {
//		System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("devwall"));
		SpringApplication.run(SpringWebfluxEssentialsApplication.class, args);
	}

}

