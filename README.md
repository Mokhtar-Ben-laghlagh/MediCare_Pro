
# 🏥 MediCare Pro — Système de Gestion des Rendez-Vous Médicaux
**Application desktop Java pour la gestion complète des rendez-vous médicaux**

## 📋 Description du projet

**MediCare Pro** est une application de bureau développée en **Java Swing** permettant à un centre médical de gérer l'ensemble de ses opérations quotidiennes : patients, médecins, rendez-vous et statistiques.

L'application propose une interface graphique moderne avec une authentification sécurisée, un système d'inscription avec confirmation par email, et une réinitialisation de mot de passe via un **code de vérification à 6 chiffres** envoyé sur Gmail.

## Diagramme de classe :

<img width="710" height="419" alt="Capture d&#39;écran 2026-02-28 011605" src="https://github.com/user-attachments/assets/df14c79c-10f3-4263-953e-81aff746515f" />

## Use case :

<img width="1430" height="392" alt="image" src="https://github.com/user-attachments/assets/272661df-a7c5-4fdf-af87-d5ffb6971d38" />

## 🏛 Architecture


![IMG-20260228-WA0095](https://github.com/user-attachments/assets/44074651-509d-4610-a06b-218d8b33bb81)



## 📽️ Démonstration vidéo




https://github.com/user-attachments/assets/7b8bf60b-9c2b-4539-ab83-e7db9802f093





---

## ✨ Fonctionnalités

### 🔐 Authentification & Sécurité
- Connexion sécurisée par login + mot de passe
- **Inscription** avec envoi d'un email de bienvenue (Gmail SMTP)
- **Mot de passe oublié** en 3 étapes sécurisées :
  1. Saisie du login
  2. **Code à 6 chiffres** reçu par email (expire en 15 min)
  3. Création du nouveau mot de passe (uniquement si code valide)
- Indicateur de force du mot de passe en temps réel

### 👤 Gestion des Patients
- Ajouter, modifier, supprimer un patient
- Champs : nom complet, âge, ville
- Tableau avec lignes alternées et sélection visuelle
- Barre de recherche intégrée

### 👨‍⚕️ Gestion des Médecins
- Ajouter, modifier, supprimer un médecin
- Champs : nom complet, spécialité, téléphone
- Interface identique à la gestion des patients

### 📅 Gestion des Rendez-Vous
- Planifier un rendez-vous (patient + médecin + date + acte + tarif)
- Modifier ou annuler un rendez-vous existant
- Affichage du tarif formaté en Dirhams (MAD)
