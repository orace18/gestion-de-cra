# CBX — Application de Gestion des CRA

API REST backend pour la gestion des Comptes Rendus d'Activité (CRA) des consultants CBX.

---

## Stack technique

| Technologie | Version | Rôle |
|---|---|---|
| Java | 21 | Langage |
| Spring Boot | 3.5.11 | Framework principal |
| Spring Security | 6.x | Authentification & autorisation |
| Spring Data JPA | 3.x | Accès base de données |
| H2 | Embarquée | Base de données (dev) |
| jjwt | 0.12.6 | Génération et validation JWT |
| springdoc-openapi | 2.5.0 | Documentation Swagger |
| Lombok | Latest | Réduction du boilerplate |

---

## Lancer le projet

### Prérequis
- Java 21+
- Maven 3.8+

### Démarrage

```bash
# Cloner le projet
git clone <https://github.com/orace18/gestion-de-cra.git>
cd cra

# Lancer l'application
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080`

---

## URLs importantes

| URL | Description |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Documentation interactive de l'API |
| `http://localhost:8080/h2-console` | Console base de données H2 |

**Connexion H2 console :**
- JDBC URL : `jdbc:h2:mem:cra_db`
- Username : `sa`
- Password : *(vide)*

---

## Comptes de démo

Les comptes suivants sont créés automatiquement au démarrage :

| Email | Mot de passe | Rôle |
|---|---|---|
| admin@cbx.fr | admin123 | Admin |


---

## Authentification

L'API utilise JWT (JSON Web Token). Pour accéder aux routes protégées :

**1. Se connecter**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@cbx.fr",
  "password": "admin123"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@cbx.fr",
  "role": "ADMIN"
}
```

**2. Utiliser le token**

Ajouter le header suivant à toutes les requêtes :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Endpoints

### Auth
| Méthode | Route | Description | Accès |
|---|---|---|---|
| POST | `/api/auth/login` | Se connecter | Public |

### Collaborateurs
| Méthode | Route | Description | Accès |
|---|---|---|---|
| GET | `/api/collaborateurs` | Lister tous les collaborateurs | Admin |
| GET | `/api/collaborateurs/{id}` | Récupérer un collaborateur | Admin |
| POST | `/api/collaborateurs` | Créer un collaborateur | Admin |
| PUT | `/api/collaborateurs/{id}` | Modifier un collaborateur | Admin |
| PATCH | `/api/collaborateurs/{id}/activation` | Activer / désactiver | Admin |

### Missions
| Méthode | Route | Description | Accès |
|---|---|---|---|
| GET | `/api/missions` | Lister toutes les missions | Admin |
| GET | `/api/missions/{id}` | Récupérer une mission | Admin |
| POST | `/api/missions` | Créer une mission | Admin |
| PUT | `/api/missions/{id}` | Modifier une mission | Admin |
| DELETE | `/api/missions/{id}` | Supprimer une mission | Admin |
| POST | `/api/missions/assign` | Affecter un collaborateur | Admin |

### CRA — Collaborateur
| Méthode | Route | Description | Accès |
|---|---|---|---|
| GET | `/api/cra?mois=3&annee=2025` | Récupérer mon CRA du mois | Collaborateur |
| GET | `/api/cra/mes-cras` | Récupérer tous mes CRA | Collaborateur |
| PUT | `/api/cra/{id}/remplir-mois` | Remplir tout le mois en 1 clic | Collaborateur |
| PUT | `/api/cra/{id}/jour` | Modifier un jour précis | Collaborateur |
| POST | `/api/cra/{id}/soumettre` | Soumettre le CRA | Collaborateur |

### CRA — Admin
| Méthode | Route | Description | Accès |
|---|---|---|---|
| GET | `/api/admin/cra` | Lister tous les CRA | Admin |
| GET | `/api/admin/cra/a-valider` | Lister les CRA en attente | Admin |
| POST | `/api/admin/cra/{id}/approuver` | Valider un CRA | Admin |
| POST | `/api/admin/cra/{id}/rejeter` | Rejeter un CRA (motif requis) | Admin |
| POST | `/api/admin/cra/{id}/invalider` | Invalider un CRA (motif requis) | Admin |

---

## Règles métier

### Fenêtre de déclaration
Un collaborateur ne peut soumettre son CRA qu'entre le **22 et le 28 du mois** (timezone Europe/Paris).

### États d'un CRA
```
DRAFT → SUBMITTED → APPROVED
                 ↘ REJECTED → SUBMITTED (correction)
APPROVED → INVALIDATED
```

| État | Modifiable |
|---|---|
| DRAFT | Oui (dans la fenêtre 22-28) |
| SUBMITTED | Non |
| APPROVED | Non |
| REJECTED | Oui (toujours, pour correction) |
| INVALIDATED | Non |

### Motif obligatoire
Le motif est obligatoire lors d'un rejet ou d'une invalidation.

### Intercontrat
Si un collaborateur n'a aucune mission active, son CRA est automatiquement créé en intercontrat.

---

## Structure du projet

```
src/main/java/com/orace/cra/
├── config/                    # JWT, Security, Scheduler, DataInitializer
├── domain/
│   ├── controllers/           # Endpoints REST
│   ├── services/              # Logique métier
│   └── model/
│       ├── dtos/
│       │   ├── request/       # Corps des requêtes entrantes
│       │   └── response/      # Corps des réponses sortantes
│       ├── entities/          # Entités JPA (tables)
│       ├── enums/             # Role, CraStatus, ContratType, Seniorite, DayType
│       └── repository/        # Accès base de données
└── exceptions/                # BusinessException, GlobalExceptionHandler
```

---

## Tâches automatiques (Scheduler)

| Déclenchement | Action |
|---|---|
| 1er du mois à 06h00 | Création automatique des CRA en DRAFT pour tous les collaborateurs actifs |
| 28 du mois à 18h00 | Alerte admin si des CRA ne sont pas encore soumis |

---
