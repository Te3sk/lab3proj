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

# Relazione Progetto Laboratorio 3
## Indice
1. [Componenti](#componenti)
   1. [Server-Side](#server-side)
      1. [`ServerMain`](#servermain)
      2. [`HOTELIERServer`](#hotelierserver)
      3. [`RequestHandler`](#requesthandler)

## Componenti

### Server-Side

#### `ServerMain`

La classe `ServerMain` è il punto di ingresso dell'applicazione server che legge i parametri di configurazione da un file di properties e avvia il server `HOTELIERServer` corrispondente.

##### Metodo `main`

Il metodo `main` è il punto di avvio dell'applicazione server. Utilizza un blocco try-with-resources per garantire la chiusura corretta dello stream di input dopo l'uso. Carica le proprietà dal file di configurazione (`serverConfig.properties`) e inizializza il server `HOTELIERServer` con i parametri letti.
I passaggi principali del metodo sono: lettura, estrazione e conversione delle proprietà di configurazione dal file `serverConfig.properties`; Istaziazione di un nuovo oggetto `HOTELIERServer` con i parametri letti e chiamata del metodo `run()` del server per avviare l'esecuzione.

#### HOTELIERServer

La classe `HOTELIERServer` rappresenta un server che gestisce connessioni TCP e UDP per un sistema di gestione alberghiera.

##### Costruttore

Il costruttore `HOTELIERServer` inizializza tutte le risorse necessarie per avviare il server, inclusi i gestori per utenti e alberghi, il canale del socket, il selettore e il ThreadPoolExecutor. I parametri sono stati precedentemente letti dal file di configurazione.

##### Metodi

- `run()`: Metodo principale che gestisce il ciclo di vita del server, gestendo la registrazione di nuove connessioni e la lettura di dati dai canali pronti.
- `fetchHandler(SocketChannel socketChannel)`: Restituisce il gestore di richieste corrispondente a un certo `SocketChannel`.
- `removeHandler(RequestHandler handler)`: Rimuove un gestore di richieste dalla lista.

##### Vantaggi delle scelte implementative

1. **Gestione non bloccante**: Utilizza un `Selector` per gestire le operazioni di I/O non bloccanti, consentendo al server di gestire molteplici connessioni senza dover dedicare un thread a ciascuna.
   
2. **ThreadPoolExecutor**: Utilizzo di un `ThreadPoolExecutor` per gestire l'esecuzione dei task asincroni, migliorando l'efficienza nell'elaborazione delle richieste.

3. **Sincronizzazione sicura**: Utilizzo di metodi sincronizzati e semafori (`Lock`) per garantire la coerenza nei dati condivisi tra i thread, evitando situazioni di race condition.

4. **Configurazione flessibile**: Il server è configurato con parametri come indirizzi, porte e intervalli di tempo tramite il costruttore, rendendolo adattabile a diversi ambienti di esecuzione.

Questi approcci aiutano a realizzare un server robusto, scalabile e efficiente, adatto per gestire un sistema complesso di gestione alberghiera attraverso connessioni TCP e UDP.

#### `RequestHandler`

La classe `RequestHandler` gestisce le richieste dai client attraverso un canale socket. Legge i messaggi, ne verifica la validità e li dispaccia ai metodi appropriati all'interno dell'applicazione server `HOTELIERServer`.

##### Metodi principali

Il `RequestHandler` implementa la logica principale per gestire le richieste dai client, come autenticazione degli utenti, ricerca di hotel, inserimento di recensioni e invio di notifiche tramite UDP.

###### Metodi di gestione delle richieste

- **`run()`**: Esegue la logica principale del `RequestHandler` in un thread separato. Legge i messaggi dal client, ne verifica la validità e li dispaccia al metodo appropriato o gestisce gli errori.
  
- **`dispatcher(String msg)`**: Gestisce il dispatching dei messaggi in base al formato e al tipo di richiesta ricevuta. Chiama i metodi corrispondenti come `signIn`, `logIn`, `logOut`, `searchHotel`, `searchAllHotels`, `insertReview` e `showMyBadges`.

- **Metodi di manipolazione dei dati e comunicazione:** **`signIn`, `logIn`, `logOut`, `searchHotel`, `searchAllHotels`, `insertReview`, `showMyBadges`**: Gestiscono le diverse operazioni di manipolazione dei dati, comunicando con le classi di gestione utente (`UserManagement`) e hotel (`HotelManagement`) per eseguire operazioni come registrazione, accesso, ricerca e inserimento di recensioni.

- **Metodi di comunicazione**
  
  - **`sendNotification(String msg)`**: Invia una notifica tramite protocollo UDP.

  - **`readAsString()` e `write(String message)`**: Gestiscono la lettura e la scrittura dei messaggi dal e verso il client.

##### Vantaggi delle scelte implementative

- **Gestione multithreading**: Utilizza thread separati per operazioni intensive come la persistenza dei dati.
  
- **Utilizzo efficiente delle risorse**: Utilizzo di `ByteBuffer` per la gestione efficiente dei dati in lettura e scrittura.

- **Separazione delle responsabilità**: Separazione delle operazioni di gestione delle richieste (`RequestHandler`) dalle operazioni di gestione dei dati (`UserManagement`, `HotelManagement`), migliorando la manutenibilità e l'estensibilità del codice.

- **Gestione degli errori**: Gestione degli errori attraverso la segnalazione di messaggi di errore appropriati ai client.

Questo approccio consente di gestire in modo efficiente le richieste dai client, mantenendo un'architettura scalabile e facile da mantenere per un'applicazione server robusta.
