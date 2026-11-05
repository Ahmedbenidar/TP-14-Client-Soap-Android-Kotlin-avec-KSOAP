package ma.ahmedbenidar.soapclient

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.ahmedbenidar.soapclient.adapter.CompteAdapter
import ma.ahmedbenidar.soapclient.beans.TypeCompte
import ma.ahmedbenidar.soapclient.ws.Service
import java.text.NumberFormat
import java.util.Locale

/**
 * Ecran principal du client SOAP.
 *
 * L activite ne parle jamais directement a ksoap2 : tous les appels passent
 * par la classe Service, executes dans une coroutine sur le fil des entrees
 * sorties puis restitues sur le fil de l interface.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var listeComptes: RecyclerView
    private lateinit var boutonAjouter: Button
    private lateinit var texteResume: TextView
    private lateinit var texteEtat: TextView

    private val adapter = CompteAdapter()
    private val service = Service()
    private val formatMonnaie = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiserVues()
        configurerListe()
        configurerEcouteurs()
        chargerComptes()
    }

    private fun initialiserVues() {
        listeComptes = findViewById(R.id.recyclerView)
        boutonAjouter = findViewById(R.id.fabAdd)
        texteResume = findViewById(R.id.textResume)
        texteEtat = findViewById(R.id.textEtat)
    }

    private fun configurerListe() {
        listeComptes.layoutManager = LinearLayoutManager(this)
        listeComptes.adapter = adapter

        adapter.onDeleteClick = { compte -> confirmerSuppression(compte) }
        adapter.onEditClick = { compte -> afficherDetails(compte) }
    }

    private fun configurerEcouteurs() {
        boutonAjouter.setOnClickListener { afficherFormulaireAjout() }
    }

    private fun chargerComptes() {
        texteEtat.text = getString(R.string.chargement_en_cours)

        lifecycleScope.launch {
            val resultat = withContext(Dispatchers.IO) {
                runCatching { service.getComptes() }
            }

            resultat.onSuccess { comptes ->
                adapter.updateComptes(comptes)
                rafraichirResume()
                texteEtat.text = if (comptes.isEmpty()) {
                    getString(R.string.aucun_compte)
                } else {
                    getString(R.string.nb_comptes, comptes.size)
                }
            }.onFailure { erreur ->
                texteEtat.text = getString(R.string.erreur_reseau, erreur.message ?: "")
                Toast.makeText(this@MainActivity, R.string.erreur_reseau_court, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun rafraichirResume() {
        val total = adapter.totalAffiche()
        texteResume.text = getString(R.string.resume_portefeuille, formatMonnaie.format(total))
    }

    private fun afficherFormulaireAjout() {
        val vue = layoutInflater.inflate(R.layout.popup_compte, null)
        val champSolde = vue.findViewById<TextInputEditText>(R.id.etSolde)
        val radioCourant = vue.findViewById<RadioButton>(R.id.radioCourant)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.nouveau_compte)
            .setView(vue)
            .setPositiveButton(R.string.ajouter) { _, _ ->
                val solde = champSolde.text.toString().toDoubleOrNull() ?: 0.0
                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE
                creerCompte(solde, type)
            }
            .setNegativeButton(R.string.annuler, null)
            .show()
    }

    private fun creerCompte(solde: Double, type: TypeCompte) {
        lifecycleScope.launch {
            val succes = withContext(Dispatchers.IO) {
                runCatching { service.createCompte(solde, type) }.getOrDefault(false)
            }

            if (succes) {
                Toast.makeText(this@MainActivity, R.string.compte_ajoute, Toast.LENGTH_SHORT).show()
                chargerComptes()
            } else {
                Toast.makeText(this@MainActivity, R.string.erreur_ajout, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmerSuppression(compte: ma.ahmedbenidar.soapclient.beans.Compte) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.supprimer_compte)
            .setMessage(getString(R.string.confirmer_suppression, compte.titulaire))
            .setPositiveButton(R.string.supprimer) { _, _ -> supprimerCompte(compte) }
            .setNegativeButton(R.string.annuler, null)
            .show()
    }

    private fun supprimerCompte(compte: ma.ahmedbenidar.soapclient.beans.Compte) {
        val identifiant = compte.id ?: return

        lifecycleScope.launch {
            val succes = withContext(Dispatchers.IO) {
                runCatching { service.deleteCompte(identifiant) }.getOrDefault(false)
            }

            if (succes) {
                adapter.removeCompte(compte)
                rafraichirResume()
                texteEtat.text = getString(R.string.nb_comptes, adapter.itemCount)
                Toast.makeText(this@MainActivity, R.string.compte_supprime, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, R.string.erreur_suppression, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun afficherDetails(compte: ma.ahmedbenidar.soapclient.beans.Compte) {
        val message = getString(
            R.string.details_compte,
            compte.id ?: 0L,
            compte.titulaire,
            formatMonnaie.format(compte.solde),
            compte.type.libelle
        )
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.details_titre)
            .setMessage(message)
            .setPositiveButton(R.string.fermer, null)
            .show()
    }
}
