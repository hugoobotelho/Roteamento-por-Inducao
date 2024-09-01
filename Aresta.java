import java.util.concurrent.Semaphore;

// Classe que representa uma aresta (conexão entre dois nós)
public class Aresta {
    private Roteador destino;
    private int valor;
    private Semaphore semaforo;

    public Aresta(Roteador destino, int valor) {
        this.destino = destino;
        this.valor = valor;
        this.semaforo = new Semaphore(1); // Semáforo para controle de acesso
    }

    public Roteador getDestino() {
        return destino;
    }

    public int getValor() {
        return valor;
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }
}
