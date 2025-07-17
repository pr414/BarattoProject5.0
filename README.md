# Introduzione al Programma
BarattoProject è un software di supporto al baratto di articoli fisici, a loro volta riconducibili ad un insieme prefissato di categorie. L’applicazione può essere adottata da varie organizzazioni, che sovrintendono al baratto di categorie diverse di articoli e/o operano su piazze diverse.
Il software permette all’utente finale di interagire con inserzioni esistenti di prodotti, proponendo scambi, o di inserire le proprie inserzioni di prodotto che intende barattare, previa descrizione degli stessi.

# Eseguibile
Una volta ottenuto il file compresso contenente il software (.zip o .rar) estrarre il contenuto in una cartella all’interno del disco.
Se in possesso di file .jar, eseguirlo tramite la JVM associata alla versione di Java posseduta.
(Su Windows) Se in possesso di file .exe, è sufficiente eseguirlo tramite il sistema operativo.
Si raccomanda la creazione di una cartella apposita sul disco di installazione per permettere l’esecuzione del programma senza interferire con altri file presenti.
In caso di codice sorgente (.java), è sufficiente generare il bytecode (.class) dalla riga di comando tramite il comando javac [nomefile.java].
Ottenuto così il file nomefile.class, eseguirlo nell’ambiente JRE tramite il comando java [nomefile.class].

# Generalità di Funzionamento
L’applicazione supporta lo scambio di articoli fisici le cui categorie vengono stabilite dal configuratore.
Ogni singola categoria è dotata di un nome e di una descrizione, entrambe stringhe in linguaggio naturale volte a esplicare il significato della categoria stessa.
Una categoria può articolarsi in due o più (sotto)categorie, a loro volta suddividibili ricorsivamente, e così via, secondo una gerarchia ad albero.
Il nome di ciascuna categoria è unico all’interno della gerarchia di appartenenza. La gerarchia comprende solo le categorie che sono di interesse ai fini delle operazioni di baratto che l’organizzazione che si è dotata dell’applicazione intende sostenere.
La medesima applicazione può considerare più gerarchie. Vige il vincolo di unicità del nome di ciascuna categoria radice entro la totalità delle categorie radice di tali gerarchie.
Gli articoli da scambiare devono appartenere solo alle categorie foglia delle gerarchie considerate.
A ciascuna categoria (a qualsiasi livello essa si collochi entro la gerarchia di appartenenza) compete inoltre un insieme (eventualmente vuoto) di campi nativi, la compilazione di ciascuno dei quali da parte del fruitore è obbligatoria o facoltativa.
Ogni categoria radice è dotata dei campi Stato di conservazione (obbligatorio) e Descrizione libera (facoltativo).
Ciascuna categoria figlio eredita la totalità dei campi della categoria padre, e i campi nativi devono necessariamente avere un nome distinto da quello dei campi ereditati.
Per ogni gerarchia, il configuratore associa i luoghi e gli intervalli di tempo per la quale possono avvenire gli scambi.
Ogni articolo da barattare può essere descritto dal fruitore assegnando un valore a ciascuno dei campi (sia nativi sia derivati) che competono alla categoria (foglia) di appartenenza dello stesso. Ogni valore è inserito dal fruitore sotto forma di una stringa di caratteri.
Le offerte possono essere in stato OPEN, TRADING, CLOSED.
Le offerte con rispettivo stato sono visualizzabili sia dal configuratore che dal fruitore, ma solo quest’ultimo può effettuare la creazione di esse ed accordarsi per gli scambi (cambiandone lo stato).
Le specifiche competenze di configuratore e fruitore sono specificate nelle specifiche sezioni di accesso.

# Setup
Al primo avvio dell’applicazione, viene proposta la schermata di login.
L’utente ha la possibilità di accedere con le credenziali temporanee o del configuratore o del fruitore, al seguito di cui viene richiesta la registrazione del ruolo richiesto con username e password, che individueranno univocamente l’utente.
Il client è pensato per essere eseguito e salvare dati in locale.
Una volta scelte le credenziali compatibilmente con i parametri di lunghezza, viene avviata la sessione ed è possibile interagire con il sistema in base al tipo di login effettuato.

Il funzionamento nel dettaglio è disponibile sotto "Manuale d'Installazione ed Uso - BarattoProject".
