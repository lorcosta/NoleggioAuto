package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {
	//CODA DEGLI EVENTI
	private PriorityQueue<Event> queue=new PriorityQueue<>();
	
	//PARAMETRI DI SIMULAZIONE
	private int NC=10;//number of cars
	private Duration T_IN=Duration.of(19, ChronoUnit.MINUTES);//intervallo di tempo tra i clienti
	//inizializzo dei valori perchè c'è il rischio che la simulazione parta senza che i parametri vengano settati
	private final LocalTime ORA_APERTURA=LocalTime.of(8, 00);
	private final LocalTime ORA_CHIUSURA=LocalTime.of(17, 00);

	
	//MODELLO DEL MONDO
	private int nAuto;//auto disponibili nel deposito (compreso tra 0 e NC)
	
	//VALORI DA CALCOLARE
	private int clienti;
	private int insoddisfatti;
	
	//SIMULAZIONE VERA E PROPRIA
	public void run() {
		//PREPARAZIONE INIZIALE, VARIABILI DEL MONDO E CODA DEGLI EVENTI
		this.nAuto=NC;
		this.clienti=this.insoddisfatti=0;
		this.queue.clear();
		LocalTime oraArrivoCliente=this.ORA_APERTURA;//arrivo del primo cliente
		do {
			Event e= new Event(oraArrivoCliente, EventType.NEW_CLIENT);
			this.queue.add(e);
			oraArrivoCliente=oraArrivoCliente.plus(this.T_IN);
		}while(oraArrivoCliente.isBefore(this.ORA_CHIUSURA));
		
		//ESECUZIUNE DEL CICLO DI SIMULAZIONE
		while(!this.queue.isEmpty()) {
			Event e=this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
		
		
	}
	private void processEvent(Event e) {
		switch(e.getType()) {
		case NEW_CLIENT:
			if(this.nAuto>0) {
				//cliente servito, genero num casuale per quanto tempo tiene la macchina
				//1.aggiorno il modello del mondo
				this.nAuto--;
				//2.aggiorno i risultati
				this.clienti++;
				//3.genera nuovi eventi, restituzione dell'auto
				double num=Math.random();
				Duration travel;
				if(num<1.0/3.0) {
					travel=Duration.of(1, ChronoUnit.HOURS);
				}else if(num<2.0/3.0) {
					travel=Duration.of(2, ChronoUnit.HOURS);
				}else {
					travel=Duration.of(3, ChronoUnit.HOURS);
				}
				//System.out.println(travel);
				Event nuovo=new Event(e.getTime().plus(travel), EventType.CAR_RETURNED);
				this.queue.add(nuovo);
				
			}else {
				//cliente insoddisfatto
				this.clienti++;
				this.insoddisfatti++;
			}
			break;
		case CAR_RETURNED:
			//aggiorno il modello del mondo
			this.nAuto++;
			
			break;
		}
	}
	
	//METODI PER IMPOSTARE I PARAMETRI
	public void setNumCars(int N) {
		this.NC=N;
	}
	public void setClientFrequency(Duration d) {
		this.T_IN=d;
	}
	//METODI PER RESTITUIRE I RISULTATI
	public int getClienti() {
		return clienti;
	}
	public int getInsoddisfatti() {
		return insoddisfatti;
	}
}
