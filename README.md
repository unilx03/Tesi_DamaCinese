# Tesi di Laurea Li Xu
Esplorazione totale dell'albero di gioco della Dama Cinese per risolvere completamente il gioco

## Regole di gioco complete
La Dama Cinese `e un gioco da tavolo strategico da 2-3-4-6 giocatori che si svolge su
un peculiare tabellone a forma di stella a sei punte. Ogni giocatore occupa con le sue
pedine uno dei triangoli che segna la sua zona iniziale. Lo scopo del gioco `e essere il
primo giocatore a spostare tutte le proprie pedine attraverso il tabellone fino al triangolo
opposto da quello iniziale.
I giocatori si alternano muovendo una sola pedina del proprio colore per turno. In un
turno, una pedina può essere semplicemente spostata in un foro adiacente oppure pu`o
eseguire uno o pi`u salti sopra altre pedine.
Nel caso di un salto, ogni salto deve avvenire sopra una pedina adiacente e atterrare
nel foro libero immediatamente oltre la direzione della pedina che si sta scavalcando.
Ogni salto pu`o essere eseguito sopra una qualsiasi pedina adiacente, che sia del giocatore
stesso o di un avversario. Pi`u salti possono effettuati nello stesso turno anche cambiando
direzione purch´e rimangano spostamenti validi. Le pedine non vengono mai rimosse dal
tabellone ed `e possibile spostarle avanti e indietro, sia tramite spostamenti adiacenti o
salti. Una volta che una pedina ha raggiunto il triangolo opposto non pu`o pi`u uscirne:
pu`o solo muoversi all’interno di quel triangolo.

Sono presenti una serie di ”house rules” che influenzano l’esplorazione dell’albero di gioco
rispetto ad altre implementazioni dello stesso gioco.
Una pedina non pu`o sostare in un triangolo che rappresenta la zona iniziale o di
traguardo di un altro giocatore: `e possibile attraversare i fori come zona di passaggio
durante i salti.
Quando una partita inizia senza raggiungere il numero massimo di 6 giocatori, i
triangoli rimanenti rimangono vuoti e possono essere utilizzati dalle pedine in gioco per
gli spostamenti (senza quindi considerare la restrizione precedente).
Per evitare situazioni in cui un giocatore non pu`o vincere perch´e una pedina avversaria
occupa uno dei fori nel triangolo di destinazione, un giocatore vince nel caso in cui tutte
le posizioni all’interno del triangolo di traguardo sono occupati ed `e presente almeno una
delle sue pedine.

## Parametri modificabili
Tester [OPTIONS] <Player Count> <Pieces> <MaxDepth>
Player Count: 2/3/4/6 (al momento solo 2)
Pieces: 3/6/10 (modalità 3 pedine in esame)
MaxDepth: livello di profondità dell'esplorazione dell'albero di gioco, 0 per profondità massima fino a considerare ogni possibilità

OPTIONS
-v: stampa dettagliata dei progressi
-g: versione giocabile con GUI tra umano e agente AI

Esecuzione tramite nohup

### Task List
Implementazione 2 giocatori
- Esplorare tutti i possibili stati per 2 giocatori con 3 pedine su tabella da 25 possibili posizioni
  
Implementazione multi-giocatori
- Maxn per più giocatori
- Versione con GUI per più di 2 giocatori
- Controllo condizioni di vittoria per zone ai lati
- Controllo movimenti pedine in altre zone solo come passaggio
- Move ordering per pedine ai lati (calcolare vicinanza per destinazioni diagonali)
- Esplorare tutti i possibili stati per 3/4/6 giocatori con 3 pedine su tabella da 25 possibili posizioni

Velocizzazione prestazioni
- Move ordering base per priorizzare pedine centrali più vicine al traguardo
- Tabelle di trasposizione con hashing effettuato in classe Board
- Considerare possibilità di move ordering in base a punteggio della board nella mossa successiva