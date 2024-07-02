# todo
- [ ] spiegare scelte personali
  - algoritmo ranking
  - altro
- [ ] definire strutture dati create
- [ ] schema dei thread
  - server side
  - client side
- [ ] descrizione delle primitive di sincronizzazione (lock)
  - 
- [ ] istruzioni su compilazione/esecuzione

# Relazione Progetto Laboratorio
## Indice
- [todo](#todo)
- [Relazione Progetto Laboratorio](#relazione-progetto-laboratorio)
  - [Indice](#indice)
  - [Protocolli](#protocolli)
    - [Connessione client-server](#connessione-client-server)
    - [Connessione Broadcast](#connessione-broadcast)
  - [Server side](#server-side)
    - [`ServerMain`](#servermain)
    - [`HOTELIERServer`](#hotelierserver)
    - [`RequestHandler`](#requesthandler)
    - [`DataPersistence`](#datapersistence)
    - [`HotelManagement`](#hotelmanagement)
      - [Algoritmo per l'assegnameto del rank](#algoritmo-per-lassegnameto-del-rank)
    - [`UserManagement`](#usermanagement)
  - [Client-side](#client-side)
    
    
## Protocolli
### Connessione client-server
Il client invia messaggi sotto forma di stringa al server tramite connessione TCP. Il formato dei messaggi è il seguente:
```java
"TYPE_indirizzoChiamante_parametro1_parametro2_parametro3_parametro4"
```
I parametri possono essere al massimo 4: a seconda del tipo di richiesta cambia la quantità e il tipo.

Le risposte del server, sempre sotto forma di stringhe, possono essere di 2 tipi:
- In caso di richiesta corretta viene inviata direttamente la stringa che verrà stampata all'utente sul client, che può contenere la risposta effettiva o l'eventuale problema che il server ha riscontrato.
- In caso di richiesta errata viene inviato uno specifico codice di errore:
  - `USERN_Y` se l'username è già presente (per la registrazione)
  - `USERN_N` se l'username non è presente (per il login)
  - `EMPTYF` se uno dei campi (parametri) è stato inviato vuoto
  - `WRONGPSW` se la password è errata
  - `HOTEL` se l'hotel non è presente
  - `CITY` se la città non è presente
  - `FORMAT` se la richiesta non è stata formattata correttamente

### Connessione Broadcast
L'invio di notifiche da parte del server a i client loggati in caso cambi l'hotel con il maggior ranking in una città, è gestita da una connessione di tipo UDP. Anche in questo caso, il server invia la stringa finale che sarà stampata dal client.

## Server side

### `ServerMain`
La classe `ServerMain` è il punto di ingresso dell'applicazione server che legge i parametri di configurazione da un file di properties, inizializza e avvia il server `HOTELIERServer` corrispondente. 

### `HOTELIERServer`
La classe `HOTELIERServer` rappresenta il server vero e proprio che gestisce connessioni TCP e UDP del sistema. Nel costruttore si inizializzano tutte le risorse necessarie: gestori per utenti e hotel, canale socket, selettore e ThreadPool. Il metodo `fetchHandler(SocketChannel socketChannel)` restituisce il gestore di richieste corrispondente a un certo `SocketChannel` e il metodo `removeHandler(RequestHandler handler)` rimuove un gestore di richieste dalla lista. Il metodo `run()` invece gestisce le connessioni dei client e le loro richieste, assegnando ognuna di queste ad un thread. I vari worker elaborano le richieste tramite la classe `RequestHandler` che implementa `Runnable`. La classe `NotificationService` è incaricata di gestire tutto il processo di notifica UDP. 

### `RequestHandler`
`RequestHandler` legge il contenuto delle richieste, elabora una risposta e la invia al client. Il metodo `run()` esegue la logica principale della classe: controlla la validità del messaggio e lo passa al metodo `dispatcher(String msg)`, il quale chiamera il metodo opportuno per gestire la richiesta a seconda del tipo. Oltre ad i metodi per gestire le signole richieste, questa classe contiene:
- `readAsString()` per leggere i dati che arrivano sulla socket e ritornarli come stringa
- `write(String message)` per inviare messaggi sulla socket
- `sendNotification(String msg)` per lanciare la notifica sul canale udp
- `quit()` per gestire il caso in cui il client chiuda la connessione

### `DataPersistence`
Questa classe implementa `Runnable` e viene eseguita periodicamente da un thread creato in `HOTELIERServer` per salvare i dati di utenti e hotel nei rispettivi file JSON in `data\`. I metodi `saveUsers`, `loadUsers`, `saveHotels`, `loadHotels`, caricano e scaricano i dati rispettivamente di utenti e hotel. I dati vengono scaricati dai costruttori di `HotelManagement` e `UserManagement`. Il metodo `run()` usa i metodi per salvare i dati sincronizzandone l'accesso con una `Lock`.

### `HotelManagement`
Questa classe fornisce funzionalità per la ricerca di hotel, l'aggiunta di recensioni, il recupero di recensioni e l'aggiornamento delle classifiche degli hotel. La classe utilizza un blocco per garantire la sicurezza del thread durante l'accesso e la modifica dei dati dell'hotel. 

I dati dell'hotel vengono archiviati in una mappa, dove la chiave è l'ID dell'hotel e il valore è l'oggetto Hotel, in modo da avere un accesso diretto agli hotel con un riferimento univoco e di favorire un'eventuale aggiornamento dei dati.

#### Algoritmo per l'assegnameto del rank
Tramite il metodo `calculateHotelScore()` viene assegnato un punteggo agli hotel con la seguente formula:
$$
\begin{array}{l}
G = \sum^{n}_{i = 0} g_i
\\\\
S = \sum^{n}_{j = 0} \frac{cleaningScore_i + positionScore_i + serviceScore_i + qualityScore_i}{4}
\\\\
R = e^{-d}
\\\\
totalScore = G \times 0.5 + S \times 0.3 + R \times 0.2
\end{array}
$$
dove:
- $n$ è il numero totale di recensioni per quell'hotel
- $g_i$ è il valore della  `globalScore` della recensione numero $i$
- $fieldScore_i$ è il valore del signolo campo della recensione $i$
- $d$ è il numero di giorni passati dall'ultima recensione
`cityHotelsXranking` gli hotel nella lista vengono ordinati 

### `UserManagement`
Analogamente questa classe fornisce funzionalità per registrare nuovi utenti, accedere agli utenti esistenti, disconnettere gli utenti, salvare i dati dell'utente in un file e recuperare le informazioni dell'utente.

## Client-side




