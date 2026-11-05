package ma.ahmedbenidar.tp14.amorce;

import ma.ahmedbenidar.tp14.depots.CompteDepot;
import ma.ahmedbenidar.tp14.entites.Compte;
import ma.ahmedbenidar.tp14.entites.TypeCompte;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Jeu de donnees de demonstration.
 * Les soldes, les dates et les types sont volontairement varies pour que
 * l application Android affiche une liste realiste des le premier lancement.
 */
@Component
public class AmorceComptes implements CommandLineRunner {

    private final CompteDepot compteDepot;

    public AmorceComptes(CompteDepot compteDepot) {
        this.compteDepot = compteDepot;
    }

    @Override
    public void run(String... args) {
        if (compteDepot.count() > 0) {
            return;
        }

        List<Compte> comptes = List.of(
                compte("Ahmed Benidar", 18500.00, "2024-01-15", TypeCompte.COURANT),
                compte("Salma El Fassi", 72350.50, "2024-02-03", TypeCompte.EPARGNE),
                compte("Youssef Ouazzani", 9400.75, "2024-03-21", TypeCompte.COURANT),
                compte("Nadia Bouhaddou", 152000.00, "2024-04-08", TypeCompte.EPARGNE),
                compte("Karim Tazi", 32680.25, "2024-05-30", TypeCompte.PROFESSIONNEL),
                compte("Imane Cherkaoui", 5100.00, "2024-06-12", TypeCompte.COURANT));

        compteDepot.saveAll(comptes);

        System.out.println("Comptes de demonstration charges : " + compteDepot.count());
    }

    private Compte compte(String titulaire, double solde, String date, TypeCompte type) {
        Compte c = new Compte();
        c.setTitulaire(titulaire);
        c.setSolde(solde);
        c.setDateCreation(java.sql.Date.valueOf(date));
        c.setType(type);
        return c;
    }

}
