package com.pb123.hub.guides.securisation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pb123.hub.ui.theme.OkGreen
import com.pb123.hub.ui.theme.PBRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurisationScreen(
    onBack: () -> Unit,
    viewModel: SecurisationViewModel = viewModel(),
) {
    val state = viewModel.state
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Sécurisation client", fontWeight = FontWeight.Bold)
                        Text(
                            "Trame d'appel",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = "Nouveau dossier", tint = Color.White)
                    }
                    IconButton(onClick = { shareRecap(context, state.buildRecap()) }) {
                        Icon(Icons.Filled.Share, contentDescription = "Partager", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        },
        bottomBar = { ProgressBottomBar(state) },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = innerPadding.calculateTopPadding() + 10.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { DossierHeaderCard(state, viewModel) }

            item {
                SectionCard("1", "Contrôle du dossier") {
                    CheckRow("Immatriculation", state.controleImmatriculation, viewModel::toggleControleImmatriculation)
                    CheckRow("Comparé avec la carte grise", state.controleCompareCarteGrise, viewModel::toggleControleCompareCarteGrise)
                    CheckRow("Assurance", state.controleAssurance, viewModel::toggleControleAssurance)
                }
            }

            item {
                SectionCard("2", "Appel client") {
                    CheckRow("Appel client effectué", state.appelEffectue, viewModel::toggleAppelEffectue)
                }
            }

            item {
                SectionCard("3", "Confirmation véhicule") {
                    CheckRow("Confirmation du modèle de véhicule", state.confirmationModele, viewModel::toggleConfirmationModele)
                }
            }

            item {
                SectionCard("4", "Assurance") {
                    Text(
                        "Type d'assurance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssuranceType.entries.forEach { type ->
                            FilterChip(
                                selected = state.assurance == type,
                                onClick = { viewModel.setAssurance(type) },
                                label = { Text(type.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = assuranceColor(type),
                                    selectedLabelColor = Color.White,
                                ),
                            )
                        }
                    }
                    when (state.assurance) {
                        AssuranceType.VERTE -> {
                            Spacer(Modifier.height(4.dp))
                            CheckRow("Appli OK ?", state.assuranceVerteAppliOk, viewModel::toggleAssuranceVerteAppliOk)
                        }
                        AssuranceType.JAUNE, AssuranceType.ROUGE -> {
                            Spacer(Modifier.height(4.dp))
                            CheckRow(
                                "Prévenir ~30 min pour déclarer le sinistre",
                                state.sinistreDeclare,
                                viewModel::toggleSinistreDeclare,
                            )
                        }
                        null -> {}
                    }
                }
            }

            item {
                SectionCard("5", "Carte grise & mémo") {
                    CheckRow("Demande de carte grise", state.demandeCarteGrise, viewModel::toggleDemandeCarteGrise)
                    CheckRow("Mémo envoyé par SMS", state.memoSms, viewModel::toggleMemoSms)
                }
            }

            item {
                SectionCard("6", "Immobilisation du véhicule") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Immobilisation.entries.forEach { option ->
                            FilterChip(
                                selected = state.immobilisation == option,
                                onClick = { viewModel.setImmobilisation(option) },
                                label = { Text(option.label) },
                            )
                        }
                    }
                }
            }

            item {
                SectionCard("7", "Véhicule de courtoisie") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.vehiculeCourtoisie == true,
                            onClick = { viewModel.setVehiculeCourtoisie(true) },
                            label = { Text("Oui") },
                        )
                        FilterChip(
                            selected = state.vehiculeCourtoisie == false,
                            onClick = { viewModel.setVehiculeCourtoisie(false) },
                            label = { Text("Non") },
                        )
                    }
                    if (state.vehiculeCourtoisie == true) {
                        Spacer(Modifier.height(4.dp))
                        CheckRow("Empreinte CB 300 €", state.empreinteCb, viewModel::toggleEmpreinteCb)
                    }
                }
            }

            item {
                SectionCard("8", "Prise de congé") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.rdvDate,
                            onValueChange = viewModel::setRdvDate,
                            label = { Text("Date RDV") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = state.rdvHeure,
                            onValueChange = viewModel::setRdvHeure,
                            label = { Text("Heure") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    CheckRow("Date et heure du RDV reconfirmées", state.priseConge, viewModel::togglePriseConge)
                }
            }

            item {
                SectionCard("📝", "Notes") {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = viewModel::setNotes,
                        label = { Text("Remarques, infos client...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Nouveau dossier ?") },
            text = { Text("La trame en cours sera effacée. Continuer ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.reset()
                    showResetDialog = false
                }) { Text("Effacer", color = PBRed) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Annuler") }
            },
        )
    }
}

@Composable
private fun DossierHeaderCard(state: SecurisationState, vm: SecurisationViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = state.nomClient,
                onValueChange = vm::setNomClient,
                label = { Text("Nom du client") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.numeroDossier,
                    onValueChange = vm::setNumeroDossier,
                    label = { Text("N° dossier") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = state.immatriculation,
                    onValueChange = vm::setImmatriculation,
                    label = { Text("Immat.") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
            OutlinedTextField(
                value = state.modeleVehicule,
                onValueChange = vm::setModeleVehicule,
                label = { Text("Modèle du véhicule") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SectionCard(
    number: String,
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(number, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun CheckRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ProgressBottomBar(state: SecurisationState) {
    val color = if (state.isComplete) OkGreen else MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (state.isComplete) "Trame complète ✓" else "Progression",
                style = MaterialTheme.typography.titleMedium,
                color = color,
            )
            Text(
                text = "${state.completedCount}/${state.totalCount}",
                style = MaterialTheme.typography.titleMedium,
                color = color,
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

private fun assuranceColor(type: AssuranceType): Color = when (type) {
    AssuranceType.VERTE -> OkGreen
    AssuranceType.JAUNE -> com.pb123.hub.ui.theme.WarnAmber
    AssuranceType.ROUGE -> PBRed
}

private fun shareRecap(context: android.content.Context, recap: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Sécurisation client — 123 Pare-Brise")
        putExtra(Intent.EXTRA_TEXT, recap)
    }
    context.startActivity(Intent.createChooser(intent, "Partager le récapitulatif"))
}
