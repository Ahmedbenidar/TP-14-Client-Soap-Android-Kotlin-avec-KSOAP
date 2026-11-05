package ma.ahmedbenidar.soapclient.ws

import ma.ahmedbenidar.soapclient.beans.Compte
import ma.ahmedbenidar.soapclient.beans.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Point d entree unique vers le service SOAP de la banque.
 *
 * Toute la mecanique ksoap2 (enveloppe, transport, lecture de la reponse) est
 * concentree ici pour que l activite ne manipule que des objets Compte.
 */
class Service {

    private val nomEspace = "http://ws.soapAcount/"
    private val adresse = "http://10.0.2.2:8084/services/ws"
    private val actionGetComptes = "getComptes"
    private val actionCreateCompte = "createCompte"
    private val actionDeleteCompte = "deleteCompte"

    private val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)

    private fun transport(): HttpTransportSE {
        // Sans delai explicite, un service eteint laisse l ecran bloque
        // plusieurs dizaines de secondes avant l affichage de l erreur.
        return HttpTransportSE(null, adresse, DELAI_MS)
    }

    fun getComptes(): List<Compte> {
        val requete = SoapObject(nomEspace, actionGetComptes)
        val enveloppe = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(requete)
        }
        val transport = transport()
        val resultat = mutableListOf<Compte>()

        transport.call("", enveloppe)

        val reponse = enveloppe.bodyIn as SoapObject
        for (index in 0 until reponse.propertyCount) {
            val element = reponse.getProperty(index) as SoapObject
            resultat.add(
                Compte(
                    id = element.getPropertySafelyAsString("id").toLongOrNull(),
                    titulaire = element.getPropertySafelyAsString("titulaire") ?: "Inconnu",
                    solde = element.getPropertySafelyAsString("solde").toDoubleOrNull() ?: 0.0,
                    dateCreation = lireDate(element.getPropertySafelyAsString("dateCreation")),
                    type = lireType(element.getPropertySafelyAsString("type"))
                )
            )
        }
        return resultat
    }

    fun createCompte(solde: Double, type: TypeCompte): Boolean {
        val requete = SoapObject(nomEspace, actionCreateCompte).apply {
            // ksoap2 ne sait pas marshaller un Double brut : on envoie la
            // valeur sous forme de texte, le serveur JAXB la convertit.
            addProperty("solde", solde.toString())
            addProperty("type", type.name)
        }
        val enveloppe = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(requete)
        }
        val transport = transport()

        return try {
            transport.call("", enveloppe)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    fun deleteCompte(id: Long): Boolean {
        val requete = SoapObject(nomEspace, actionDeleteCompte).apply {
            addProperty("id", id.toString())
        }
        val enveloppe = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(requete)
        }
        val transport = transport()

        return try {
            transport.call("", enveloppe)
            val brut = enveloppe.response?.toString()?.trim()?.lowercase(Locale.ROOT)
            brut == "true"
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    private fun lireDate(valeur: String?): Date {
        if (valeur.isNullOrBlank()) {
            return Date()
        }
        val texte = valeur.take(10)
        return try {
            formatDate.parse(texte) ?: Date()
        } catch (ex: Exception) {
            Date()
        }
    }

    private fun lireType(valeur: String?): TypeCompte {
        val texte = valeur?.trim()?.uppercase(Locale.ROOT) ?: ""
        return TypeCompte.values().firstOrNull { it.name == texte } ?: TypeCompte.COURANT
    }

    private companion object {
        const val DELAI_MS = 8000
    }
}
