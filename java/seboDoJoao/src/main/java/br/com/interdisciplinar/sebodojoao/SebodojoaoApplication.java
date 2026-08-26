package br.com.interdisciplinar.sebodojoao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SebodojoaoApplication {

	public static void main(String[] args) {
		System.out.println("Funcionou?");

		SpringApplication.run(SebodojoaoApplication.class, args);

		System.out.println("Sim, funcionou!");
	}

}
