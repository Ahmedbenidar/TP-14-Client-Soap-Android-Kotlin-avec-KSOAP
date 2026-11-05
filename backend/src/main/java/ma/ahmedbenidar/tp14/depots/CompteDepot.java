package ma.ahmedbenidar.tp14.depots;

import ma.ahmedbenidar.tp14.entites.Compte;
import ma.ahmedbenidar.tp14.entites.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompteDepot extends JpaRepository<Compte, Long> {

    List<Compte> findByTypeOrderBySoldeDesc(TypeCompte type);

}
