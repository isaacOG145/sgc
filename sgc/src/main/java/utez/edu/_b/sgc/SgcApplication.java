package utez.edu._b.sgc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SgcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SgcApplication.class, args);
	}

}
