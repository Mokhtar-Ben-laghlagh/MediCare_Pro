
# 🏥 MediCare Pro — Système de Gestion des Rendez-Vous Médicaux
**Application desktop Java pour la gestion complète des rendez-vous médicaux**

## Diagramme de classe :

<img width="710" height="419" alt="Capture d&#39;écran 2026-02-28 011605" src="https://github.com/user-attachments/assets/a3e7e67b-ba69-45bb-b5fe-7b279cb371d6" />

## Use case :

![IMG-20260228-WA0002](https://github.com/user-attachments/assets/e269623e-0b56-4bdf-99b4-f68c9297d05f)

## 🏛 Architecture


<img width="1278" height="855" alt="Capture d&#39;écran 2026-02-28 110609" src="https://github.com/user-attachments/assets/9514056c-f8b8-41f4-995b-80c880fff91e" />


## 📽️ Démonstration vidéo




https://github.com/user-attachments/assets/7b8bf60b-9c2b-4539-ab83-e7db9802f093




## 📋 Description du projet

**MediCare Pro** est une application de bureau développée en **Java Swing** permettant à un centre médical de gérer l'ensemble de ses opérations quotidiennes : patients, médecins, rendez-vous et statistiques.

L'application propose une interface graphique moderne avec une authentification sécurisée, un système d'inscription avec confirmation par email, et une réinitialisation de mot de passe via un **code de vérification à 6 chiffres** envoyé sur Gmail.

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
