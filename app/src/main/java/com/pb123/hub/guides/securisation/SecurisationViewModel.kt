package com.pb123.hub.guides.securisation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** Détient et met à jour l'état de la trame de sécurisation. */
class SecurisationViewModel : ViewModel() {

    var state by mutableStateOf(SecurisationState())
        private set

    private fun update(transform: (SecurisationState) -> SecurisationState) {
        state = transform(state)
    }

    // En-tête
    fun setNomClient(v: String) = update { it.copy(nomClient = v) }
    fun setImmatriculation(v: String) = update { it.copy(immatriculation = v) }
    fun setModeleVehicule(v: String) = update { it.copy(modeleVehicule = v) }
    fun setNumeroDossier(v: String) = update { it.copy(numeroDossier = v) }

    // 1. Contrôle du dossier
    fun toggleControleImmatriculation() = update { it.copy(controleImmatriculation = !it.controleImmatriculation) }
    fun toggleControleCompareCarteGrise() = update { it.copy(controleCompareCarteGrise = !it.controleCompareCarteGrise) }
    fun toggleControleAssurance() = update { it.copy(controleAssurance = !it.controleAssurance) }

    // 2 & 3
    fun toggleAppelEffectue() = update { it.copy(appelEffectue = !it.appelEffectue) }
    fun toggleConfirmationModele() = update { it.copy(confirmationModele = !it.confirmationModele) }

    // 4. Assurance
    fun setAssurance(type: AssuranceType) = update {
        // En changeant de couleur on remet les sous-cases à zéro pour éviter les incohérences.
        it.copy(assurance = type, assuranceVerteAppliOk = false, sinistreDeclare = false)
    }
    fun toggleAssuranceVerteAppliOk() = update { it.copy(assuranceVerteAppliOk = !it.assuranceVerteAppliOk) }
    fun toggleSinistreDeclare() = update { it.copy(sinistreDeclare = !it.sinistreDeclare) }

    // 5. Carte grise + mémo
    fun toggleDemandeCarteGrise() = update { it.copy(demandeCarteGrise = !it.demandeCarteGrise) }
    fun toggleMemoSms() = update { it.copy(memoSms = !it.memoSms) }

    // 6. Immobilisation
    fun setImmobilisation(value: Immobilisation) = update { it.copy(immobilisation = value) }

    // 7. Courtoisie
    fun setVehiculeCourtoisie(value: Boolean) = update {
        it.copy(vehiculeCourtoisie = value, empreinteCb = if (value) it.empreinteCb else false)
    }
    fun toggleEmpreinteCb() = update { it.copy(empreinteCb = !it.empreinteCb) }

    // 8. Prise de congé
    fun setRdvDate(v: String) = update { it.copy(rdvDate = v) }
    fun setRdvHeure(v: String) = update { it.copy(rdvHeure = v) }
    fun togglePriseConge() = update { it.copy(priseConge = !it.priseConge) }

    // Notes
    fun setNotes(v: String) = update { it.copy(notes = v) }

    /** Remet la trame à zéro pour un nouveau dossier. */
    fun reset() {
        state = SecurisationState()
    }
}
