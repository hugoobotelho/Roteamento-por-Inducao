import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.animation.PathTransition;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Roteador {
    private int id;
    private Button button;
    private double posX;
    private double posY;
    private Map<Roteador, Aresta> adjacencias; // Lista de adjacências (nós conectados)
    private Semaphore semaforo;
    // private Roteador pai;

    public Roteador(int id, Button button, double posX, double posY) {
        this.id = id;
        this.button = button;
        this.posX = posX;
        this.posY = posY;
        this.adjacencias = new HashMap<>();
        this.semaforo = new Semaphore(1);
    }

    public int getID() {
        return id;
    }

    public Button getButton() {
        return button;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    // public void setRoteadorPai(Roteador pai) {
    //     this.pai = pai;
    // }

    public Map<Roteador, Aresta> getAdjacencias() {
        return adjacencias;
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }

    public void adicionarAdjacencia(Roteador destino, int valor) {
        Aresta aresta = new Aresta(destino, valor);
        adjacencias.put(destino, aresta);
    }

    // Inicia o envio de pacotes, ligando o roteador
    public void enviarPacoteTodos() {
        // Cria uma nova thread para cada chamada
        new Thread(() -> {
            for (Map.Entry<Roteador, Aresta> entrada : adjacencias.entrySet()) {
                Roteador vizinho = entrada.getKey();
                System.out.println("Enviando para o vizinho: " + vizinho.getID());
                Aresta aresta = entrada.getValue();
                enviarPacote(vizinho, aresta, 1);
            }
        }).start();
    }

        // Inicia o envio de pacotes, ligando o roteador
        public void enviarPacoteTodosExcetoOQueChegou(Roteador pai) {
            // Cria uma nova thread para cada chamada
            new Thread(() -> {
                for (Map.Entry<Roteador, Aresta> entrada : adjacencias.entrySet()) {
                    Roteador vizinho = entrada.getKey();
                    if (vizinho != pai) {
                        System.out.println("Enviando para o vizinho: " + vizinho.getID());
                        Aresta aresta = entrada.getValue();
                        enviarPacote(vizinho, aresta, 2);
                    }
                }
            }).start();
        }

    private void enviarPacote(Roteador destino, Aresta aresta, int opcao) {
        try {
            // Controle de acesso ao envio do pacote
            aresta.getSemaforo().acquire();

            Platform.runLater(() -> {
                Circle pacote = new Circle(10, javafx.scene.paint.Color.YELLOW);
                System.out.println("Criando pacote para o destino: " + destino.getID());

                Principal.grafoPane.getChildren().add(pacote); // Adiciona o pacote ao Pane do grafo

                // Criando a linha de movimento
                Line path = new Line(this.getPosX(), this.getPosY(), destino.getPosX(), destino.getPosY());

                PathTransition transition = new PathTransition(Duration.seconds(2), path, pacote);
                transition.setOnFinished(event -> {
                    System.out.println("Pacote chegou ao destino: " + destino.getID());
                    // Quando o pacote chega ao destino
                    Principal.grafoPane.getChildren().remove(pacote);
                    switch (opcao) {
                        case 1:
                            destino.enviarPacoteTodos(); //Envia pacote para todos os vizinhos
                            break;
                        case 2:
                            // destino.setRoteadorPai(this); // Define o roteador atual como "pai"
                            destino.enviarPacoteTodosExcetoOQueChegou(this); //Envia pacote para todos os vizinho exceto o que chegou
                            break;
                        case 3:
                            // destino.enviarPacoteTodosExcetoOQueChegouTTL();
                            break;
                        case 4:
                            // destino.enviarPacoteOpcao4();
                            break;
                    }
                    // destino.enviarPacoteTodos(); // Envia pacote para todos os vizinhos
                });
                transition.play();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            aresta.getSemaforo().release();
        }
    }
}
