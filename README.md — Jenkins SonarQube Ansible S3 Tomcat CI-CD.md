# 🚀 End-to-End CI/CD Pipeline with Jenkins, SonarQube, AWS S3 & Ansible

Ce projet met en œuvre une chaîne **CI/CD complète et automatisée** permettant de construire, analyser, stocker et déployer une application Java Web sur plusieurs serveurs **Apache Tomcat**.

L'objectif est de reproduire un environnement DevOps proche d'un scénario de production, en intégrant :

**GitHub → Jenkins → Maven → SonarQube → AWS S3 → Ansible → Tomcat**

---

## 📐 Architecture du projet

### Workflow CI/CD

```text
                    ┌─────────────────┐
                    │     Developer   │
                    │                 │
                    │     GitHub      │
                    └────────┬────────┘
                             │
                             │ Git Push
                             ▼
                    ┌─────────────────┐
                    │     Jenkins     │
                    │                 │
                    │ CI/CD Pipeline  │
                    └────────┬────────┘
                             │
                 ┌───────────┴───────────┐
                 │                       │
                 ▼                       ▼
          ┌──────────────┐       ┌──────────────┐
          │    Maven     │       │  SonarQube   │
          │              │       │              │
          │ Build WAR    │       │ Code Quality │
          └──────┬───────┘       └──────┬───────┘
                 │                       │
                 └───────────┬───────────┘
                             │
                        Quality Gate
                             │
                             ▼
                    ┌─────────────────┐
                    │     AWS S3      │
                    │                 │
                    │  WAR Artifact   │
                    └────────┬────────┘
                             │
                             │ Download
                             ▼
                    ┌─────────────────┐
                    │     Ansible     │
                    │                 │
                    │ Configuration   │
                    │ & Deployment    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │ Tomcat 1 │   │ Tomcat 2 │   │ Tomcat 3 │
        │  Prod 1  │   │  Prod 2  │   │  Prod 3  │
        └──────────┘   └──────────┘   └──────────┘
```

---

# 🔄 Pipeline CI/CD

Le pipeline automatise les différentes étapes du cycle de livraison :

### 1. Source Code Management

Le développeur pousse le code source de l'application Java sur **GitHub**.

```text
Developer
    │
    ▼
  GitHub
```

Jenkins récupère ensuite automatiquement le projet.

---

### 2. Checkout

Jenkins récupère le code source depuis le repository Git.

```text
GitHub
   │
   ▼
Jenkins Workspace
```

---

### 3. Build avec Maven

Jenkins utilise **Apache Maven** pour compiler l'application et générer le fichier `.war`.

Exemple :

```bash
mvn clean package
```

Le résultat est généré dans :

```text
target/*.war
```

---

### 4. Analyse avec SonarQube

Le code source est analysé par **SonarQube** afin d'identifier notamment :

- Bugs
- Vulnerabilities
- Code Smells
- Duplication
- Complexité
- Couverture des tests

Le pipeline peut ensuite vérifier le **Quality Gate** avant de poursuivre le déploiement.

```text
Source Code
     │
     ▼
 SonarQube
     │
     ▼
 Quality Gate
     │
     ├── FAILED → Pipeline stopped
     │
     └── PASSED → Continue
```

---

### 5. Stockage de l'artefact dans AWS S3

Après validation du build et de la qualité du code, Jenkins envoie le fichier `.war` dans un bucket **Amazon S3**.

```text
Jenkins
   │
   │ WAR
   ▼
AWS S3
```

S3 permet de conserver l'artefact généré et de faciliter sa récupération lors du déploiement.

---

### 6. Provisioning avec Ansible

Ansible est utilisé pour automatiser la configuration des serveurs Tomcat.

Le playbook peut notamment :

- Installer Java
- Installer Apache Tomcat
- Configurer Tomcat
- Créer les utilisateurs nécessaires
- Configurer le service systemd
- Démarrer Tomcat

---

### 7. Déploiement de l'application

Ansible récupère ensuite l'artefact `.war` depuis AWS S3 et le déploie sur les serveurs Tomcat.

```text
                    AWS S3
                      │
                      │ WAR
                      ▼
                   Ansible
                      │
          ┌───────────┼───────────┐
          │           │           │
          ▼           ▼           ▼
       Tomcat 1    Tomcat 2    Tomcat 3
        Prod 1      Prod 2      Prod 3
```

L'application est ainsi déployée sur plusieurs serveurs.

---

# 🛠️ Technologies utilisées

| Catégorie | Technologie | Rôle |
|---|---|---|
| SCM | Git / GitHub | Gestion du code source |
| CI/CD | Jenkins | Orchestration du pipeline |
| Build | Apache Maven | Compilation et génération du WAR |
| Code Quality | SonarQube | Analyse statique du code |
| Artifact Storage | AWS S3 | Stockage du WAR |
| Automation | Ansible | Provisioning et déploiement |
| Application Server | Apache Tomcat | Exécution de l'application Java |
| Cloud | AWS EC2 | Infrastructure serveur |
| Operating System | Linux | Système des serveurs |

---

# 📁 Structure du projet

```text
Jenkins-SonarQube-Ansible-S3-Pipeline/
│
├── Architecture.png
│
├── Jenkinsfile.groovy
│
├── Ansible_Tomcat-Installatins-Playbook.yml
│
├── deploy.yml
│
└── Screenshot/
    │
    ├── Ansible-playboot-exc.png
    ├── App-on-S3.png
    ├── App-on-Server1.png
    ├── App-on-Server2.png
    ├── App-Testing-On-SonarQube.png
    ├── Ec2s.png
    └── Result-on-jenkins.png
```

---

# ⚙️ Jenkins Pipeline

Le fichier :

```text
Jenkinsfile.groovy
```

centralise l'ensemble des étapes du processus CI/CD.

### Pipeline logique

```text
Checkout
   │
   ▼
Maven Build
   │
   ▼
SonarQube Analysis
   │
   ▼
Quality Gate
   │
   ▼
Generate WAR
   │
   ▼
Upload WAR → S3
   │
   ▼
Ansible Provisioning
   │
   ▼
Ansible Deployment
   │
   ▼
Tomcat Servers
```

---

# 🔐 Gestion des credentials

Les informations sensibles ne doivent pas être stockées directement dans le repository.

Les credentials peuvent être configurés dans Jenkins Credentials :

```text
Jenkins Credentials
│
├── AWS Credentials
├── SSH Credentials
└── SonarQube Token
```

Exemples de secrets à protéger :

- AWS Access Key
- AWS Secret Access Key
- SSH Private Key
- SonarQube Token

---

# ☁️ Infrastructure AWS

Le projet utilise plusieurs instances EC2.

Exemple d'organisation :

```text
                 AWS Cloud
                     │
          ┌──────────┴──────────┐
          │                     │
       Jenkins               SonarQube
          │
          │
       Ansible
          │
    ┌─────┼─────┐
    │     │     │
    ▼     ▼     ▼
 Prod1  Prod2  Prod3
    │     │     │
    ▼     ▼     ▼
 Tomcat Tomcat Tomcat
```

AWS S3 est utilisé comme stockage centralisé des artefacts.

---

# 📸 Preuves du projet

## 1. Infrastructure AWS EC2

La capture montre les instances EC2 utilisées pour l'environnement CI/CD.

**Screenshot :**

```text
Screenshot/Ec2s.png
```

---

## 2. Analyse SonarQube

La capture montre l'analyse de qualité effectuée sur le projet.

**Screenshot :**

```text
Screenshot/App-Testing-On-SonarQube.png
```

---

## 3. Artefact dans AWS S3

Le fichier `.war` généré par Jenkins est stocké dans le bucket S3.

**Screenshot :**

```text
Screenshot/App-on-S3.png
```

---

## 4. Déploiement avec Ansible

La capture montre l'exécution réussie des playbooks Ansible.

**Screenshot :**

```text
Screenshot/Ansible-playboot-exc.png
```

---

## 5. Pipeline Jenkins

La capture montre l'exécution et le résultat du pipeline Jenkins.

**Screenshot :**

```text
Screenshot/Result-on-jenkins.png
```

---

## 6. Application déployée

L'application est disponible sur plusieurs serveurs Tomcat.

### Production Server 1

```text
Screenshot/App-on-Server1.png
```

### Production Server 2

```text
Screenshot/App-on-Server2.png
```

---

# 🚀 Prérequis

Pour reproduire ce projet, les composants suivants sont nécessaires :

### Jenkins

- Jenkins opérationnel
- Pipeline Plugin
- Git Plugin
- Maven
- SonarQube Scanner
- AWS integration
- Ansible

### SonarQube

Un serveur SonarQube opérationnel et accessible depuis Jenkins.

### AWS

- Compte AWS
- Instances EC2
- Bucket S3
- IAM permissions
- Security Groups correctement configurés

### Ansible

Un nœud de contrôle Ansible capable de communiquer avec les serveurs Tomcat via SSH.

### Tomcat

Plusieurs serveurs Linux exécutant Apache Tomcat.

---

# ▶️ Installation et utilisation

## 1. Cloner le projet

```bash
git clone https://github.com/<USERNAME>/<REPOSITORY>.git
cd Jenkins-SonarQube-Ansible-S3-Pipeline
```

---

## 2. Configurer Jenkins

Créer un nouveau **Pipeline Job** dans Jenkins.

Configurer le repository Git :

```text
GitHub Repository
        │
        ▼
    Jenkins
        │
        ▼
Jenkinsfile.groovy
```

---

## 3. Configurer SonarQube

Configurer le serveur SonarQube dans Jenkins et ajouter les credentials nécessaires.

---

## 4. Configurer AWS

Configurer les credentials AWS dans Jenkins et vérifier que le rôle ou l'utilisateur IAM possède les permissions nécessaires pour accéder au bucket S3.

---

## 5. Configurer Ansible

Définir l'inventaire des serveurs Tomcat.

Exemple :

```ini
[tomcat_servers]
prod1
prod2
prod3
```

Puis tester la connexion :

```bash
ansible all -m ping
```

---

## 6. Exécuter le pipeline

Lancer le pipeline depuis Jenkins :

```text
Build Now
```

Le pipeline exécute automatiquement :

```text
GitHub
  ↓
Checkout
  ↓
Maven
  ↓
SonarQube
  ↓
Quality Gate
  ↓
S3
  ↓
Ansible
  ↓
Tomcat
```

---

# 🎯 Objectifs DevOps démontrés

Ce projet démontre la maîtrise pratique de plusieurs concepts DevOps :

- **Continuous Integration**
- **Continuous Delivery / Deployment**
- **Pipeline as Code**
- **Infrastructure Automation**
- **Configuration Management**
- **Code Quality**
- **Artifact Management**
- **Cloud Infrastructure**
- **Linux Administration**
- **AWS EC2**
- **AWS S3**
- **Ansible Automation**
- **Java Application Deployment**
- **Apache Tomcat**

---

# 📌 Compétences acquises

À travers ce projet, les compétences suivantes sont mises en pratique :

```text
Git/GitHub
     │
     ▼
Jenkins
     │
     ├── Maven
     │
     └── SonarQube
             │
             ▼
          AWS S3
             │
             ▼
          Ansible
             │
             ▼
        Apache Tomcat
```

Ce projet constitue ainsi une démonstration pratique d'une chaîne **CI/CD Cloud + Automation**, combinant Jenkins, SonarQube, AWS et Ansible.

---

# 👨‍💻 Author

**Boubacar Djibrilla**

Software Engineering | AWS | DevOps | Cloud

GitHub: `boubacardjibrilla`