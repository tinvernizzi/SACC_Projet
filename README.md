## SACC_Projet

Les requetes postman se trouvent dans le fichier SaccProjet.postman_collection.json

Listes des requetes possibles :

- Initialiser la base de données : 

Un simple POST va ajouter 2 utilisateurs de chaque catégorie dans la base de données

https://userregistry-dot-polyshare-222618.appspot.com/initDb
POST {}

- Ajouter un utilisateur :

  A l'ajout d'un utilisateur, il sera renvoyé l'ID de l'utilisateur fraichement créé, qui pourra ensuite etre utilisé dans les requetes suivantes, afin de les associer avec ce compte
  
https://userregistry-dot-polyshare-222618.appspot.com/
POST {userName, userEmailAdress}

- Modifier le score d'un utilisateur

https://userregistry-dot-polyshare-222618.appspot.com/score
POST {userId, userScore}

- Telecharger un fichier
Il est possible de mettre l'ID de chaque rang existant afin d'etre redirigé vers la queue associée (push dans le cas des noobs, pull dans le reste)

https://dispatcher-dot-polyshare-222618.appspot.com/download
POST {userId, fileId}


- Upload un fichier

Upload un fichier enverra un mail contenant l'ID du fichier stocké, permettant d'acceder au téléchargement de celui ci

https://uploadhandler-dot-polyshare-222618.appspot.com/service
POST {userId, File}


Nous n'avons pas certaines routes :
  - Réinitialisation des données
  
Nous n'avons pas eu le temps d'implémenter ces fonctionnalités :
  - Suppression des fichiers au bout de X minutes

Note : 
Le mail contient le lien vers la ressource, qui a été créé automatiquement pour google. Cependant, il faudra ajouter "&alt=media" au lien, sans quoi le téléchargement ne se fera pas.

Nous avons fait une adresse mail pour pouvoir receptionner les mails
Il faut se créer un compte via le userregistry, avec cette adresse :

GMAIL : sacc.projet.si5@gmail.com
PWD : SACC_2018_POLY

# Membres

DALLA-NORA Enzo

DEGAND Sébastien 

HUANG Shiyang 

INVERNIZZI Tanguy

