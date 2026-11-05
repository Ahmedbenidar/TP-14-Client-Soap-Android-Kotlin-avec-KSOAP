package ma.ahmedbenidar.soapclient.beans

import java.util.Date

/**
 * Compte bancaire tel que renvoye par le service SOAP.
 * Le titulaire est conserve cote client pour afficher une carte plus lisible
 * que le simple numero de compte du squelette du TP.
 */
data class Compte(
    val id: Long?,
    val titulaire: String,
    val solde: Double,
    val dateCreation: Date,
    val type: TypeCompte
)

enum class TypeCompte(
    val libelle: String,
    val couleur: Long
) {
    COURANT("Courant", 0xFF1E88E5),
    EPARGNE("Epargne", 0xFF2E7D32),
    PROFESSIONNEL("Professionnel", 0xFF6A1B9A)
}
