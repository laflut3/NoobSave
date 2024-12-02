# 🚀 Gestion des Conteneurs et Applications

Ce projet fournit un environnement pour :
- Une base de données MariaDB.
- Une interface graphique pour la gestion de la base de données avec Adminer.
- Une application backend Spring Boot.
- Une application frontend React.

Un script Bash automatise l’installation et le démarrage des services.

---

## 🛠 Prérequis

Assurez-vous que les outils suivants sont installés sur votre machine :

- **Docker** : [Installer Docker](https://www.docker.com/get-started)
- **Docker Compose** : [Installer Docker Compose](https://docs.docker.com/compose/install/)
- **Java (JDK 11 ou supérieur)** : [Installer Java](https://adoptopenjdk.net/)
- **Maven** : Si Maven n'est pas installé, utilisez le wrapper inclus (`mvnw`) dans votre projet Spring Boot.
- **Node.js et npm** : [Installer Node.js](https://nodejs.org/)

---

## 📂 Structure du projet

- **Spring Boot App** : Placez votre application backend dans le répertoire configuré dans le script (`SPRING_BOOT_DIR`).
- **React App** : Placez votre application frontend dans le répertoire configuré dans le script (`REACT_APP_DIR`).

---

## 🚀 Instructions

### 1. Rendre le script exécutable

Dans le terminal, donnez les permissions d'exécution au script :
```bash
chmod +x start_app.sh
```
### 2. Rendre le script exécutable

Exécutez le script pour démarrer les conteneurs Docker, l'application Spring Boot et l'application React :

```bash 
./start_app.sh
```
## 🌐 Accès aux applications
- **Adminer** (interface de gestion de la base de données) : http://localhost:8081
- **Application React** (frontend) : http://localhost:3000
- **Application Spring Boot** (backend) : http://localhost:8080

## ⚙️ Configuration supplémentaire

### ariables d'environnement pour MariaDB :

Par défaut, MariaDB est configuré avec les paramètres suivants dans `docker-compose.yml` :

- **Nom d'utilisateur** : `root`
- **Mot de passe** : `password`
- **Nom de la base** : `mydb`

Si nécessaire, modifiez ces valeurs directement dans le fichier `docker-compose.yml`.

## 🛑 Arrêter les services

Pour arrêter les conteneurs et les applications, utilisez :

```bash
docker-compose down
```
Et terminez manuellement les processus Spring Boot et React s’ils sont toujours en cours d’exécution.

## 🤝 Contributions

Les contributions sont les bienvenues ! N’hésitez pas à soumettre des issues ou des pull requests.

## 📄 Licence

Ce projet est sous licence MIT. Consultez le fichier LICENSE pour plus d'informations.

```go

Enregistrez ce contenu dans un fichier nommé `README.md` à la racine de votre projet.
```
