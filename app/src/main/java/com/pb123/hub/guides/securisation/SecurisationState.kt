package com.pb123.hub.guides.securisation

/** Type d'assurance figurant au dossier (code couleur 123 Pare-Brise). */
enum class AssuranceType(val label: String) {
    VERTE("Verte"),
    JAUNE("Jaune"),
    ROUGE("Rouge"),
}

/** Durée d'immobilisation estimée du véhicule. */
enum class Immobilisation(val label: String) {
    ADAS("Journée (ADAS)"),
    STANDARD("2h30 / 3h"),
}

/**
 * État complet de la trame d'appel de sécurisation client.
 * Tout est immuable : la ViewModel produit une copie à chaque modification.
 */
data class SecurisationState(
    // En-tête dossier
    val nomClient: String = "",
    val immatriculation: String = "",
    val modeleVehicule: String = "",
    val numeroDossier: String = "",

    // 1. Contrôle du dossier
    val controleImmatriculation: Boolean = false,
    val controleCompareCarteGrise: Boolean = false,
    val controleAssurance: Boolean = false,

    // 2. Appel client
    val appelEffectue: Boolean = false,

    // 3. Confirmation modèle véhicule
    val confirmationModele: Boolean = false,

    // 4. Assurance
    val assurance: AssuranceType? = null,
    val assuranceVerteAppliOk: Boolean = false,
    val sinistreDeclare: Boolean = false, // jaune / rouge : prévenir ~30 min

    // 5. Carte grise + mémo
    val demandeCarteGrise: Boolean = false,
    val memoSms: Boolean = false,

    // 6. Immobilisation véhicule
    val immobilisation: Immobilisation? = null,

    // 7. Véhicule de courtoisie
    val vehiculeCourtoisie: Boolean? = null,
    val empreinteCb: Boolean = false,

    // 8. Prise de congé
    val rdvDate: String = "",
    val rdvHeure: String = "",
    val priseConge: Boolean = false,

    // Notes libres
    val notes: String = "",
) {
    /**
     * Liste des points "à cocher" qui comptent dans la progression, en tenant
     * compte des branches conditionnelles (assurance, courtoisie).
     */
    private val checklist: List<Boolean>
        get() = buildList {
            add(controleImmatriculation)
            add(controleCompareCarteGrise)
            add(controleAssurance)
            add(appelEffectue)
            add(confirmationModele)
            add(assurance != null)
            when (assurance) {
                AssuranceType.VERTE -> add(assuranceVerteAppliOk)
                AssuranceType.JAUNE, AssuranceType.ROUGE -> add(sinistreDeclare)
                null -> {}
            }
            add(demandeCarteGrise)
            add(memoSms)
            add(immobilisation != null)
            add(vehiculeCourtoisie != null)
            if (vehiculeCourtoisie == true) add(empreinteCb)
            add(priseConge)
        }

    val completedCount: Int get() = checklist.count { it }
    val totalCount: Int get() = checklist.size
    val progress: Float get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
    val isComplete: Boolean get() = completedCount == totalCount

    /** Récapitulatif texte partageable (SMS, mail, presse-papier...). */
    fun buildRecap(): String {
        fun box(done: Boolean) = if (done) "[x]" else "[ ]"
        fun line(label: String, done: Boolean) = "${box(done)} $label"

        val sb = StringBuilder()
        sb.appendLine("TRAME APPEL SÉCURISATION — 123 Pare-Brise")
        sb.appendLine("=".repeat(38))
        if (nomClient.isNotBlank()) sb.appendLine("Client      : $nomClient")
        if (numeroDossier.isNotBlank()) sb.appendLine("Dossier     : $numeroDossier")
        if (immatriculation.isNotBlank()) sb.appendLine("Immat.      : $immatriculation")
        if (modeleVehicule.isNotBlank()) sb.appendLine("Véhicule    : $modeleVehicule")
        sb.appendLine()

        sb.appendLine("1. Contrôle du dossier")
        sb.appendLine("  " + line("Immatriculation", controleImmatriculation))
        sb.appendLine("  " + line("Comparé avec la carte grise", controleCompareCarteGrise))
        sb.appendLine("  " + line("Assurance", controleAssurance))

        sb.appendLine("2. " + line("Appel client effectué", appelEffectue))
        sb.appendLine("3. " + line("Confirmation modèle véhicule", confirmationModele))

        sb.appendLine("4. Assurance : ${assurance?.label ?: "—"}")
        when (assurance) {
            AssuranceType.VERTE -> sb.appendLine("  " + line("Appli OK", assuranceVerteAppliOk))
            AssuranceType.JAUNE, AssuranceType.ROUGE ->
                sb.appendLine("  " + line("Prévenu ~30 min / sinistre déclaré", sinistreDeclare))
            null -> {}
        }

        sb.appendLine("5. Carte grise & mémo")
        sb.appendLine("  " + line("Demande carte grise", demandeCarteGrise))
        sb.appendLine("  " + line("Mémo envoyé par SMS", memoSms))

        sb.appendLine("6. Immobilisation : ${immobilisation?.label ?: "—"}")

        val courtoisieTxt = when (vehiculeCourtoisie) {
            true -> "Oui"
            false -> "Non"
            null -> "—"
        }
        sb.appendLine("7. Véhicule de courtoisie : $courtoisieTxt")
        if (vehiculeCourtoisie == true) {
            sb.appendLine("  " + line("Empreinte CB 300 €", empreinteCb))
        }

        sb.appendLine("8. Prise de congé")
        val rdv = listOf(rdvDate, rdvHeure).filter { it.isNotBlank() }.joinToString(" à ")
        if (rdv.isNotBlank()) sb.appendLine("  RDV : $rdv")
        sb.appendLine("  " + line("Date et heure reconfirmées", priseConge))

        if (notes.isNotBlank()) {
            sb.appendLine()
            sb.appendLine("Notes :")
            sb.appendLine(notes)
        }

        sb.appendLine()
        sb.appendLine("Progression : $completedCount/$totalCount")
        return sb.toString().trimEnd()
    }
}
