# Generic Java Examples
<a href="https://www.alnao.it/javaee/"> 
        <img src="https://img.shields.io/badge/Java-ED8B00?style=plastic&logo=openjdk&logoColor=white" height="25px"/>
        <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=plastic&logo=SpringBoot&logoColor=white" height="25px" />
</a>

## Come creare e compilare un progetto con Maven

Maven è uno tool creato dal team di Apache per la gestione della compilazione dei progetti Java con lo scopo di gestire e organizzare le dipendenze di un qualsiasi progetto. È pensato per semplificare il processo di compilazione e semplificare la gestione delle dipendenze del progetto. Apache Maven è ormai diventato uno strumento essenziale per qualsiasi sviluppatore Java diventato uno strumento standard universalmente usato. 

Maven è pensato per gestire anche il ciclo di vita del software con una serie di plug-in per l'esecuzione di test e la creazione di documentazione, ciò semplifica la gestione del progetto e garantisce che tutto venga svolto in modo coerente ed efficiente.

In tutti i successivi articoli di questa categoria verrà usato questo tool quindi si da per scontato che lettore abbiamo installato Maven, in questo articolo vengono introdotti i pochi comandi base che devono essere usati per la gestione dei progetti con questa libreria, a titolo introduttivo vengono usati i comandi da riga di comando (la shell di GNU Linux oppure il "cmd" Ms Windows) ma dai prossimi articoli verrà usato Eclipse o Visual Studio Code che risultano molto più comodi visto che mettono a disposizione un tool grafico.

I comandi di Maven possono essere riassunti in pochi punti:
- mvn compile: usato per compilare un progetto
- mvn clean: rimuove tutti i compilati esistenti
- mvn test: esegue tutti gli step di testing se presenti
- mvn install: esegue il deploy del pacchetto (Jar o War) nel repository locale se configurato
- mvn package: esegue la compilazione, il testing e l'installazione, viene quasi sempre usato al posti dei precedenti 4 comandi 
- mvn deploy: esegue il precedente comando e poi carica il pacchetto (Jar o War) in un repository remoto se configurato
Mentre il comando base per creare un progetto è:
```
mvn archetype:generate -DgroupId=it.alnao -DartifactId=progettoJava
```
con questo comando viene avviato il generatore di progetti indicando i due parametri base (gruppo e artefatto), il programma chiede all'utente di inserirli se non sono indicati nel comando.

Il progetto creato presenta tre files:
- pom.xml: file di configurazione del progetto
- App.java: classe java base
- AppTest.java: classe java di test, vedere la sezione dedicata per approfondire l'argomento
Il file pom.xml presenta tutta una serie di informazione del progetto tra cui:
- nome del progetto e dati base come artifactId, groupId, versione
- proprietà del compiilatore tra cui la versione (dalla 1.7 in poi)
- elenco di tutte le librerie dipendenti
- comandi per la compilazione e plug-in con le varie configurazioni
Come visto in precedente per compilare il progetto il comando principale è: 
```
mvn package
```
nel dettaglio il processo di compilazione esegue tutta una serie di passi che verranno descritti nei prossimi articoli, se la compilazione va a buon fine viene creato un file jar all'interno della cartella target, il comando per avviare il jar è il classico di java:
```
java -jar ./target/*.jar
```
ma lanciando il primo progetto senza modifiche si potrebbe ottenere un errore di compilazione in quanto, di default, il jar creato non è auto-avviante e non c'è nessuna "main-class", per configurare la classe da impostare come main modificare il file pom.xml aggiungendo il tag "configuration" nel plug-in "maven-jar-plugin":
```
<plugin>
  <artifactId>maven-jar-plugin</artifactId>
  <version>3.0.2</version>
  <configuration>
    <archive>
      <manifest>
        <addClasspath>true</addClasspath>
        <classpathPrefix>dependency/</classpathPrefix>
        <mainClass>it.alnao.App</mainClass>
      </manifest>
    </archive>
  </configuration>
</plugin>
```
come facilmente intuibile, nel tag xml è presente la configurazione che indica al compilatore di aggiungere nel manifest quale è la classe principale, in questo esempio è presente anche il "classpathPrefix" indispensabile se si vuole aggiungere qualche libreria dipendenza nel progetto.

Infatti è possibile aggiungere una dipendenza ad un progetto modificando il file pom.xml nella sezione-tag "depedencies", per esempio per aggiungere log4j al progetto :
```
<dependency>
  <groupId>log4j</groupId>
  <artifactId>log4j</artifactId>
  <version>1.2.17</version>
</dependency>
```
questa viene aggiunta ma il compilatore deve essere configurato per "portare" la libreria nella cartella target e quindi deve essere aggiunto un ulteriore plugin al pom.xml:
```
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-dependency-plugin</artifactId>
  <version>3.0.1</version>
  <executions>
    <execution>
      <id>copy-dependencies</id>
      <phase>package</phase>
      <goals>
        <goal>copy-dependencies</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
e il comando per la compilazione diventa
```
mvn clean dependency:copy-dependencies package
```
infatti per poter eseguire la compilazione è indispensabile prima copiare le dipendenze dentro alla cartella "dependency" configurata nel precedente plugin.
```
public class App {
  private static final Logger logger = LogManager.getLogger(App.class);
  public static void main( String[] args ) {
    BasicConfigurator.configure();
    System.out.println( "Hello World!" );
    logger.info("We've just greeted the user!");
  }
}
```
Questo è un semplice esempio di classe java per verificare che la libreria log4j venga correttamente inclusa nel progetto e copiata nella cartella target.
Per la creazione di progetti web è possibile usare un archetipo specifico, impostandolo fin dalla creazione del progetto con il comando
```
mvn archetype:generate -DgroupId=it.alnao -DartifactId=02webModule -DarchetypeArtifactId=maven-archetype-webapp
```
questo tipo di progetto crea sempre tre files ma di tipo diverso: 
- una pagina jsp
- un file di configurazione dell'applicazione web.xml
- il file pom.xml con caratteristiche ben diverse da quelle viste nel progetto classico
questo tipo di progetto viene approfondito nel dettaglio nei prossimi articoli. Esiste anche un tipo specifico chiamato "maven-archetype-j2ee-simple" che va a create una completa applicazione j2ee con dei sotto-progetti specifici per ear, projects, servlets e il codice sorgente nella cartella src. I dettagli sulla tool maven è disponibile al sito ufficiale, tutti i dettagli sugli archetipi è disponibile in questa pagina della documentazione ufficiale, l'elenco completo di tutti i plugin e le librerie disponibili come dipendenze è disponibile al repository ufficiale (mvnrepository), è consigliato tenere sempre a portata di mano questi link. In qualsiasi progetto Java, in progetti puri o in framework strutturati, la gestione del log è un tema molto importante che deve essere sempre tenuto sotto controllo, la libreria principale è log4j sviluppata dalla comunità open-source di Apache Software Foundation. Di questa libreria esisono due versioni (uno e due), nei nuovi progetti viene usata la versione 2 ma in tutti i progetti degli ultimi 10 anni è usata la versione 1 quindi è bene avere sempre sotto mano le specifiche di entrambe le versioni. L'importazione della prima versione nel file pom di maven:
```
<dependency>
  <groupId>log4j</groupId>
  <artifactId>log4j</artifactId>
  <version>1.2.17</version>
</dependency>
```
La seconda versione della libreria, presente di default in alcuni archetipi di maven, è importata con due dipendenze principali:
```
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-api</artifactId>
  <version>2.13.0</version>
</dependency>
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-core</artifactId>
  <version>2.13.0</version>
</dependency>
```
Storicamente la libreria necessita di un file di configurazione che deve essere posizionata nella cartella resources dentro al progetto, cioè nella cartella "src/main" se si tratta di un progetto java standard. La prima versione prevede un file properties dove è possibile configurare il sistema di logging, si rimanda alla documentazione ufficiale per tutti i dettagli su come impostare e usare al meglio la libreria. La seconda versione della libreria prevede un file di configurazione di tipo XML che deve essere "log4j2.xml", la documentazione ufficiale è ricca di esempi, il più semplice file di configurazione è:
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
```
La scelta di una delle due librerie implica il dover adattare il codice Java sorgente delle classi alla libreria scelta, in particolare la classe principale LogManager è presente in entrambe le versioni ma in pacakge differenti:
```
//V1
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//V2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class App {
  private static final Logger logger = LogManager.getLogger(App.class);
  public static void main( String[] args ) {
    BasicConfigurator.configure(); //only in V1
    logger.debug("We've just greeted the user!");
    logger.info("We've just greeted the user!");
    logger.fatal("We've just greeted the user!");
  }
}
```
In ogni caso si rimanda sempre alla documentazione ufficiale per maggiori informazioni ed esempi.

# Come gestire progetti Maven con Eclipse

Eclipse è un ambiente di sviluppo (SDK), in questi articoli viene usato per la gestione di tutti i progetti Java, è possibile usare anche altri tool compatibili con Java-Maven come Visual Studio Code di Microsoft o i tool a pagamento come IntelliJ.

In Eclipse i progetti sono raggruppati in un workspace, tipicamente si usa una cartella del sistema come base di tutto lo spazio di lavoro e ogni sotto-cartella rappresenta un progetto, in questi articoli saranno usati solo progetti Java-Maven ma è possibile usare questo programma anche per altri tipi di progetto come i vecchi progetti J2EE e i progetti java puri. 

Per creare un progetto Maven, si deve avvia la procedura guidata di creazione di un progetto dal menù principale e selezionando la voce "New", il tipo "Project" e poi cercare il wizard "Maven project". Nella finestra successiva si deve selezionare il Archetype, esattamente si si esegue in fase di creazione con la console testuale vista nel precedente articolo. Qui è possibile anche selezionare un tipo da una lista che Eclipse scarica da internet dai vari repository ufficiali e non, disponibili in tutto il monto. Se si vuole creare un progetto base senza particolari esigenze, è consigliato usare gli archetipi disponibili dal sito "org.apache.maven.archetypes", per esempio selezionando il tipo "maven-archetype-simple". 

Nella seconda videata della procedura guidata è richiesto l'inserimento dei valori come il GroupId e il ArtifactId, esattamente come nella procedura da riga di mando. Una volta lanciato il processo di creazione nella vista console di Eclipse è possibile verificare tutti i messaggi di conferma che maven darebbe se il comando fosse lanciato da riga di comando. Al termine il progetto è pronto all'uso nella vista dei progetti con il suo pom.xml generato in automatico.

Eclipse permette anche di importare un progetto Maven non presente nel workspace, è consigliato importare progetti che si trovano nella stessa cartella del workspace stesso altrimenti potrebbero esserci problemi di conflitti da files, la procedura di import può essere lanciata dal menù principale nella voce "Import" e selezionando il wizard "Existing maven projects". Nella procedura guidata è necessario indicare la posizione esatta del file pom.xml, senza questo file infatti non si può parlare di progetto di tipo Maven.

Un progetto di tipo Maven in Eclipse si può riconoscere da una piccola lettera M presente nell'icona del progetto a fianco dell'icona J che indica Java come tipo di linguaggio del progetto, questo tipo di progetti hanno un menù dedicato tra le opzioni del progetto accessibili cliccando con il tasto destro nel progetto stesso. La funzionalità più interessante è la possibilità di aggiungere con una piccola procedura guidata una dipendenza la progetto, questa è l'alternativa più semplice al modificare il file pom.xml manualmente, per esempio per voler inserire la dipendenza dalla libreria log4j, disponbile all'indirizzo ufficiale è possibile aggiungere manualmente nel pom il tag:
```
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
  <groupId>log4j</groupId>
  <artifactId>log4j</artifactId>
  <version>1.2.17</version>
</dependency>
```
oppure è possibile inserire i tre valori (groupId, artifactId e version) nella procedura guidata di Eclipse. Un semplice esempio di utilizzo di Log4J 
```
package it.alnao.mavenEclipse;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
public class App {
  private static final Logger logger = LogManager.getLogger(App.class);
  public static void main( String[] args ){
    BasicConfigurator.configure();
    System.out.println( "Hello World!" );
    logger.info("Hello world");
    //set a breakpoint here to debug project
    System.out.println( "END!" );
  }
}
```
Per i progetti di questo tipo il programma mette a disposizione di compilazione nel sotto-menù "Run as" dove è possibile lanciare i comandi standard maven:
- "Maven install" che esegue il comando di installazione delle librerie dipendenti
- "Maven build" esegue la compilazione del progetto, alla prima esecuzione bisogna impostare "package" come Goal della compilazione così maven usa la cartella target come destinazione del jar corrispondente
- "Maven test" esegue tutte i test indicati nel file di configurazione
- "Java application" esegue il progetto come fosse una applicazione java senza compilare il jar
Eclipse dispone anche di una procedura per eseguire i debug di un progetto, per esempio inserendo un breakpoint in un punto del codice Java e poi cliccando sul sotto-menu "Debug" del progetto e  selezionando la voce "Java application".
Questo articolo sarà integrato in futuro se in articoli di questa categoria saranno usate altre funzionalità di Eclipse per gestire progetti Maven, è disponibile una ricca la guida ufficiale del progetto m2eclipse per l'integrazione tra i due sistemi.

# Come creare applicazioni Web-J2EE con servlet & JSP

Per la creazione di applicazioni web Java mette a disposizione la tecnologia Java 2 Enterprise Edition (spesso abbreviata con la sigla J2EE), tecnica diventata negli anni uno standard molto usato in quanto robusto, sicuro ma anche flessibile. Il successo è dovuto soprattutto al linguaggio Java stesso che può fondersi con le tecnologie di frontend come HTML e Javascript. La tecnologia si può dividere in alcune componenti base. Si tratta delle tecnologie legate alla produzione di interfacce web dinamiche, le cui componenti si possono dividere in tre parti:
- pagine web Java Server Pages (JSP), Java Server Faces (JSF), Custom Tag oppure framework specifici come Struts
- logiche di business con Enterprise JavaBeans (EJB, giunti alla specifica 3.0), JNDI e Java Message Service (JMS) 
- librerie esterne: esposizione di webservice, gestione di protocolli di rete.
La tecnologia permette facilmente di gestire la tecnologia model-view-controller (MVC) infatti, come standard universalmente usato i componenti si dividono in:
- model: classi Bean/DAO java spesso esterne ai progetti J2EE
- view: pagine web/jsp scritte in HTML con i tag messe a disposizione dalle varie librerie importabili 
- controller: classi servlet oppure uso di un framework specifico come Struts 
Eclipse mette a disposizione una procedura guidata per creare "Dynamic web Project" che rispettano lo standard J2EE ma conviene usare l'archetipo messo a disposizione dal Maven che risulta più completo e oggi lo standard mondiale per le applicazioni web in Java, da riga di comando si può lanciare
```
mvn archetype:generate -DarchetypeArtifactId = maven-archetype-webapp 
```
oppure è possibile usare la procedura guidata di Eclipse selezionando l'archetipo "maven-archetype-webapp" Nel progetto creato vengono create le cartelle e i files:
- src/main/java: contiene tutte le classi java del progetto
- src/main/resources: contiene i file di properties del progetto
- src/main/webapp: contiene tutti i file jsp e la sotto-cartella WEB-INF
- src/main/webapp/WEB-INF: contiene tutti i componenti dell'applicazione che non si trovano nella root dell'applicazione, difficile definizione.
- src/main/webapp/WEB-INF/web.xml: file di configurazione del progetto J2EE, questo descrive tutte le componenti del progetto web
- src/main/webapp/WEB-INF/index.jsp: pagina di esempio
- src/main/webapp/WEB-INF/lib: cartella che può contenere jar usati dall'applicazione
- src/main/webapp/resources: può contenere tutte le componenti statiche del progetto web come immagini e stili
Talvolta, in alcune vecchie versioni dello standard J2EE la cartella "WebApp" veniva indicata con il nome di "WebContent". Una volta creato il progetto è possibile impostare la vista "Servers" di Eclipse per avviare dal programma il progetto web: prima bisogna impostare la "Server runtimes" nelle preferenze di Ecipse e poi agganciare l'applicazione nella view "Servers". Trattandosi di un progetto Maven standard è possibile compilarlo con il comando
```
mvn clean package
```
oppure usando il "mvn build" di Eclipse impostando il goal di tipo "package". Il risultato della compilazione non è un jar ma è un file war, questo può essere caricato in qualsiasi web-server java come Tomcat. In questo semplice progetto c'è solo una pagina che viene caricata e non c'è nessun codice java compilato.

Le servelt sono le classi java che, all'interno di un progetto web, hanno il compito di controllare il comportamento dell'applicazione, inteso proprio come controller nel paradigma MVC. Le classi sono composte da due proprietà una request (di tipo HttpServletRequest) e una response (di tipo HttpServletResponse), attraverso il primo oggetto è possibile accedere a tutte le informazioni utili come i parametri di input e gli oggetti in sessione mentre il secondo oggetto, inizialmente vuoto, deve essere valorizzato con la risposta che si vuole inviare al client, inoltre è disponibile anche un oggetto ServletContext (di tipo javax.servlet.ServletContext) con la quale è possibile accedere alle informazioni del contesto (context) di un’applicazione cioè tutte le informazioni generali. Le classe implementa uno dei metodi HTTP usati per la chiamata: doGet, doPost, doPut, doDelete.

Per creare una classe di questo tipo è possibile usare la procedura guidata di Eclipse che è possibile eseguire dal menù "New" selezionando il tipo "Classe Java Servlet" oppure è indispensabile creare a mano la classe con proprietà e metodi. Alla prima creazione, può comparire un errore di compilazione risolvibile facilmente impostando il "Java build path" del progetto java di Eclipse aggiugnendo le librerie ServerRuntime e WebAppLibraries. Da notare che, se si è usati la procedura guidata di Eclipse, questo avrà modificato in automatico il file web.xml impostando la servlet, se invece si è scelto di creare le classi a mano è indispensabile censire la servlet nel file:
```
<servlet>
   <servlet-name>PrimaServlet</servlet-name>
   <display-name>PrimaServlet</display-name>
   <description></description>
   <servlet-class>it.alnao.mavenExamples.PrimaServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>PrimaServlet</servlet-name>
  <url-pattern>/PrimaServlet</url-pattern>
</servlet-mapping>
```
Questi due tag nel file di configurazione impostano due parametri: assegnano alla classe java un nome e assegnano a quel nome un pattern, questo ultimo sarà il endpoint nel quale la servelt sarà esposta nell'applicazione web. Infatti il webserver risponderà con la servelt dal endpoint:
```
http://localhost:<porta>/05mavenWebapp/PrimaServlet  
```
Per stampare codice HTML da una servlet bisogna usare la classe "Printer", un semplice esempio di codice di servlet per visualizzare una frase:
```
private static final String HTML_TOP = "<html><head><title>Primo esempio servlet: tabelline</title></head><body>";
private static final String HTML_BOTTOM = "</body></html>";
private static final String TABLE_TOP = "<h3>Tabelline</h3><table width='80%'>";
private static final String TABLE_BOTTOM = "</table>";
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
     throws ServletException, IOException {
  response.setContentType("text/html");
  PrintWriter out = response.getWriter();
  out.println(HTML_TOP);
  out.println("Questa tecnica non è molto usata");
  out.println(HTML_BOTTOM);
}
```
Tuttavia questa tecnica è poco usata in quanto non rispetta il paradigma di MVC: le servlet devono solo occuparsi della fase controller e non del view, per separare i compiti il controller richiama le componenti view (cioè le classi JSP) con un dispatcher.
```
request.setAttribute("nomeInRequest", "Alberto");
request.getSession().setAttribute("cognomeInSessione","Nao");
request.getRequestDispatcher("prova.jsp").forward(request, response);
```
In questo semplice esempio alla pagina di prova è possibile trasmettere dei dati attraverso la request oppure attraverso la sessione, si rimanda alla documentazione ufficiale per un approfondimento di cosa sono e come usare queste due tecniche. La pagina di default è sempre "index.jsp", tuttavia è possibile personalizzare la pagina di ingresso con un tag nel file di configurazione web.xml:
```
<welcome-file-list>
  <welcome-file>prova.jsp</welcome-file>
</welcome-file-list>
```
La parte Java all'interno delle pagine JSP viene compilata in maniera sequenziale dalla prima riga, quindi all'inizio di ogni file si possono inserire le direttive che servono al compilatore Java per indicare come compilare la pagina, istruzioni simili alle import nelle classi Java, queste istruzioni vengno dette direttive e si indicano in una pagina con il tag:
```
<%@ page import="java.util.*" %>
```
per importare una o più classi e sono da usare come gli import java, usare una classe java in una jsp senza import genera un errore di compilazione che però viene segnalato solo in fase di esecuzione quindi solo quando la pagina jsp viene eseguita.
```
<%@ include file="filedaincludere.jsp" %>
```
in questo secondo esempio viene usata la direttiva include per includere nella corrente pagina un altro file jsp che verrà compilato assieme. Altre direttive molto usate sono sono:
```
<%@ page contentType="text/html" %>
```
indica in quale formato sia scritto il documento (per esempio HTML o XML)
```
<%@ taglib prefix="myprefix" uri="taglib/miataglib.tld" %>
```
importa nella pagina JSP una tag-lib che essere identificata tramite una URI (questo argomento viene approfondito nei prossimi articoli). Per quanto riguarda il codice java dentro le pagine jsp, di base si usano le scriptlet, sistema creato proprio per "mischiare" il codice HTML (client) e il codice Java (server) anche se questo sistema è considerato deprecato viene ancora usato ma molti anche per ragioni di retro-compatibilità nei progetti di grandi dimensioni. La prima disputa sul termine scriplet è se, in lingua italiana, il termine sia maschile (gli scriplet) o femminile (le scriptlet), ogni programmatore ha la propria opinione. In un corpo di pagina, una scriplet inizia con <% e termina con %>, per esempio:
```
<%
  int i=0;
  i++;
%>
```
In particolare si possono usare scriplet anche per fare cicli o condizioni di elementi HTML, per esempio
```
<%
  for (int i = 0; i < 5; i++){
    %> Ciao Mondo! <%
  }
%>
```
Si possono definire dei metodi proprietari della pagina con l'uso di una apertura di scriplet particolare <%!, per esempio:
```
<%!
public int void contaParole (String s){
  StringTokenizer st = new StringTokenizer(s);
  return st.countTokens();
}
%>
```
Ed è possibile definire il metodo jspInit che viene lanciato una sola volta e serve per inizializzare gli elementi in pagina e il metodo jspDestroy per la pulizia a fine caricamento. Le scriplet vengono usate soprattutto per inviare al client dei valori dal server, cioè passare dei valori da Java all'HTML, l'uso del tag <%=, da notare che in questo caso è possibile inserire una sola riga e NON si deve mettere il ; (punto e virgola) finale, credo sia l'unico caso in java:
```
<%=i%>
<%=contaParole("Quante sono queste parole?")%>
```
Da notare che gli oggetti request, session, out, exception e application sono oggetti considerati impliciti nella pagina quindi non serve importarli e/o definirli, per esempio è possibile usare:
```
<% out.print("valore"); %>
```
al posto di
```
<%=valore%>
```
oppure si può usare
```
<%
String valoreId=request.getParameter("id");
session.setAttribute("id",valoreId);
%>
```
Usando le servlet introdotta prima è possibile visualizzare in una pagina le informazioni provenenti dalla servlet recuperando i valori dalla request o dalla sessione:
```
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+
     ":"+request.getServerPort()+path+"/";
%>
```
```
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Prova</title>
  </head>
  <body>
  Pagina di prova
  <br/>
  BasePath = <%=basePath%>
  <br />
  <%=request.getAttribute("nomeInRequest") %>
  <%=request.getSession().getAttribute("cognomeInSessione") %>
  </body>
</html>
```
Questa tecnica di scrivere le pagine jsp è considerato deprecato e non dovrebbe essere usato, in passato molto codice è stato scritto con questi metodi ed è sempre bene conoscere le basi di testa tecnologia!

# Come usare tag e creare custom-tag

La caratteristica principale della programmazione di Java è l'uso delle classi e degli oggetti, esattamente come nei file di una classe, all'interno di un file jsp è possibile ed usare usare classi se importate ovviamente metatag:
```
<%@ page import="it.alnao.manuali.java.NomeClasse"%>
```
dopo l'import è possibile usare la classe nelle scriptlet, per esempio un metodo statico è richiamabile con:
```
<% NomeClasse.NomeMetodoStatico(); %>
```
Per JSP Tags si intendono elementi java utilizzabili nel codice delle pagine jsp, distribuiti dentro a delle librerie particolari dette TagLib che definiscono la "firma" del tag e anche la propria implementazione, l'uso di questi permette di separare il codice "client" da quello "server" con lo scopo di evitare di scrivere codice Java puro nelle pagine jsp e di superare i limiti della programmazione XHTML. Tutti i Custom tag hanno questa sintassi:
```
<prefix:tag attr1="value" ... attrN="value" />
```
oppure:
```
<prefix:tag attr1="value" ... attrN="value" >Body</prefix:tag>
```
esattamente come un tag di HTML o come i tag standard JSP. Per usare un custom tag dentro ad una jsp bisogna importgare la TagLib e poi definirla, all'interno di ogni TagLib è presente un file TLD che rappresenta le firme dei tag e definisce quali classi java implementano quel tag, è fondamentale conscere il TLD di una taglib.

Il più semplice tag è quello messo a disposizione proprio dal J2EE, nello specifico il tag jsp permette di eseguire operazioni basiche su request e response, per esempio se si vuole usare un oggetto che è già presente in request o in sessione si può ridurre il codice scritto usando il tag usebean, per esempio:
```
<jsp:useBean id="NomeBean" scope="session" class="it.alnao.manuali.java.NomeClasse"/>
```
questo tag prende dalla sessione l'oggetto NomeVariabile, lo casta al tipo indicato e crea nella pagina una variabile di nome NomeVariabile con quel valore, cioè questo tag sostituisce l'istruzione
```
<% NomeClasse var=(NomeClasse) request.getSession().getAttribute("NomeBean"); %>
```
molto usato in quei progetti dove vengono usate le scriplet e non linguaggi più evoluti come le jstl o i tag Struts, analogamente al tag useBean esistono i tag setProperty e getProperty che servono a valorizzare e leggere proprietà di un oggetto bean, la sintassi è
```
<jsp:setProperty name="NomeBean" property="NomeProp" param="Valore" /> 
<jsp:getProperty name="NomeBean" property="NomeProp">
```
Da notare che per usare questi tag non serve importare nessuna libreria jar perchè sono tag "standard" java e vengono riconosciuti automaticamente dal WebServer, è presente anche un tag per importare altre pagine jsp in maniera dinamica:
```
<jsp:import
```
alternativa al bruttissimo:
```
<%@ include file="filedaincludere.jsp" %>
```
ma è corretto nominarlo ed evitarlo se possibile.

Per Custom tags si intendoo tag sviluppati all'interno del progetto o comunque non rilasciati ufficialmente dalla libreria J2EE, all'interno delle applicazioni JEE si hanno a disposizione più modi di creare un custom-tag, uno dei metodi più classici è quello di usare una classe java e un TLD per la descrizione del tag detti TLD acronimo di Tag Library Descriptor. Il primo passo per creare un TLD-TAG è quello di creare un file TLD nella giusta cartella del progetto WebContent/WEB-INF/tld/nome.tld, ricordandosi che ad un tld non corrisponde un solo tag ma corrisponde una libreria che può comprendere anche più tag, una documentazione completa dei file TLD può essere trovata nella documentazione ufficiale , lo scheletro base di questo tipo di file è:
```
<?xml version="1.0" encoding="UTF-8"?>
<taglib version="2.0" xmlns="http://java.sun.com/xml/ns/j2ee" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee 
http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd">
 <description>Alnao tag library</description>
 <tlib-version>1.0</tlib-version>
 <short-name>alnao</short-name>
 <tag>
  <name>TagCommentoSenzaParametri</name>
  <tag-class>it.alnao.prova.tags.TagCommentoSenzaParametri</tag-class>
  <body-content>JSP</body-content>
 </tag>
 <tag>
  <description>Importo</description>
  <name>Importo</name>
  <tag-class>it.alnao.prova.tags.ImportoTag</tag-class>
  <body-content>JSP</body-content>
  <attribute>
    <name>positiveStyle</name>
    <required>true</required>
    <rtexprvalue>true</rtexprvalue>
  </attribute>
  <attribute>
    <name>negativeStyle</name>
    <required>true</required>
    <rtexprvalue>true</rtexprvalue>
  </attribute>
 </tag>
</taglib>
```
in questo file viene definita una libreria assegnandogli anche uno short-name e un elenco di tag dove, per ogni elemento, è definito il nome, la classe e gli attributi se presenti. Poi serve implementare la classe TagCommentoSenzaParametri che deve espandere la classe TagSupport, per esempio un tag java che scrive un commento in pagina è
```
package it.alnao.prova.tags;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
public class TagCommentoSenzaParametri extends TagSupport {
  private static final long serialVersionUID = 1L;
  public int doStartTag() throws JspException {
    String s="\n\n<!-- prova tag AlNao.it -->\n\n";
    try {
      JspWriter out = pageContext.getOut();
      out.println(s);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return SKIP_BODY;
  }
  public int doEndTag() throws JspException {
    return EVAL_PAGE;
  }
}
```
mentre un esempio di tag per la visualizzazione di importi è:
```
package it.alnao.prova.tags;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
public class ImportoTag extends BodyTagSupport {
  private static final long serialVersionUID=1L;
  private String positiveStyle = null;
  private String negativeStyle = null;
  private String bodyTag = null;
  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }
  public int doAfterBody() throws JspException {
    bodyTag = getBodyContent().getString().trim();
    if (bodyTag != null) {
      Number decimal;
      DecimalFormat df = new DecimalFormat();
      decimal = df.parse(bodyTag);
      if (decimal != null) {
        if (decimal.doubleValue() > 0) { //positivo
          bodyTag = "<span class=\"" + getPositiveStyle() + "\">";
        } else {
          if (decimal.doubleValue() < 0) { //negativo
          bodyTag = "<span class=\"" + getNegativeStyle() + "\">";
        }
      } else { //zero
        bodyTag = "<span class=\"defaultClass\">" ;//+ bodyTag
      }
    } else { //non è un numero
      bodyTag = "<span>";
    }
    return (SKIP_BODY);
  }
  public int doEndTag() throws JspException {
    bodyTag = bodyTag + "</span>";
    try {
      this.pageContext.getOut().print(this.bodyTag);
    } catch (IOException ioe) {
      throw new JspException(ioe);
    }
    return EVAL_PAGE;
  }
  ... metodi get e set delle proprietà ...
}
```
All'interno delle pagine jsp, per poter usare questi tag è necessario importarli con una scriptlet:
```
<%@ taglib uri="/WEB-INF/tld/nome.tld" prefix="nomeTLD" %>
```
e poi è necessario usare il prefisso definito per chiamare i singoli tag, per esempio:
```
<nomeTLD:TagCommentoSenzaParametri />
<nomeTLD:Importo positiveStyle="classeCCS1" negativeStyle="classeCCS2">-12.12</nomeTLD:Importo>
```
Un metodo più semplice per creare TLD-TAG è usare classi java pure e, grazie alla tecnica dell'ereditarietà di Java, utilizzare classi già esistenti per evitare di scrivere codice già presente in altri tag, per esempio è possibile creare un tag condizionale "if" personalizzato che abbia due parametri in input, se sono uguali il corpo del tag verrà visualizzato in pagina, altrimenti il codice verrà saltato, riportando il codice di esempio di questo tag condizionale:
```
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
public class IfTag extends ConditionalTagSupport {
  private String value1 = null;
  private String value2 = null;
  public IfTag() {
    super();
  }
  protected boolean condition() throws JspTagException {
    if (value1==null || value2==null)
    throw new JspTagException ("Parametri nulli");
    return value1.equals(value2);
  }
}
```
dopo aver definito il file tld corrispettivo, in una qualsiasi pagina jsp poi basta usare il tag così:
```
<MieiTag:IfTag value1="uno" value2="due">
  <%
    //questo codice non viene eseguito perchè "uno" è diverso da "due"
  %>
</MieiTag:IfTag>
<MieiTag:IfTag value1="tre" value2="tre">
  <%
    //questo codice viene eseguito perchè le stringhe sono uguali
  %>
  Viene mostrato questo messaggio perchè "tre"=="tre"
</MieiTag:IfTag>
```
Allo stesso modo è possibile scrivere un tag "else" che estende la classe "if" in questo modo ma che nega il valore del metodo condition:
```
public class ElseTag extends IfTag{
  protected boolean condition() throws JspTagException {
    return ! super();
  }
}
```
nota: la classe ConditionalTagSupport si trova nella libreria jstl che deve essere importata nel file di configurazione di Maven.

Creare un custom-tag con una classe java è una tecnica molto elegante ma può risultare complicata se si deve scrivere un TAG che comprende molto codice client all'interno perché, per scrivere codice HTML dalla classe, si usa il metodo out che può risultato non semplicissimo da usare e il risultato rischia di diventare una classe molto lunga con molte stringhe costanti all'interno del codice. Esiste una alternativa alla tecnica dei Java-TLD: usare un file jsp dedicato al posto di una classe Java. Prima di tutto bisogna sempre definire un TLD e il riferimento al tag senza però nessun parametro anche se previsti, per esempio:
```
<tag-file>
  <name>tag_esempio</name>
  <path>/WEB-INF/tags/esempio.tag</path>
</tag-file>
```
poi è necessario creare il file che per convenzione ha estensione tag anche se in realtà è un file jsp, questo file deve sempre iniziare con il meta-tag specifico previsto da J2EE:
```
<%@ tag %>
```
e al suo interno deve essere presente l'elenco degli attributi, cioè i parametri del tag, per esempio:
```
<%@ attribute name="oggetto" required="true" rtexprvalue="true" description="." %>
```
e dopo si inserisce il codice HTML/Java del tag come se fosse un file jsp, per esempio
```
<DIV class="classeDiv">
  L'oggetto ha nome <%=request.getParameter("oggetto")%>
</DIV>
```
Per richiamare il tag da qualsiasi pagina JSP basta invocarlo richiamando il tag definito nel TLD:
```
<tagLib:tag_esempio oggetto="valore" />
```
Scegliere tra con una classe Java o con un file jsp è una questione di gusti, tipicamente si usano i file jsp quando il codice HTML è tanto e il codice Java è poco, viceversa si usano le classi quando il codice Java è molto complicato oppure se si deve definire un tag che estende un altro tag. La documentazione ufficiale è sempre il punto di riferimento per tutti gli sviluppatori.

## Come creare applicazioni web con Struts 1

Apache Struts è un framework open source per lo sviluppo di applicazioni studiato come estensione/evoluzione delle servlet dei progetti J2EE. Permette di creare applicazioni Web di grandi dimensioni, agevola la parallelizazione degli sviluppi e offre delle potentissime tag-lib, la validazione dei form e la gestione della localizzazione/l'internazionalizzazione. In questo sito si fa riferimento sempre alla mitica versione 1.3 del framework, la versione 2 non mi è mai piaciuta ma una guida è disponile vecchia versione del sito. La base del framework si basa su alcuni concetti:

classi Action gestiscono la logica di business e la logica di forward tramite metodi specifici, di fatto sono estensioni evolute delle classi Servelet
classi Form gestiscono i dati inseriti dall'utente nelle pagine definendo le regole di validazione dei dati
le pagine jsp non devono aver nessuna logica ma hanno solo il compito di visualizzare i dati
Un utente esperto avrà notato che queste tre componenti rispettano l'architettura MVC dove il Model sono le classi Form, le view sono le classi jsp e il controtroller sono le classi Action. Il framework per funzionare correttamente ha bisogno che tutte le classi Form e le classi action vengano censite in file di configurazione che storicamente viene sempre chiamato:
```
struts-config.xml
```
che deve essere censito nel web.xml. Per creare un semplice esempio di progetto con questo framework si parte da un progetto webapp standard con maven:
```
mvn archetype:generate -DgroupId=it.alnao -DartifactId=06Struts -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```
E bisogna importare le dipendenze del core e della tablib:
```
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>servlet-api</artifactId>
  <version>2.5</version>
</dependency>
<dependency>
  <groupId>org.apache.struts</groupId>
  <artifactId>struts-core</artifactId>
  <version>1.3.10</version>
</dependency>
<dependency>
  <groupId>org.apache.struts</groupId>
  <artifactId>struts-taglib</artifactId>
  <version>1.3.10</version>
</dependency>
```
Nel web.xml si vede censire una (e una sola) servlet dove si indica la posizione del file di configurazione xml previsto dal framework:
```
<servlet>
  <servlet-name>action</servlet-name>
  <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
  <init-param>
    <param-name>config</param-name>
    <param-value>/WEB-INF/struts-config.xml</param-value>
   </init-param>
   <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
  <servlet-name>action</servlet-name>
  <url-pattern>*.nao</url-pattern>
</servlet-mapping>
```
Nel file di configurazione si deve procedere al censimento di due blocchi: il primo riguarda i form-beans, cioè tutte le classi Form del progetto, il secondo definisce l'elenco di tutte le action con il corrispettivo form, path, classe e tutte le classi jsp sulle quali le classi Action potranno fare il forward:
```
<struts-config>
  <form-beans>
    <form-bean name="loginForm" type="it.alnao.mavenExamples.PrimoForm" />
  </form-beans>
  <action-mappings>
    <action name="loginForm" path="/login"
        type="it.alnao.mavenExamples.PrimaAction" scope="request"
        input="/index.jsp">
      <forward name="failure" path="/index.jsp" redirect="true" />
      <forward name="success" path="/success.jsp" redirect="true" />
    </action>
  </action-mappings>
</struts-config>
```
La classe Action estende un tipo previsto dall'architettura e deve definire obbligatoriamente un metodo execute che permette di definire logiche di business (come per esempio la validazione di un username-password) con conseguenti logiche di business (per esempio quale pagina visualizzare a seconda se le credenziali sono valide):
```
public class PrimaAction extends Action {
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    PrimoForm loginForm = (PrimoForm) form;
    if (loginForm.getUserName() == null 
        || loginForm.getPassword() == null
        || ! loginForm.getUserName().equalsIgnoreCase("alnao")
        || ! loginForm.getPassword().equals("bellissimo")) {
      return mapping.findForward("failure");
    } else{
      return mapping.findForward("success");  
    }
  }
}
```
La classe Form oltre a definire tutti i campi come un semplice Bean/model definiscono due metodi: reset e validate, il primo serve ad inizializzare i dati di un form, il secondo serve a validarli. Qualora una classe action viene eseguita e il suo form non sia valido, la servlet non viene eseguita ma viene eseguito un forward automatico nella pagina definita come input nel file xml di configurazione:
```
public class PrimoForm extends ActionForm {
  private String userName = null; //TODO Setter & Getter
  private String password = null;
  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.password = null;
  }
  @Override
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request){
    // TODO Auto-generated method stub
    return super.validate(mapping, request);
  }
}
```
Il sistema di validazione dei dati viene attivato solo se nella pagina jsp viene essere usato un tag form previsto dalla libreria standard struts e gli input previsti dalla libreria standard del framework. Nel metodo presente nella classe ActionForm è possibile creare logiche di validazione come nell'esempio. Il form della pagina che invia dati:
```
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
...
<html:form action="/login" focus="userName">
  <p>Username : <html:text property="userName" /></p>
  <p>Password : <html:password property="password" /></p>
  <p><html:submit value="login" /></p>
</html:form>
```
La tag-lib di struts prevede tre grandi tag: html, bean e logic che verranno descritti in un articolo dedicato.

Il tag più usato è il bean:message per la visualizzazione delle label, questo viene usato perché il framework gestisce nativamente il concento di localizzazione e multilingua: i messaggi vengono censiti in file di proprietà, per esempio il file di default di solito si chiama Application.properties e deve essere posizionato in un package dell'applicazione:
```
success.message=Benvenuto
```
E' possibile creare un secondo file per una specifica lingua, per esempio un file dedicato ai messaggi in lingua inglese  Application_en.properties che deve essere posizionato nello stesso package del generale:
```
success.message=Welcome
```
Il bundle (cioè il gruppo di file di tipo properties) deve essere censito nel file di xml di configurazione con l'indicazione del package e del nome:
```
<message-resources parameter="it.alnao.Application" key="ApplicationBundle" />
```
Nella pagina jsp, per visualizzare i messaggi, bisogna prima importare la libreria e poi visualizzare i messaggi indicando il nome del bundle e il nome del messaggio:
```
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
...
<p><bean:message key="success.message" bundle="ApplicationBundle"/></p>
```
Per attivare la gestione del multi-lingua nel framework bisogna impostare il "locale" nella prima action chiamata:
```
request.getSession().setAttribute(Globals.LOCALE_KEY, request.getLocale());
```
con questa istruzione il framework riuscirà a recuperare la lingua del browser (della request) e selezionerà il messaggio corrispondente al file indicato nel file di configurazione. L'esempio completo funzionante può essere trovato al solito repository:
```
https://github.com/alnao/JavaExamples/tree/master/GenericJava/06Struts
```

## Come usare le taglib di Struts e JSTL

La libreria standard del framework Struts mette a disposizione alcuni tag pronti all'uso molto utili che aiutano molto quando c'è la necessità di introdurre logiche in pagina e si vuole evitare di usare le scriptlet come consigliato da tutti i manuali. La documentazione ufficiale dei tag è molto ricca di esempi e questo articolo vuole esserne un riassunto , il più semplice, come già visto nel precedente articolo, è il tag per visualizzare messaggi:
```
<bean:message key="app.title"/>
```
Tuttavia la libreria bean mette a disposizione messaggi per visualizzare oggetti e proprietà all'interno degli oggetti:
```
<bean:write name="employee" property="username" />
```
Se, nelle pagine jsp, si vogliono recuperati oggetti salvati in request dalla classe action è necessario configurare struts affinché non esegua un redirect (creando una nuova request) ma impostando a false il parametro verrà mantenuta la stessa request tra classe Action pagina jsp di destinazione:
```
<forward name="success" path="/success.jsp" redirect="false" />
```
Altri tag a disposizione sono il define per dichiarae valori
```
<bean:define id="displayText" value="Text to Display" />
<bean:write name="displayText" />
```
Un altro gruppo molto usato sono i tag logici di cui si riportano alcuni esempi:
```
<logic:notPresent name="logonForm"> 
  <html:link forward="logon">Sign in here</html:link> 
</logic:notPresent> 
<logic:present name="logonForm"> 
  <html:link forward="logoff">Sign out</html:link> 
</logic:present> 
<logic:empty name="user">
  <forward name="login" />
</logic:empty />
...
<bean:define id="value2" name="bean2" property="value"/> 
<logic:equal value="<%=(String) value2 %>" name="bean1" property="value"> 
  HIT! 
</logic:equal> 
<logic:iterate id="employee" name="employees"> 
  <tr align="left"> 
    <td> <bean:write name="employee" property="username" /> </td> 
    <td> <bean:write name="employee" property="name" /> </td> 
    <td> <bean:write name="employee" property="phone" /> </td> 
  </tr> 
</logic:iterate>
```
In assoluto i tag più usati della libreria struts sono i tag per la gestione degli input, questi permettono di gestire i Form collegati direttamente agli ActionForm, l'esempio base già visto nel precedente articolo è la sostituzione dei classici tag HTML con i tag specifici della libreria che permetteranno a Struts di valorizzare il Bean ActionForm con i valori inseriti in pagina:
```
<html:form action="/login" focus="userName">
<p>Username : <html:text property="userName" /></p>
<p>Password : <html:password property="password" /></p>
<p><html:submit value="login" /></p>
</html:form>
```
Si rimanda alla documentazione ufficiale per approfondimenti a riguardo visto che è una tecnica indispensabile quando si lavora in progetti di grandi dimensioni con Struts 1.

Esiste un'altra libreria molto usata chiamata JSTL (Java Standard Tag Library), con lo scopo di uniformare i tag di tutti i framework, integra i tag funzionali mancanti di Struts e alcuni vengono sostituiti da sintassi molto più semplici, grazie a questi tag si può evitare "quasi" completamente l'uso delle scriptlet all'interno delle pagine jsp. Per integrare questa libreria nel progetto basta aggiungere le dipedenze:
```
<dependency>
  <groupId>jstl</groupId>
  <artifactId>jstl</artifactId>
  <version>1.2</version>
</dependency>
<dependency>
  <groupId>taglibs</groupId>
  <artifactId>standard</artifactId>
  <version>1.1.2</version>
</dependency>
```
e nelle pagina basta importare la libreria:
```
<%@ taglib uri="https://java.sun.com/jsp/jstl/core" prefix="c" % %>
<%@ taglib uri="https://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="https://java.sun.com/jsp/jstl/functions" prefix="fn" %>
```
I principali tag disponibili sono:
- ```<c:out>``` permette di visualizzare un dato da un bean o da un oggetti
- ```<c:import>``` permette di includere in una pagina altre pagine simile a jsp:include, permette l'invio dei parametri con il sottotag ```<c:param>```
- ```<c:redirect>``` esegue la redirect verso altre servlet
- ```<c:set>``` esegue la set di un valore in una proprietà o in un oggetto
- ```<c:remove>``` rimuovere una variabile o un oggetto
- ```<c:catch>``` cattura eventuali exception generati al suo interno
- ```<c:if>``` semplice condizione, usata anche come base per il tag catch
- ```<c:choose>``` condizione articolata con usati in combinazione anche i ```<c:when>``` and ```<c:otherwise>```
- ```<c:forEach>``` semplice iterazione su una collezione di oggetti
- ```<c:url>``` crea un link ad una servlet/action
Un semplice esempio di utilizzo di queste libreria
```
<c:set var = "salario" scope = "request">2200</c:set>
<c:if test = "${importo > 2000}">
  <p>L'importo di < c:out value = "${salary}"/> supera i 2000 €</p>
</c:if>
```
Un occhio attento avrà notato che nella condizione della c:if appena introdotta è presente una espressione con i caratteri ${variabile}, questa tecnica ufficiale è chiamata Expression Language (spesso abbreviato con EL), principale feature introdotta con la versione 2 di Jsp e permette di abbreviare ulteriomente il codice in pagine sostituendo alcuni tag (e alcune scriplet) con un linguaggio dedicato, questo prevede l'uso del simbolo dollaro seguito dalle istruzione in graffe, di fatto il codice
```
<% out.print(variabile); %>
<%=variabile %>
<c:out value="variabile" />
```
può essere sostituito con il semplice
```
${variabile}
```
questa tecnica è indispensabile quando si usano alcuni tag come le condizioni o cicli in jstl ma può essere usata in qualsiasi situazione. Deve essere attivata in pagina con il comando:
```
<%@ page isELIgnored="false" %>
```
Un esempio combinato di utilizzo di questa tecnica, per la creazione di un link con parametri:
```
<c:url value = "/NomeAction.do" var = "myURL">
  <c:param name = "parametro1" value = "valoreUno"/>
  <c:param name = "parametro2" value = "valoreDue"/>
</c:url>
<a href="${myURL}">Link a NomeAction</a>
<%--in alternativa <c:import url = "${myURL}"/> --%>
```
Oppure un altro esempio di ciclo su una collezioned i elementi:
```
<c:forEach items="${variabileElenco}" var="elmento" varStatus="status">
  <p>Item posizione ${status.index} con valore ${elemento} </p>
</c:forEach>
<c:if test="${empty variabileElenco }">
  <p>La lista vuota</p>
</c:if>
```
Un ultimo esempio di gestione delle exception misto tra JSTL e EL:
```
<c:catch var ="catchException">
  <% int x = 5/0;%>
</c:catch>
<c:if test = "${catchException != null}">
  <p>L'exception lanciata è : ${catchException} Con il messaggio: ${catchException.message}</p>
</c:if>
```
Le due librerie combinate possono semplificare molto la vita dei programmatori quando si deve eseguire formattazioni particolari, in particolare JSTP mette a disposizione la libreria:
```
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
```
Un elenco quasi completo di tutti i tag e le loro possibili applicazioni:
```
<c:set var = "balance" value = "120000.2309" />
<fmt:formatNumber value = "${balance}" type = "currency"/>
<fmt:formatNumber type = "number" maxIntegerDigits = "3" value = "${balance}" />
<fmt:formatNumber type = "number" maxFractionDigits = "3" value = "${balance}" />
<fmt:formatNumber type = "percent" maxIntegerDigits="3" value = "${balance}" />
<fmt:formatNumber type = "percent" minFractionDigits = "10" value = "${balance}" />
<fmt:formatNumber type = "percent" maxIntegerDigits = "3" value = "${balance}" />
<fmt:formatNumber type = "number" pattern = "###.###E0" value = "${balance}" />
...
<fmt:setLocale value = "en_US"/>
<fmt:formatNumber value = "${balance}" type = "currency"/>
<fmt:parseNumber var = "variabile" type = "number" value = "${balance}" />
...
<fmt:parseNumber var = "variabile" integerOnly = "true" 
         type = "number" value = "${balance}" />
<c:set var = "now" value = "20-10-2010" />
<fmt:parseDate value = "${now}" var = "parsedEmpDate" pattern = "dd-MM-yyyy" />
...
<c:set var = "now" value = "<%= new java.util.Date()%>" />
<fmt:formatDate type = "time" value = "${now}" />
<fmt:formatDate type = "date" value = "${now}" />
<fmt:formatDate type = "both" value = "${now}" />
<fmt:formatDate type = "both" dateStyle = "short" timeStyle = "short" value = "${now}" />
<fmt:formatDate type = "both" dateStyle = "long" timeStyle = "long" value = "${now}" />
<fmt:formatDate pattern = "yyyy-MM-dd" value = "${now}" />
```
```
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
```
```fn:contains``` testa se una stringa è contenuta in un'altra, per esempio:
```
<c:set var = "theString" value = "I am a test String"/>
<c:if test = "${fn:contains(theString, 'test')}">
Found test string< /c:if>
fn:containsIgnoreCase come la precedente ma ignora maiuscole e minuscole
fn:endsWith testa se una stringa termina con una determinata stringa per esempio:
<c:set var = "theString" value = "I am a test String 123"/>
<c:if test = "${fn:endsWith(theString, '123')}">String ends with 123< /c:if>
fn:escapeXml codifica gli elementi XML all'interno di una stringa come testo e non come tag HTML, per esempio:
<c:set var = "string2" value = "This is second String."/>
<p>string: ${fn:escapeXml(string2)}< /p>
visualizzato: This is second String.
fn:indexOf calcola la posizione di una stringa all'interno di un altra
fn:join unisce tutti gli elementi di un elenco in una stringa separati da un separatore, per esempio:
<c:set var = "string1" value = "This is first String."/>
<c:set var = "string2" value = "${fn:split(string1, ' ')}" />
<c:set var = "string3" value = "${fn:join(string2, '-')}" />
<p>${string3}< /p>
fn:length calcola il numero di elementi in una lista (collection) oppure la lunghezza di una stringa
fn:replace ritorna una stringa nella quale è stato sostituita tutte le occorrenze di una stringa con un'altra
fn:split divide una stringa in un array di sottostringhe
fn:startsWith testa se una stringa inizia con una determinata stringa
fn:substring estrae una stringa da una posizione ad un'altra (si comincia a contare da zero come in java), per esempio:
<c:set var = "string1" value = "This is first String." />
<c:set var = "string2" value = "${fn:substring(string1, 5, 15)}" />
<p>is first S< /p>
fn:toLowerCase trasforma tutti i caratteri di una stringa in minuscolo
fn:toUpperCase trasforma tutti i caratteri di una stringa in maiuscolo
fn:trim rimuove tutti gli spazi bianchi all'inizio e alla fine di una stringa
```

## Come gestire connessioni a database con JDBC

In qualsiasi progetto il collegamento con la base dati è una spetto fondamentale e la maggior parte dei progetti Java prevedono uno standard chiamato JDBC, abbreviazione di Java DataBase Connection, che permette di utilizzare componenti Java per il collegamento con la base dati, questo standard prevede a basso livello i seguenti componenti:

un driver "registrato": identifica il tipo di base di dati e i componenti necessari per il collegamento, come la definizione del endPoint, le credenziali di accesso e qualunque altro parametro necessario al collegamento dal programma in esecuzione alla base dati:
- la connessione: rappresenta il filo logico tra il componente in esecuzione e il server
- lo statement: definisce tutte le istruzioni da eseguire e il risultato ritornato dalla base dati, nel caso di database relazionali, indica quali queries scritte nel linguaggio SQL eseguire
I driver di collegamento possono essere di vario tipo visto che i DBMS sono forniti da aziende diverse (più o meno amiche di Java) e possono essere di vario tipo: ODBC, Native API, Network API o pure Java; per la definizione del collegamento il tipo influisce solamente per il tipo di parametri necessari: a seconda del tipo saranno necessarie informazioni diverse, per esempio nel caso di un collegamento via network sarà necessario conoscere le impostazioni di rete mentre nel caso di pure Java sono necessarie il informazioni del socket di connessione.

I componenti di connessione e statement fanno parte di un package globale java.sql :
```
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
```
Ipotizzando di volersi collegare ad un Database di tipo Mysql con un il driver ufficiale scaricabile con maven aggiungendo nel pom.xml la dipendenza:
```
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <version>8.0.29</version>
</dependency>
```
Il codice per collegarsi è molto semplice e prevede la definizione di driver, connessione e statement. Inoltre il resultSet ritornato dalla query può essere elaborato per il recupero dei dati:
```
public class MySql {
  private static final Logger logger = LogManager.getLogger(MySql.class);
  private final static String URL = "jdbc:mysql://localhost:3306/dbname";
  private final static String USERNAME = "admin";
  private final static String PASSWORD = "password";
  private final static String DRIVER = "com.mysql.jdbc.Driver";
  public static void main( String[] args ) throws Exception{
    BasicConfigurator.configure();
    String sqlCommand="select nome,eta from tab where nome is not null order by nome";
    Class.forName(DRIVER);
    Connection con = DriverManager.getConnection (URL,USERNAME,PASSWORD);
    PreparedStatement cmd = con.prepareStatement(sqlCommand);
    ResultSet res = cmd.executeQuery();
    ArrayList<String> nomi=new ArrayList<String>();
    if (res!=null){
      while(res.next()) {
        logger.debug("name:" + res.getString("nome"));
        nomi.add( res.getString("nome") );
      }
    }
    cmd.close();
    con.close();
  }
}
```
Le istruzioni di modifica dati possono eseguite con la stessa tecnica con l'accorgimento ulteriore che è possibile usare i parametri dello statement per creare query dinamiche:
```
final String K_INSERT_TABELLA="insert into tab (nome,descrizione,eta) VALUES (?,?,?)";
int numeroParametro=0;
Connection con = DriverManager.getConnection (getUrl(),getUser(),getPassword());
PreparedStatement cmd = con.prepareStatement(K_INSERT_TABELLA);
cmd.setString(numeroParametro++,"Alberto"); //imposto il primo parametro
cmd.setString(numeroParametro++,"bellissimo"); //imposto il secondo parametro
cmd.setInt(numeroParametro++,24); //imposto il terzo parametro
int result=cmd.executeUpdate();
cmd.close();
con.close();
```
La stessa tecnica può essere usata anche con altri tipi di DMBS come postgresql la cui configurazione del pom.xml è:
```
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>9.0-801.jdbc4</version>
</dependency>
```
Per tutti i DMBS più diffusi sono disponibili driver pubblici sui server maven, nel caso di driver proprietari è possibile importare la libreria jar nel progetto e poi usare il driver. L'uso di Connection e Statement è una tecnica talmente diffusa e usata che è diventata uno standard di fatto. Tuttavia non è necessario definire tutti questi passaggi ad ogni istruzione ma ci sono i framework che gestiscono questi componenti, nei prossimi articoli saranno introdotti tecniche meno manuali con l'ausilio di framework come Hibernate e Spring Boot.

## Come gestire connessioni a database con Hibernate

La libreria Hibernate è usata per creare una mappa tra le classi java e il modello di una base dati e si integra perfettamente con il concetto di persistenza dei dati verso il database visto che implementa le specifiche JPA (Java Persistence API) per la persistenza dei dati. In tutto il mondo è la libreria più usata anche da framework più evoluti come Spring e Spring Boot e. Come indicato nel sito ufficiale oltre alla libreria base per il collegamento con la base dati contiene anche altre sotto-librerie come la Hibernate Search (ricerca full-text), Hibernate Validator (gestione dei vincoli), Hibernate OGM (supporto Java Persistence per database NoSQL), Hibernate Tools (raccoglie strumenti a riga di comando e plug-in per lavorare con Hibernate).

Per importare la libreria in un progetto (dal semplice maven-archetype-quickstart al più complesso) basta importare nel pom.xml le librerie:
```
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-core</artifactId>
  <version>4.3.5.Final</version>
</dependency>
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.17</version>
</dependency>
```
come è possibile notare anche in questo caso è necessaria una libreria di connessione di tipo JDBC alla base dati, infatti la libreria necessita di un file di configurazione, che di solito si chiama hibernate.cfg.xml che i parametri di configurazione della base dati come la stringa di connessione, le credenziali e i parametri base, inoltre nel file di configurazione è necessario censire la lista delle classi
```
<hibernate-configuration>
  <session-factory>
    <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
    <property name="hibernate.connection.url">jdbc:mysql://X.Y.Z.U:3306/uat</property>
    <property name="hibernate.connection.username">admin</property>
    <property name="hibernate.connection.password">xxxxxxx</property>
    <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
    <property name="hibernate.current_session_context_class">thread</property>
    <property name="hibernate.show_sql">true</property>
    <property name="show_sql">true</property>
    <mapping class="it.alnao.hibernate.Model"/>
    <!-- <mapping resource="Models.xml"></mapping>-->
  </session-factory>
</hibernate-configuration>
```
Queste classi mapping definiscono la struttura dell'oggetto con le proprietà, con in aggiunta le informazioni della base dati con delle annotation, come il nome della tabella nella annotation table, il nome delle colonne e le proprietà nelle varie annotation previste dalla libreria
```
@Entity
@Table(name= "test_alberto", 
uniqueConstraints={@UniqueConstraint(columnNames={"id"} ) } ) 
public class Model{
  private long id;
  private String nome;
  private String cognome;
  
  @Id
  @Column(name = "id", unique = true, nullable = false)
  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
 
  @Column(name = "nome")
  public String getNome() {
    return nome;
  }
  ....
}
```
Per funzionare la libreria deve essere caricata in esecuzione, prima delle operazioni dei database quindi viene spesso definita una classe di utilità per il recupero della libreria e del file di configurazione:
```
public class HibernateUtil {
 private static final SessionFactory sessionFactory = buildSessionFactory();
 private static SessionFactory buildSessionFactory() {
  SessionFactory sessionFactory = null;
  try {
    Configuration configuration = new Configuration();
    configuration.configure("hibernate.cfg.xml");
    System.out.println("Hibernate Configuration loaded");
    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
      .applySettings(configuration.getProperties()).build();
    System.out.println("Hibernate serviceRegistry created");
    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    return sessionFactory;
  }catch (Exception e) {
    e.printStackTrace();
  }
  return sessionFactory;
 }
 public static SessionFactory getSessionFactory() {
  return sessionFactory;
 }
}
```
Per richiamare la libreria ed eseguire operazioni sul database è necessario richiamare prima la classe di utilità e poi si possono eseguire le operazioni sulla base dati:
```
public static void main( String[] args ) {
  Model m=new Model();
  m.setId(1);
  m.setNome("Alberto");
  m.setCognome("Nao");
  Session session = HibernateUtil.getSessionFactory().openSession();
  try{
    session.save(m); 
    session.flush();
    System.out.println("Saved Successfully.");
    Query q = session.createQuery("from Model");
    List<Model> resultList = q.list();
    System.out.println("num:" + resultList.size());
    for (Model next : resultList) {
      System.out.println("- " + next);
    }
    session.delete(resultList.get(0));
  }catch (Exception e) {
    e.printStackTrace(); 
  }finally {
    session.close(); 
  } 
}
```
Come notato in questo semplice esempio viene inserito un elemento, viene eseguita una query di selezione e successivamente viene cancellato. La query viene eseguita con il linguaggio HQL (Hibernate Query Language) che è molto simile al SQL con la differenza che si usano i nomi java e non i nomi della base dati. Si rimanda alla documentazione ufficiale per maggior informazioni riguardo a questa tecnica.

La libreria permette di censire i modelli come file xml al posto delle classi java: nel file di configurazione si deve aggiungere il riferimento ad un file xml esterno (come possibile vedere nell'esempio sopra nella riga commentata) e in questo file si devono censire la lista delle tabelle e la classe corrispondente e la lista di tutti i campi della base dati con i nomi delle proprietà java corrispondenti. Per esempio:
```
<hibernate-mapping package="it.alnao.hibernate">
  <class name="Model" table="test_alberto" >
    <id name="id" type="long" column = "id">
      <generator class="native"/>
    </id>
    <property name="nome" type="string" column="name" />
    <property name="cognome" type="string" column="cognome" />
  </class>
</hibernate-mapping>
```
Questa tecnica eviterebbe l'uso delle tante annotation nelle classi java, tuttavia nel tempo non è stata molto usata e ha sempre più preso piede l'uso delle classi con le annotation senza il mapping separato, tecnica che sarà spesso usata nei prossimi esempi visto che per Spring Boot è lo standard.

Questa libreria con queste tecnica permette di costruire applicazioni perfettamente in linea con la filosofia del MVC, dove Hibernate e il suo file di configurazione si prende il compido del Model, delegando ad altre librerie (come Struts o Spring) il compito di gestire le View e il controller.

# Come creare applicazioni grafiche con swag

Con la sigla GUI (Graphical User Interface) si intendono le applicazioni con una interfaccia grafica che permette all'utente di iteragire con i dati, questo concetto comprende anche le applicazioni formate da finestre, bottoni, labels e così via. Le librerie Swing e JavaFX sono tra le due più comuni usate per creare applicazioni GUI con il linguaggio Java, in questo articolo sarà introdotta la prima libreria che è la più usata dai programmatori anche se è sempre meno usata visto che questo tipo di applicazioni è stato sostituito dalle applicazioni web. Questa si basa su'altra libreria AWT di cui è, de facto, una estensione, si rimanda ai tantissimi siti per maggiori informazioni riguardo alla libreria e le sue caratteristiche.

La guida ufficiale introduce un semplice esempio dove si costruisce una semplice classe per la visualizzazione di una finestra con un messaggio semplice messaggio:
```
import javax.swing.*; 
public class HelloWorldSwing {
  private static void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("HelloWorldSwing");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //Add the ubiquitous "Hello World" label.
    JLabel label = new JLabel("Hello World");
    frame.getContentPane().add(label);
    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }
  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
```
Per la creazione di un progetto si può usare qualunque archetipo di tipo maven, la libreria Swing infatti si trova nel package base javax che si trova in qualunque SDK standard e non necessita librerie aggiuntive aggiunte come dipendenze nel file di configurazione.

Questa libreria si basa principalmente sull'uso della classe JFrame che definisce il comportamento della principale finestra, per esempio:
```
frame = new JFrame("Aws J Console");
frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
frame.setLayout(new BorderLayout());
frame.setLocationByPlatform(true);
frame.setSize(200,200);
frame.setContentPane(contentPane); 
frame.pack();
frame.setJMenuBar(Menu.createMenuBar(... , this ));
frame.setVisible(true);
```
si rimanda alla documentazione ufficiale per maggiori dettagli. Un bellissimo progetto di applicazione costruita in Java con Swing è Jedit: un semplice editor di testo costruito per funzionare su tutti i sistemi operativi.

Per esempio per definire una lista con un metodo che descrive il compormaento al click di un elemento
```
public JScrollPane createListPanel(...) throws FileNotFoundException {
  String[] columnNames = {"NOME"};
  String[][] data = new String[listB.size()][1];
  for (int i=0;i<listB.size();i++) {
    data[i][0]=listB.get(i).name();
  } 
  JTable table = new JTable(data , columnNames){
    private static final long serialVersionUID = 1L;
    public boolean isCellEditable(int row, int column) { 
      return false; 
    };
  };
  table.getColumnModel().getColumn(0).setPreferredWidth(200);
  table.setPreferredSize(new Dimension(100,200));
  table.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
      JTable target = (JTable) evt.getSource();
      int row = target.rowAtPoint(evt.getPoint());
      int col = target.columnAtPoint(evt.getPoint());
      System.out.println("Click on" + row + "-" + col);
    }
  });
  JScrollPane p = new JScrollPane(table);//table
  p.setPreferredSize(new Dimension(105,205));
  return p;
}
```
Per ogni componente sono disponibili molto esempi e tutorial, per esempio sono disponibili molti esempi per la gestione di elenchi e tabelle:

## Come creare servizi SOAP e Rest con Axis2 e Jersey
Per quanto riguarda il termine WebService esiste una definizione data dalla W3C: si tratta di un software progettato per collegare più punti di una rete in un contesto distribuito come il tipo client-server o server-server. Nelle applicazioni Java esistono diverse librerie e framework che permettono ai programmatori di costruire applicazioni è possibile creare applicazioni per l'esposizione di servizi, le principali modalità sono: applicazioni web, web service SOAP e servizi REST.

Tipicamente le applicazioni web sono costruite con l'architettura J2EE che permettono di creare applicazioni navigabili con un browser attraverso il protocollo HTTP comunemente chiamato WWW, nelle applicazioni è possibile aggiungere framework evoluti come Struts o Spring per gestire servlet e migliorare la struttura delle applicazioni. Si rimanda ai vari articoli dedicati a questi argomenti per maggior approfondimenti.

Web service esposti con il protocollo SOAP possono essere creati con le librerie Axis2, JaxWS oppure CXF, grazie a queste librerie è possibile creare servizi di tipo SOAP-XML o simili, si rimanda alla documentazione ufficiale di Apache-Axis per maggior in formazioni riguardo a questa libreria. Esistono molti tutorial e guide che descrivono i passi necessari per creare webservice con questa libreria, in particolare questi descrivono come un semplice esempio di servizio:
```
public int add(int num1, int num2){
    return num1+num2;
}
```
possa essere descritto come servizio tramite un file XML:
```
<service>
    <parameter name="ServiceClass" locked="false">
        com.chamiladealwis.ws.service.SimpleService
    </parameter>
    <operation name="sayHello">
        <messageReceiver class="org.apache.axis2.rpc.receivers.RPCMessageReceiver" />
    </operation>
</service>
```
La libreria poi permette di esportare il servizio aar (Axis2 Archive) e possono essere distribuiti tramite il pacchetto war di Axis2.

Il programma Eclipse mette a disposizione una procedura guidata molto semplice ed efficace per creare servizi con questa libreria, anche in questo caso si rimanda al sito ufficiale dove è possibile trovare un semplice tutorial. Grazie a questa procedura è possibile creare webservice esposte con un il protoccolo SOAP e un WSDL: questa libreria un po' datata e non a volte non funziona con le ultime versioni di Eclipse e del servet Tomcat. E' possibile anche usare la libreria JaxWS per la definizione di questo tipo di webservice senza l'uso di Eclipse e Maven, si rimanda sempre alla documentazione ufficiale.
Tramite la libreria Jersey è possibile creare semplici servizi REST, grazie a maven è possibile selezionare il tipo base "Jersey-quickstart-webapp" presente nel repository "org.glassfish.jersey", questo archetipo costruisce una applicazione web con la servelet configurata già pronta all'uso e una piccola classe di esempio che crea e espone un semplice servizio con una risorsa e il metodo GET dello standard HTTP, il codice di esempio della classe di esemepio:
```
@Path("/myresource")
public class MyResource {
@GET
@Produces(MediaType.TEXT_HTML)
public String sayHtmlHello() {
  return "<html> " + "<title>" + "Hello " + "</title>"
      + "<body><h1>" + "Hello " + "</body></h1>" + "</html> ";
}
@GET
@Path("/json")
@Produces(MediaType.APPLICATION_JSON)
public Response hello() {
  Persona p=new Persona("Alberto");
  return Response.status(Response.Status.OK).entity(p).build();
}
```
Questo servizio può essere richiamato da un piccolo client di esempio:
```
public class App{
    public static void main( String[] args )    {
    System.out.println("App 11RestClient");
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient();// newClient(config);
        WebTarget target = client.target(getBaseURI());
        javax.ws.rs.core.Response response = target.path("webapi").
                            path("myresource/json").
                            request().
                            accept(MediaType.APPLICATION_JSON).
                            get(Response.class);//.toString();
        System.out.println(response.toString());
        System.out.println(response.readEntity( String.class ) );
    }
    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8081/11RestServer").build();
    }
}
```

## Come usare i protocolli di rete SFTP e FTP

All'interno delle applicazioni J2EE è possibile creare connessioni verso Server FTP utilizzando il protocollo omonimo o il protocollo specifico più sicuro di tipo SFTP. Non esiste una unica libreria standard per effettuare questo tipo di connessioni. La libreria principale è stata implementata da apache e mette a disposizione i package:
```
import org.apache.commons.net.ftp
```
Con la libreria importabile nei file di configurazione pom.xml:
```
<dependency>
  <groupId>commons-net</groupId>
  <artifactId>commons-net</artifactId>
  <version>3.6</version>
</dependency>
```
Con questa libreria si può creare una connessione ad un server FTP (senza lo strato di sicurezza)
```
public void open() throws IOException {
  ftp = new FTPClient();
  ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
  ftp.connect(server, port);
  int reply = ftp.getReplyCode();
  if (!FTPReply.isPositiveCompletion(reply)) {
    ftp.disconnect();
    throw new IOException("Exception in connecting to FTP Server");
  }
  ftp.login(user, password);
}
public void close() throws IOException {
  ftp.disconnect();
}
public List<Object> listFiles(String path) throws IOException {
  FTPFile[] files = ftp.listFiles(path);
  return Arrays.stream(files)
    .map(FTPFile::getName)
    .collect(Collectors.toList());
}
```
Mentre per il protocollo SFTP esistono due librerie: JSch di jcraft e Vfs2 di apache, rispettivamente importabili nei progetti con le librerie:
```
<dependency>
  <groupId>com.jcraft</groupId>
  <artifactId>jsch</artifactId>
  <version>0.1.55</version>
</dependency>
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-vfs2</artifactId>
  <version>2.9.0</version>
</dependency>
```
Con queste librerie è possibile usare il protocollo con le chiavi di sicurezza previste del protocollo SFTP, si rimanda alla documentazione ufficiale delle due librerie per maggiori dettagli. Con la libreria Vfs2 di Apache è possibile collegarsi ad un server remoto con poche righe di codice:
```
public List<String> ls(String path) throws Exception {
  StandardFileSystemManager fsManager = new StandardFileSystemManager();
  fsManager.init();//Initializes the file manager
  FileSystemOptions opts = new FileSystemOptions();
  SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
  String sftpUri = getConnection(path).toString() ;
  File[] identities = { new File(privateKey) };
  SftpFileSystemConfigBuilder.getInstance().setIdentities(opts, identities);
  FileObject localFileObject=fsManager .resolveFile (sftpUri, opts );
  FileObject[] children = localFileObject.getChildren();
  ArrayList<String> l=new ArrayList<String>();
  for ( int i = 0; i < children.length; i++ ){
    l.add ( children[ i ].getName().getBaseName() );
  } 
  fsManager.close();
  return l;
}
```

## Cosa sono le Lambda Function in Java

Una delle evoluzioni principali dalla versione 8 di Java è stata l'introduzioni delle "Lambda Function" dette anche "Java Lambda Expression" che permettono di scrivere del codice senza dovergli dare un nome (metodo o classe), le possibili sintassi di questa tecnica prevedono 3 tipi sinonimi a seconda del numero di parametri e del numero di istruzioni nel blocco:
```
parameter -> expression
(parameter1, parameter2) -> expression
(parameter1, parameter2) -> { code block }
```
Il caso più semplice e più usato è l'uso delle lambda function per definire l'iterazione di una lista:
```
import java.util.ArrayList;
public class Main {
  public static void main(String[] args) {
    ArrayList<Integer> numbers = new ArrayList<Integer>();
    numbers.add(5);
    numbers.add(9);
    numbers.add(8);
    numbers.add(1);
    numbers.forEach( (n) -> { System.out.println(n); } );
  }
}
```
Ma allo stesso modo è possibile definire un oggetto "codice":
```
import java.util.function.Consumer;
...
Consumer<Integer> method = (n) -> { System.out.println(n); };
numbers.forEach( method );
```
Si rimanda alla documentazione ufficiale, il quick start presentato dal sito ufficiale e alla pagina w3c per maggiori dettagli.
L'uso di questa tecnica è usato in moltissimi casi specifici, si elencano alcuni esempi dove il codice classi può essere sostituito con questa tecnica:
- definizione del comportamento di un bottone:
  ```
  bottone.setOnAction(
    event -> System.out.println("Click done")
  );
  ```
- filtri con ciclo for:
  ```
  public List<Persona> getMaschi(iscritti){
    List<Persona> persone = new ArrayList<Persona>();
    for (Persona p:iscritti)
      if (isMaschio(p))
        persone.add(p);
    return persone;
  }
  ```
- filtri con metodo:
  ```
  Predicate<Persona> allMaschi = p -> p.getSesso().equals("M");
  public List<Persona> getIscrittiFiltratiPer(Predicate<Persona> pred){
    List<Persona> persone = new ArrayList<Persona>();
    for (Persona p:iscritti)
      if (pred.test(p))
        persone.add(p);
    return persone;
  }
  ms.getIscrittiFiltratiPer(allMaschi);
  ```
- logiche annidate sulle liste con filter e map:
  ```
  lista.stream()
  .filter( p -> p.getGender() == Person.Sex.MALE) //filtrare elementi di una lista
  .map(p -> p.getEmailAddress()) //funzione map per modificare un elemento
  .forEach(email -> System.out.println(email));
  ```
- definizione di interfacce funzionali:
  ```
  package java.awt.event;
  import java.util.EventListener;
  public interface ActionListener extends EventListener {
    public void actionPerformed(ActionEvent e);
  }
  ```
- runnable block:
  ```
  public class RunnableTest {
    public static void main(String[] args) {
      System.out.println("=== RunnableTest ===");
      Runnable r1 = new Runnable(){// Anonymous Runnable
      
      @Override
      public void run(){
        System.out.println("Hello world old style!");
      }
    };
    // Lambda Runnable
    Runnable r2 = () -> System.out.println("Hello world with Lambda!");
      r1.run();
      r2.run(); 
    }
  }
  ```

La tecnica delle Lambda Function è stata introdotta per semplificare la vita dei programmatori, assieme alla tecnica delle classi innestate, questa tecnica è molto utile e usata tuttavia spesso rende il codice molto meno leggibile e l'utilizzo deve sempre essere pensato, si rimanda alla pagina ufficiale che consiglia quando usare e quando non usare queste tecniche.Una nota obbligatoria è il nome di queste funzioni: "Lambda Function" e "Lambda Expression" sono nomi usati anche da altre tecnologie, come le lambda function di AWS, per questo motivo è sempre consigliato usare il nome con il linguaggio di programmazione "Java Lambda Function" per evitare fraintendimenti, poi ovviamente è possibile usare "Java Lambda Function" per definire delle "AWS Lambda Function" ma questa è una sega mentale di noi programmatori che lavorano con Java in Cloud.

## Cosa è il Test Driven Development

In informatica, nello sviluppo software, il test-driven development, spesso abbreviato con la sigla TDD, è un modello di sviluppo del software che prevede che la stesura dei test automatici avvenga prima di quella del software e che lo sviluppo del software applicativo sia orientato esclusivamente all'obiettivo di passare i test automatici precedentemente predisposti. Il TDD prevede la ripetizione di un breve ciclo di sviluppo in tre fasi: nella prima fase viene scritto un test automatico, nella seconda fase viene sviluppata la quantità minima di codice necessaria per passare il test e nella terza fase viene eseguito un refactoring del codice per rispettare i livelli di qualità e leggibilità richiesti (fonte wiki).

Questo articolo non vuole essere una spiegazione accademica di cosa sono i TDD e come dovrebbero essere implementati, ma vuole essere solo una descrizione di una modalità di sviluppo con esempi pratici funzionanti di una delle tecniche più usate per realizzare TDD efficaci con il linguaggio Java. I test di questo tipo di TDD sono tutti atomici e devono verificare una sola operazione del requisito e, all'interno di ogni test, devono essere sviluppati tre passi:
- Arrange: la definizione di tutti i dati di partenza
- Act: la chiamata alla operazione da testare (di solito è una sola operazione ma può essere anche multipla)
- Assert: la validazione del risultato confrontando la risposta del passo 2 con quanto definito nel punto 1
Nei progetti Java creati con Maven, in fase di creazione del progetto è presente la configurazione dei test con una classe vuota, lanciando il comando
```
$ mvn test
```
il sistema maven eseguirà tutte le classi di test presenti nel progetto, se il comando viene lanciato da riga di comando il risultato dei test viene espost con i messaggi:
```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, 
Time elapsed: 0.033 s - in it.alnao.examples.AppTest
oppure un messaggio di errore in caso di test non passato con il messaggio

[INFO] Running it.alnao.examples.AppTest
[ERROR] Tests run: 4, Failures: 1, Errors: 0, Skipped: 1, Time elapsed: 0.031 s <<< FAILURE! - in it.alnao.examples.AppTest
[ERROR] dividiPerZero Time elapsed: 0.01 s <<< FAILURE!
org.opentest4j.AssertionFailedError: Expected java.lang.ArithmeticException to be thrown, but nothing was thrown.
at it.alnao.examples.AppTest.dividiPerZero(AppTest.java:40)
```
Mentre nei programmi IDE (come Visual studio Code e/o Eclipse) sono presenti diversi tool grafici e viste che permettono di eseguire i test e vedere il risultato in maniera grafica. 

E' possibile anche generare un report di maven e si usa il comando
```
$ mvn site
```
però è necessario censitre nel pom.xml una sezione dedicata di reporting:
```
<reporting>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-report-plugin</artifactId>
    </plugin>
  </plugins>
</reporting>
```
La libreria comunemente più usata è quella JUnit alla versione 5 ma la maggior parte dei progetti creati con maven tramite archetipi è configurata con la 4 o con la 3, è consigliato sempre aggiornare il file di configurazione pom.xml di tutti i nuovi progetti impostando l'ultima versione di JUnit, gli esempi fanno riferimento alla versione 5 che ha modificato i package e le classi usate per eseguire il passo assert nelle test unit perché i metodi sono racchiusi nel pacakge:
```
import org.junit.jupiter.api.Assertions;
```
e in aggiunta bisogna ricordarsi che è necessario usare la versione 11 di Java.

Un esempio pratico: l'idea è di realizzare un "servizio" che effettui la divisione di due numeri, usando la metodologia TDD dobbiamo definire per prima i test che verificheranno il buon codice, per esempio possiamo partire dall'idea di verificare che il numero 5 diviso per il numero 2 dia come risultato due e mezzo e bisogna ricordare non è possibile eseguire divisioni se il divisore è zero, prevediamo un test specifico che verifichi la esecuzione di una ArithmeticException nel caso di divisore zero. Il codice di esempio di questi semplici test diventa:
```
@Test
@DisplayName("Dividi 42")
public void dividi42() throws Exception{
  //1) Arrange: la definizione di tutti i dati di partenza 
  Double dividendo=new Double(42.0);
  Double divisore=new Double(16.0);
  Double resultAtteso=new Double(2.625);
  //2) Act: la chiamata alla operazione da testare (di solito è una sola operazione ma può essere anche multipla)
  Double result=App.dividi(dividendo, divisore);
  //3) Assert: la validazione del risultato confrontando la risposta del passo 2 con quanto definito nel punto 1 
  Assertions.assertEquals(resultAtteso,result);
}
@Test
@DisplayName("Dividi per zero")
public void dividiPerZero(){
  //1) Arrange: la definizione di tutti i dati di partenza 
  final Double dividendo=new Double(5.0);
  final Double divisore=new Double(0);
  //3) Assert: la validazione del risultato confrontando la risposta del passo 2 con quanto definito nel punto 1 
  Assertions.assertThrows(ArithmeticException.class,
    ()->{ //2) Act: la chiamata alla operazione da testare (di solito è una sola operazione ma può essere anche multipla)
      App.dividi(dividendo, divisore);
    }
  );
}
@Test
public void shouldAnswerWithTrue() {
  Assertions.assertTrue( true );
}
@Test
@Disabled("Disabled test example")
  void disabledTest() {
  fail();
}
```
In questo semplice esempio sono stati usati i metodi:
- assertEquals per confrontare il ritorno del metodo con il valore atteso
- assertThrows per verificare che venga effettivamente lanciata l'exception corretta nel caso di divisore pari a zero
- assertTrue per una verifica statica, può essere usato per per far fallire automaticamente un test in un ramo di codice che non deve essere eseguito con il parametro false.
Inoltre negli esempi si possono notare tutte diverse le annotazioni messe a disposizione dalla libreria, l'elenco completo di tutti i metodi disponibili e le varie annotazioni si può trovare alla documentazione ufficiale. 


# AlNao.it
Nessun contenuto in questo repository è stato creato con IA o automaticamente, tutto il codice è stato scritto con molta pazienza da Alberto Nao. Se il codice è stato preso da altri siti/progetti è sempre indicata la fonte. Per maggior informazioni visitare il sito [alnao.it](https://www.alnao.it/).

## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*