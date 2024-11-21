package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.Compte;
import ma.projet.graph.repositories.CompteRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ma.projet.graph.entities.TypeCompte;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private CompteRepository compteRepository;



    @QueryMapping
    public List<Compte> allComptes(){
        return compteRepository.findAll();
    }

    @QueryMapping
    public List<Compte> compteByType(@Argument TypeCompte type) {
        return compteRepository.findByType(type);
    }


    @QueryMapping
    public Compte compteById(@Argument Long id){
        Compte compte =  compteRepository.findById(id).orElse(null);
        if(compte == null) throw new RuntimeException(String.format("Compte %s not found", id));
        else return compte;
    }

    @MutationMapping
    public String deleteCompte(@Argument Long id) {
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return String.format("Compte avec l'ID %s a été supprimé avec succès.", id);
        } else {
            throw new RuntimeException(String.format("Compte avec l'ID %s n'existe pas.", id));
        }
    }

    @MutationMapping
    public String transfer(@Argument Long fromAccountId, @Argument Long toAccountId, @Argument double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant de la transaction doit être supérieur à zéro.");
        }

        // Récupérer les comptes source et destination
        Compte fromAccount = compteRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Compte source introuvable"));
        Compte toAccount = compteRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Compte destination introuvable"));

        // Vérifier le solde suffisant
        if (fromAccount.getSolde() < amount) {
            throw new RuntimeException("Solde insuffisant dans le compte source");
        }

        // Effectuer la transaction
        fromAccount.setSolde(fromAccount.getSolde() - amount);
        toAccount.setSolde(toAccount.getSolde() + amount);

        // Enregistrer les modifications
        compteRepository.save(fromAccount);
        compteRepository.save(toAccount);

        return String.format("Transaction de %.2f effectuée avec succès entre le compte %d et le compte %d.",
                amount, fromAccountId, toAccountId);
    }


    @MutationMapping
    public Compte saveCompte(@Argument Compte compte){
       return compteRepository.save(compte);
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count(); // Nombre total de comptes
        double sum = compteRepository.sumSoldes(); // Somme totale des soldes
        double average = count > 0 ? sum / count : 0; // Moyenne des soldes

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }
}
