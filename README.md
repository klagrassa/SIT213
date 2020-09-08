# Simulateur de chaîne de transmission
SIT 213
TRITARELLI Vincent, LA GRASSA Karl
v0.1

Description
Ce logiciel est un simulateur de chaîne de transmission. Il permet de modéliser
différents types de chaînes de transmissions à l'aide des options disponibles plus bas.
Les éléments de la chaîne sont la source, le transmetteur et la destination. Des sondes sont présentes afin de proposer une exploitation des résultats sous forme graphique. 


Installation
Décompressez l'archive puis lancez le script "compile". Vous pouvez ensuite lancer le script "genDoc" pour générer la doc.


Mise en route 
Le script "simulateur" permet de lancer une simulation. Il y a plusieurs arguments disponibles.

OPTIONS :

-mess  m
	précise le message ou la longueur du message à émettre: Si m est une suite de 0 et de 1 de longueur au moins égale à 7,  m est le message à émettre.Si m comporte au plus 6 chiffres décimaux et correspond à la représentation en base 10 d'un entier,cet entier est la longueur du message que le simulateur doit générer et transmettre.Par défaut le simulateur doit générer et transmettre un message de longueur 100. 

-s 
	indique l’utilisation des sondes.Par défaut le simulateur n’utilise pas de sondes

-seed v
	précise l’utilisation d’une semence pour l’initialisation des générateurs aléatoires du simulateur.v doit être une valeur entière. L’utilisation d’une semence permet de rejouer à l’identique une simulation(à la fois pour le message émis et le bruitage s’il est activé).Par défaut le simulateur n’utilise pas de semence pour initialiser ses générateurs aléatoires.

-form  f
	utilisation d’une transmission analogique, f précise la forme d’onde.
Le paramètre f peut prendre les valeurs suivantes:
	- NRZ    forme d'onde rectangulaire 
	- NRZT  forme d'onde trapézoïdale (temps de montée ou de descente à 1/3 du temps bit)
	- RZ      forme d'onde impulsionnelle (amplitude min sur le premier et dernier tiers du temps bit,        impulsionnelle sur le tiers central avec un max au milieu du temps bit égal à l’amplitude max). Par défaut le simulateur doit utiliser la forme d’onde RZ pour le signal analogique.

-nbEch  ne
	utilisation d’une transmission analogique, ne précise le nombre d’échantillons par bit.ne  doit être une valeur entière positive.Par défaut le simulateur doit utiliser 30 échantillons par bit.

-ampl  min max
	utilisation d’une transmission analogique,  min et max précisent  l’amplitude  min et max du signal.min et max doivent être des valeurs flottantes (avec min < max).Par défaut le simulateur doit utiliser  0.0f comme min et 1.0f comme max

-snrpb  s
	utilisation d’une transmission analogique bruitée, s est la valeur du rapport signal sur bruit par bit (Eb/N0en dB).Le paramètre s doit être une valeur flottante.

-ti dt ar
	utilisation d’une transmission analogique multitrajet (‘trajet indirect’) dt précise le décalage temporel (en nombre d’échantillons) entre le trajet  indirect du signal et le trajet direct, ar précise l’amplitude relative du signal du trajet indirect par rapport à celle du signal du trajet direct.Les dt et ar doivent être respectivement une valeur entière et une valeur flottante. Plusieurs couples de valeurs (5 au maximum) peuvent être mis après le ‘-ti’ pour simuler autant de trajets indirects.Par défaut le simulateur ne simule pas de trajets indirects, ce qui correspond à des valeurs 0 et 0.0fpour tous les trajets indirects.

-codeur 
	précise l’utilisation d’un codeur (en émission) et d’un décodeur (en réception). Par défaut le simulateur n’utilise pas de codage de canal.
