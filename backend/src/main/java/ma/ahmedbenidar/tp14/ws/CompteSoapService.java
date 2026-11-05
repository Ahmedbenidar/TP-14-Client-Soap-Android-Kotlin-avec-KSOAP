package ma.ahmedbenidar.tp14.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import ma.ahmedbenidar.tp14.depots.CompteDepot;
import ma.ahmedbenidar.tp14.entites.Compte;
import ma.ahmedbenidar.tp14.entites.TypeCompte;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Service SOAP consomme par l application Android Kotlin du TP 14.
 * Le nom des operations est celui attendu par le client ksoap2 :
 * getComptes, createCompte et deleteCompte.
 */
@Component
@WebService(
        serviceName = "BanqueKsoapWS",
        portName = "BanqueKsoapPort",
        targetNamespace = "http://ws.soapAcount/")
public class CompteSoapService {

    private final CompteDepot compteDepot;

    public CompteSoapService(CompteDepot compteDepot) {
        this.compteDepot = compteDepot;
    }

    @WebMethod(operationName = "getComptes")
    @WebResult(name = "compte")
    public List<Compte> getComptes() {
        return compteDepot.findAll();
    }

    @WebMethod(operationName = "createCompte")
    @WebResult(name = "compte")
    public Compte createCompte(
            @WebParam(name = "solde") double solde,
            @WebParam(name = "type") String type) {

        Compte compte = new Compte();
        compte.setTitulaire("Client guichet");
        compte.setSolde(solde);
        compte.setDateCreation(new Date());
        compte.setType(TypeCompte.valueOf(type));
        return compteDepot.save(compte);
    }

    @WebMethod(operationName = "deleteCompte")
    @WebResult(name = "supprime")
    public boolean deleteCompte(@WebParam(name = "id") Long id) {
        if (!compteDepot.existsById(id)) {
            return false;
        }
        compteDepot.deleteById(id);
        return true;
    }

    @WebMethod(operationName = "getCompteById")
    @WebResult(name = "compte")
    public Compte getCompteById(@WebParam(name = "id") Long id) {
        return compteDepot.findById(id).orElse(null);
    }

    @WebMethod(operationName = "getComptesParType")
    @WebResult(name = "compte")
    public List<Compte> getComptesParType(@WebParam(name = "type") String type) {
        return compteDepot.findByTypeOrderBySoldeDesc(TypeCompte.valueOf(type));
    }

    @WebMethod(operationName = "getSoldeGlobal")
    @WebResult(name = "total")
    public double getSoldeGlobal() {
        return compteDepot.findAll().stream()
                .mapToDouble(Compte::getSolde)
                .sum();
    }

}
