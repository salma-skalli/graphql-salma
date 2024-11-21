package ma.projet.graph;

import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.TypeCompte;
import ma.projet.graph.repositories.CompteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate; // Import LocalDate

@SpringBootApplication
public class GraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CompteRepository compteRepository){
		return args -> {
			// Initialisation des comptes avec LocalDate
			compteRepository.save(new Compte(null, Math.random()*9000, LocalDate.now(), TypeCompte.EPARGNE)); // Use LocalDate.now() instead of new Date()
			compteRepository.save(new Compte(null, Math.random()*9000, LocalDate.now(), TypeCompte.COURANT)); // Same here
			compteRepository.save(new Compte(null, Math.random()*9000, LocalDate.now(), TypeCompte.EPARGNE)); // And here
		};
	}
}
