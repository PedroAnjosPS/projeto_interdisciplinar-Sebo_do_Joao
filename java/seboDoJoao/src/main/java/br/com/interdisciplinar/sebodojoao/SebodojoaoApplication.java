package br.com.interdisciplinar.sebodojoao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SebodojoaoApplication {

	public static void main(String[] args) {
		System.out.print("Funcionou?");

		SpringApplication.run(SebodojoaoApplication.class, args);

		System.out.print("Sim, funcionou!");
	}

}
