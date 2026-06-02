# 123 Hub Pare-Brise

Application mobile Android (Kotlin + Jetpack Compose) servant de **hub de guides**
pour le travail terrain chez **123 Pare-Brise**.

L'idée : un écran d'accueil qui regroupe plusieurs « mini-guides » métier.
Chaque guide est une procédure interactive (cases à cocher, champs à remplir,
récapitulatif partageable). De nouveaux guides peuvent être ajoutés facilement.

## Guides disponibles

| Guide | État | Description |
|-------|------|-------------|
| **Sécurisation client** | ✅ Disponible | Trame d'appel de sécurisation : contrôle dossier, assurance, RDV, immobilisation, véhicule de courtoisie. |
| **Chrono séchage colle** | ✅ Disponible | Minuteur du temps de roulage sécurisé + notification quand le véhicule peut rouler. |
| Pose pare-brise | 🔜 À venir | Étapes de dépose / repose et contrôle qualité. |
| Calibrage ADAS | 🔜 À venir | Procédure de recalibrage caméras / capteurs. |
| Procédure assurance | 🔜 À venir | Déclaration de sinistre, dossiers verte / jaune / rouge. |
| Check-list livraison | 🔜 À venir | Contrôle final et restitution du véhicule. |

## Guide « Sécurisation client »

Reprend la trame d'appel de sécurisation :

1. **Contrôle du dossier** — immatriculation / comparé avec la carte grise / assurance
2. **Appel client**
3. **Confirmation du modèle de véhicule**
4. **Assurance** — verte / jaune / rouge
   - Verte → *appli OK ?*
   - Jaune / rouge → *prévenir ~30 min pour déclarer le sinistre*
5. **Carte grise & mémo** — demande de CG + mémo par SMS
6. **Immobilisation** — journée (ADAS) ou 2h30 / 3h
7. **Véhicule de courtoisie** — oui / non → si oui, empreinte CB 300 €
8. **Prise de congé** — reconfirmer date et heure du RDV

Fonctionnalités :

- Champs d'en-tête (client, n° dossier, immatriculation, modèle)
- Barre de progression en bas d'écran
- Bouton **Partager** : génère un récapitulatif texte (SMS, mail, presse-papier…)
- Bouton **Nouveau dossier** : remet la trame à zéro

## Construire / lancer

Pré-requis : **Android Studio** (Ladybug ou plus récent) avec le SDK Android 35.

```bash
# Ouvrir le dossier dans Android Studio, puis Run ▶
# ou en ligne de commande (SDK Android requis) :
./gradlew assembleDebug      # génère app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # installe sur un appareil/émulateur connecté
```

- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material 3
- **minSdk** : 24 (Android 7.0) — **targetSdk / compileSdk** : 35
- **Package** : `com.pb123.hub`

## Architecture

```
app/src/main/java/com/pb123/hub/
├── MainActivity.kt
├── navigation/AppNavHost.kt        # routes + navigation
├── ui/theme/                       # couleurs, typographie, thème (charte 123)
├── hub/
│   ├── Guide.kt                    # modèle + catalogue des guides
│   └── HubScreen.kt                # écran d'accueil
└── guides/securisation/
    ├── SecurisationState.kt        # état + génération du récapitulatif
    ├── SecurisationViewModel.kt
    └── SecurisationScreen.kt
```

### Ajouter un nouveau guide

1. Créer l'écran (ex. `guides/pose/PoseScreen.kt`).
2. Ajouter une route dans `navigation/AppNavHost.kt` (`Routes`) et l'enregistrer dans le `NavHost`.
3. Ajouter une entrée `Guide(...)` dans `hubGuides` (`hub/Guide.kt`) avec la route.
