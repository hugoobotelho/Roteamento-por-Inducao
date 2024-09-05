/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 31/08/2024
* Ultima alteracao.: 05/09/2024
* Nome.............: Roteomento por Inundacao
* Funcao...........: Simular o algoritmo de roteamento por inundacao da rede
*************************************************************** */

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.animation.PathTransition;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Roteador {
    private int id;
    private Button button;
    private double posX;
    private double posY;
    private Map<Roteador, Aresta> adjacencias; // Lista de adjacências (nós conectados)
    private Semaphore semaforo;
    private static int pacotesGerados = 0; // Contador de pacotes gerados
    private Random random = new Random();  // Para gerar valores aleatórios
    private int p = 70; //define a probabilidade da ultima opcao de 70%


    public Roteador(int id, Button button, double posX, double posY) {
        this.id = id;
        this.button = button;
        this.posX = posX;
        this.posY = posY;
        this.adjacencias = new HashMap<>();
        this.semaforo = new Semaphore(1);
    }

    /*
    * ***************************************************************
    * Metodo: setPacotesGerados
    * Funcao: define o numero total de pacotes gerados e atualiza o Label na interface grafica.
    * Parametros: quantidade - o numero total de pacotes gerados.
    * Retorno: void.
    * ***************************************************************
    */
    public static void setPacotesGerados(int quantidade) {
        pacotesGerados = quantidade;
        // Atualizar o Label na interface gráfica
        Platform.runLater(() -> {
            Principal.pacotesGeradosLabel.setText("Pacotes Gerados: " + pacotesGerados);
        });
    }

    /*
    * ***************************************************************
    * Metodo: getID
    * Funcao: obtem o id do roteador.
    * Parametros: nenhum.
    * Retorno: o id do roteador.
    * ***************************************************************
    */
    public int getID() {
        return id;
    }

    /*
    * ***************************************************************
    * Metodo: getButton
    * Funcao: obtém o botão associado ao roteador.
    * Parametros: nenhum.
    * Retorno: o botão do roteador.
    * ***************************************************************
    */
    public Button getButton() {
        return button;
    }

    /*
    * ***************************************************************
    * Metodo: getPosX
    * Funcao: obtém a coordenada X do roteador.
    * Parametros: nenhum.
    * Retorno: a coordenada X do roteador.
    * ***************************************************************
    */
    public double getPosX() {
        return posX;
    }

    /*
    * ***************************************************************
    * Metodo: getPosY
    * Funcao: obtém a coordenada Y do roteador.
    * Parametros: nenhum.
    * Retorno: a coordenada Y do roteador.
    * ***************************************************************
    */
    public double getPosY() {
        return posY;
    }

    /*
    * ***************************************************************
    * Metodo: getAdjacencias
    * Funcao: obtém o mapa de adjacencias do roteador.
    * Parametros: nenhum.
    * Retorno: o mapa de adjacencias.
    * ***************************************************************
    */
    public Map<Roteador, Aresta> getAdjacencias() {
        return adjacencias;
    }

    /*
    * ***************************************************************
    * Metodo: getSemaforo
    * Funcao: obtém o semáforo do roteador.
    * Parametros: nenhum.
    * Retorno: o semáforo do roteador.
    * ***************************************************************
    */
    public Semaphore getSemaforo() {
        return semaforo;
    }

    /*
    * ***************************************************************
    * Metodo: adicionarAdjacencia
    * Funcao: adiciona uma adjacencia entre este roteador e um
    *          roteador destino com um valor associado.
    * Parametros: destino - o roteador destino.
    *             valor - o valor da adjacencia.
    * Retorno: void.
    * ***************************************************************
    */
    public void adicionarAdjacencia(Roteador destino, int valor) {
        Aresta aresta = new Aresta(destino, valor);
        adjacencias.put(destino, aresta);
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodos
    * Funcao: envia um pacote para todos os roteadores adjacentes.
    * Parametros: pacote - o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodos(Pacote pacote) {
        new Thread(() -> {
            // pacotesGerados++; // Incrementa o contador de pacotes gerados
            if (this == pacote.getDestino()) {
                System.out.println("Foram gerados " + pacotesGerados + " ate chegar no destino");
                // Atualizar o Label na interface gráfica
                Platform.runLater(() -> {
                    Principal.pacotesGeradosLabel.setText("Pacotes Gerados: " + pacotesGerados);
                });
            }
            else {
                for (Map.Entry<Roteador, Aresta> entrada : adjacencias.entrySet()) {
                    Roteador vizinho = entrada.getKey();
                    Aresta aresta = entrada.getValue();
                    enviarPacote(vizinho, aresta, pacote, 1);
                }
            }
  
        }).start();
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodosExcetoOQueChegou
    * Funcao: envia um pacote para todos os roteadores adjacentes, exceto para o roteador de onde o pacote chegou.
    * Parametros: pai (o roteador de onde o pacote veio pacote) o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodosExcetoOQueChegou(Roteador pai, Pacote pacote) {
        new Thread(() -> {
            // pacotesGerados++; // Incrementa o contador de pacotes gerados
            if (this == pacote.getDestino()) {
                System.out.println("Foram gerados " + pacotesGerados + " ate chegar no destino");
                // Atualizar o Label na interface gráfica
                Platform.runLater(() -> {
                    Principal.pacotesGeradosLabel.setText("Pacotes Gerados: " + pacotesGerados);
                });
            }
            else {
                for (Map.Entry<Roteador, Aresta> entrada : adjacencias.entrySet()) {
                    Roteador vizinho = entrada.getKey();
                    if (vizinho != pai) {
                        Aresta aresta = entrada.getValue();
                        enviarPacote(vizinho, aresta, pacote, 2);
                    }
                }
            }

        }).start();
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodosExcetoOQueChegouTTL
    * Funcao: envia um pacote para todos os roteadores adjacentes, exceto para o roteador de onde o pacote chegou, considerando o TTL.
    * Parametros: pai (o roteador de onde o pacote veio pacote) e o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodosExcetoOQueChegouTTL(Roteador pai, Pacote pacote) {
        new Thread(() -> {
            // pacotesGerados++; // Incrementa o contador de pacotes gerados
            if (this == pacote.getDestino()) {
                System.out.println("Foram gerados " + pacotesGerados + " ate chegar no destino");
                // Atualizar o Label na interface gráfica
                Platform.runLater(() -> {
                    Principal.pacotesGeradosLabel.setText("Pacotes Gerados: " + pacotesGerados);
                });
            }
            else if (pacote.getTTL() > 0) { // Verifica se o TTL é maior que 0
                for (Map.Entry<Roteador, Aresta> entrada : adjacencias.entrySet()) {
                    Roteador vizinho = entrada.getKey();
                    if (vizinho != pai) {
                        Aresta aresta = entrada.getValue();
                        enviarPacote(vizinho, aresta, pacote, 3);
                    }
                }
            }
        }).start();
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodosExcetoOQueChegouTTLComProbabilidade
    * Funcao: envia um pacote para todos os roteadores adjacentes, exceto para o roteador de onde o pacote chegou, considerando o TTL e uma probabilidade p.
    * Parametros: pai (o roteador de onde o pacote veio pacote) e o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodosExcetoOQueChegouTTLComProbabilidade(Roteador pai, Pacote pacote) {
        new Thread(() -> {
            // pacotesGerados++; // Incrementa o contador de pacotes gerados
            if (this == pacote.getDestino()) {
                System.out.println("Foram gerados " + pacotesGerados + " ate chegar no destino");
                // Atualizar o Label na interface gráfica
                Platform.runLater(() -> {
                    Principal.pacotesGeradosLabel.setText("Pacotes Gerados: " + pacotesGerados);
                });
            }
            else if (pacote.getTTL() > 0) { // Verifica se o TTL é maior que 0
                for (Map.Entry<Roteador, Aresta> entrada : adjacencias.entrySet()) {
                    Roteador vizinho = entrada.getKey();
                    if (vizinho != pai) {
                        double probabilidade = random.nextInt(101); // Gera um número aleatório entre 0 e 100
                        if (probabilidade <= p) { // Envia o pacote com a probabilidade p
                            Aresta aresta = entrada.getValue();
                            enviarPacote(vizinho, aresta, pacote, 4); // Passa o código da opção 4
                        }
                    }
                }
            }
        }).start();
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacote
    * Funcao: envia o pacote para o roteador destino, visualizando o movimento do pacote na interface grafica.
    * Parametros: destino - o roteador destino. aresta - a aresta entre os roteadores. pacote - o pacote a ser enviado. opcao - a opcao do envio (1 a 4).
    * Retorno: void.
    * ***************************************************************
    */
    private void enviarPacote(Roteador destino, Aresta aresta, Pacote pacote, int opcao) {
        try {
            aresta.getSemaforo().acquire();
            pacotesGerados++; //incrementa o numero de pacotes gerados a madida que envia pacote

            Platform.runLater(() -> {
                Circle pacoteVisual = new Circle(10, javafx.scene.paint.Color.YELLOW); // Visual do pacote
                Principal.grafoPane.getChildren().add(pacoteVisual); // Adiciona o pacote ao Pane

                // Criando a linha de movimento
                Line path = new Line(this.getPosX(), this.getPosY(), destino.getPosX(), destino.getPosY());

                PathTransition transition = new PathTransition(Duration.seconds(2), path, pacoteVisual);
                transition.setOnFinished(event -> {
                    Principal.grafoPane.getChildren().remove(pacoteVisual); // Remove o visual do pacote
                    // System.out.println("Pacote chegou ao destino: " + destino.getID());

                    try {
                        pacote.decrementaTTL(); // Decrementa o TTL do pacote
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    switch (opcao) {
                        case 1:
                            destino.enviarPacoteTodos(pacote); // Envia pacote para todos os vizinhos
                            break;
                        case 2:
                            destino.enviarPacoteTodosExcetoOQueChegou(this, pacote); // Envia pacote para todos exceto o pai
                            break;
                        case 3:
                            destino.enviarPacoteTodosExcetoOQueChegouTTL(this, pacote); // Envia pacote para todos exceto o pai com TTL
                            break;
                        case 4:
                            destino.enviarPacoteTodosExcetoOQueChegouTTLComProbabilidade(this, pacote); // Envia pacote para todos exceto o pai com TTL com um probabilidade P
                    }
                });
                transition.play();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            aresta.getSemaforo().release();
        }
    }

    /*
    * ***************************************************************
    * Metodo: getPacotesGerados
    * Funcao: obtém o numero total de pacotes gerados.
    * Parametros: nenhum.
    * Retorno: o numero total de pacotes gerados.
    * ***************************************************************
    */
    public static int getPacotesGerados() {
        return pacotesGerados;
    }
}
