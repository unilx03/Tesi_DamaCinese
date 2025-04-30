# Tesi di Laurea Li Xu
Esplorazione totale dell'albero di gioco della Dama Cinese per risolvere completamente il gioco

## Parametri modificabili
Tester [OPTIONS] <Player Count> <Pieces>
Player Count: 2/3/4/6 (al momento solo 2)
Pieces: 3/6/10 (modalità 3 pedine in esame)
OPTIONS
-v: stampa dettagliata della modalità di gioco senza GUI tra agenti AI
-g: versione giocabile con GUI tra umano e agente AI

## Costanti modificabili per testing
Situati in Tester.java
- maxDepth: profondità massima esplorazione albero di gioco, -1 per visita completa
- maxTurns: turni giocabili per agenti AI in modalità senza GUI, -1 per avanzare fino a una vittoria

In Game.java possibilità di impostare PLAYERB a agente AI che sceglie una mossa casuale tra tutte quelle disponibili nel turno corrente

### Task List
Implementazione 2 giocatori
- Riscrivere gameLoop e minimax senza ripetizioni di codice (adattare a seconda di agent/isMaximizing)
- Considerare possibilità di move ordering in base a punteggio della board nella mossa successiva
- Esplorare tutti i possibili stati per 2 giocatori con 3 pedine su tabella da 25 possibili posizioni
  
Implementazione multi-giocatore
- Maxn per più giocatori
- Versione con GUI per più di 2 giocatori
- Controllo condizioni di vittoria per zone ai lati
- Move ordering per pedine ai lati (calcolare vicinanza per destinazioni diagonali)
- Esplorare tutti i possibili stati per 3/4/6 giocatori con 3 pedine su tabella da 25 possibili posizioni