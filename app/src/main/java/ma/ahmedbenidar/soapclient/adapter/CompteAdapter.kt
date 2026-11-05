package ma.ahmedbenidar.soapclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import ma.ahmedbenidar.soapclient.R
import ma.ahmedbenidar.soapclient.beans.Compte
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter du RecyclerView des comptes.
 * Le formatage monetaire et la couleur de la puce dependent du type de compte,
 * ce qui donne une lecture immediate de la liste.
 */
class CompteAdapter : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {

    private val comptes = mutableListOf<Compte>()
    private val formatMonnaie = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    private val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    var onDeleteClick: ((Compte) -> Unit)? = null
    var onEditClick: ((Compte) -> Unit)? = null

    fun updateComptes(nouveaux: List<Compte>) {
        comptes.clear()
        comptes.addAll(nouveaux)
        notifyDataSetChanged()
    }

    fun removeCompte(compte: Compte) {
        val position = comptes.indexOf(compte)
        if (position >= 0) {
            comptes.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun totalAffiche(): Double = comptes.sumOf { it.solde }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val vue = LayoutInflater.from(parent.context).inflate(R.layout.item_compte, parent, false)
        return CompteViewHolder(vue)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.lier(comptes[position])
    }

    override fun getItemCount(): Int = comptes.size

    inner class CompteViewHolder(vue: View) : RecyclerView.ViewHolder(vue) {

        private val textId: TextView = vue.findViewById(R.id.textId)
        private val textTitulaire: TextView = vue.findViewById(R.id.textTitulaire)
        private val textSolde: TextView = vue.findViewById(R.id.textSolde)
        private val textType: Chip = vue.findViewById(R.id.textType)
        private val textDate: TextView = vue.findViewById(R.id.textDate)
        private val boutonModifier: MaterialButton = vue.findViewById(R.id.btnEdit)
        private val boutonSupprimer: MaterialButton = vue.findViewById(R.id.btnDelete)

        fun lier(compte: Compte) {
            textId.text = "Compte ${compte.id}"
            textTitulaire.text = compte.titulaire
            textSolde.text = "${formatMonnaie.format(compte.solde)} DH"
            textType.text = compte.type.libelle
            val teinte = android.content.res.ColorStateList.valueOf(compte.type.couleur.toInt())
            textType.chipStrokeColor = teinte
            textType.chipStrokeWidth = 3f
            textType.chipBackgroundColor = teinte
            textType.setTextColor(android.graphics.Color.WHITE)
            textDate.text = "Ouvert le ${formatDate.format(compte.dateCreation)}"

            boutonModifier.setOnClickListener { onEditClick?.invoke(compte) }
            boutonSupprimer.setOnClickListener { onDeleteClick?.invoke(compte) }
        }
    }
}
