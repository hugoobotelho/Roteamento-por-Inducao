/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 31/08/2024
* Ultima alteracao.: 05/09/2024
* Nome.............: Roteomento por Inundacao
* Funcao...........: Simular o algoritmo de roteamento por inundacao da rede
*************************************************************** */

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Classe que representa o grafo
public class Grafo {
    private static final int RADIUS = 200;
    private static final int NODE_RADIUS = 30;
    private static final int CENTER_X = 450;
    private static final int CENTER_Y = 300;

    private static Pane grafoPane = new Pane(); // Pane para o grafo

    private Map<Integer, Roteador> nos = new HashMap<>();
    private int totalNodes;
    private Integer inicio = null; // Variável para armazenar o nó de início
    private Integer fim = null; // Variável para armazenar o nó de fim
    private String opcao = "";

    /*
    * ***************************************************************
    * Metodo: setOpcao
    * Funcao: seta a opcao de algoritmo escolhida pelo usuario
    * Parametros: recebe uma string para informar a opcao
    * Retorno: void.
    * ***************************************************************
    */
    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    /*
    * ***************************************************************
    * Metodo: getPane
    * Funcao: retornar o grafoPane
    * Parametros: sem parametros
    * Retorno: grafoPane.
    * ***************************************************************
    */
    public static Pane getPane() {
        return grafoPane;
    }

   /*
   * ***************************************************************
   * Metodo: criarGrafoAPartirDeArquivo.
   * Funcao: gera o grafo para aparecer na tela a partir do backbone
   * Parametros: recebe um string do nome do arquivo.
   * Retorno: retorna um pane que contem o grafo.   
   * ***************************************************************
   */
    public Pane criarGrafoAPartirDeArquivo(String arquivo) {
        Pane grafoPane = new Pane();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            // Ler a primeira linha para obter o número total de nós
            String linha = br.readLine();
            totalNodes = Integer.parseInt(linha.split(";")[0]);

            // Ler as linhas subsequentes para criar os nós e as arestas
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                int no1Id = Integer.parseInt(partes[0]);
                int no2Id = Integer.parseInt(partes[1]);
                int valor = Integer.parseInt(partes[2]);

                // Criar ou obter os nós
                Roteador no1 = nos.computeIfAbsent(no1Id, n -> criarNo(n, grafoPane));
                Roteador no2 = nos.computeIfAbsent(no2Id, n -> criarNo(n, grafoPane));

                // Criar a aresta entre os nós
                criarAresta(no1, no2, valor, grafoPane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return grafoPane; // Retorna o Pane contendo o grafo
    }

    /*
    * ***************************************************************
    * Metodo: criarNo
    * Funcao: cria e posiciona um nó no grafo, representado por um botão com uma imagem de roteador.
    * Parametros: recebe o ID do nó e o Pane onde o nó será adicionado.
    * Retorno: retorna o objeto Roteador criado.
    * ***************************************************************
    */
    private Roteador criarNo(int nodeId, Pane pane) {
        // Distribui os nós de forma circular baseado no total de nós lido do arquivo
        double angle = 2 * Math.PI * (nodeId - 1) / totalNodes;
        double x = CENTER_X + RADIUS * Math.cos(angle);
        double y = CENTER_Y + RADIUS * Math.sin(angle);
    
        // Carrega a imagem do roteador
        Image routerImagem = new Image("/img/router3.png");
        ImageView routerImageView = new ImageView(routerImagem);
    
        // Cria o botão do nó e adiciona a imagem
        Button nodeButton = new Button();
        nodeButton.setGraphic(routerImageView); // Adiciona a imagem ao botão
        nodeButton.setLayoutX(x - NODE_RADIUS);
        nodeButton.setLayoutY(y - NODE_RADIUS);
        nodeButton.setStyle("-fx-background-color: cornflowerblue; -fx-text-fill: white;");
    
        // Ação ao clicar no botão (nó)
        nodeButton.setOnAction(event -> selecionarNo(nodeId, nodeButton));
    
        pane.getChildren().add(nodeButton);
        
        // Criação de um Label para exibir a letra do roteador
        char letraDoRoteador = (char) ('A' + nodeId - 1);  // A partir de 'A'
    
        // Calcula a posição do Label fora do círculo, aumentando o raio
        double labelRadius = RADIUS + 40;  // Aumenta o raio para posicionar o Label fora do círculo
        double labelX = CENTER_X + labelRadius * Math.cos(angle);
        double labelY = CENTER_Y + labelRadius * Math.sin(angle);
    
        Label nodeLabel = new Label(String.valueOf(letraDoRoteador));
        nodeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"); // Estilo do texto com negrito
        nodeLabel.setLayoutX(labelX - NODE_RADIUS / 2);  // Posiciona o label fora do botão
        nodeLabel.setLayoutY(labelY - NODE_RADIUS / 2);  // Alinha verticalmente o label com o botão
    
        pane.getChildren().add(nodeLabel);
    
        Roteador no = new Roteador(nodeId, nodeButton, x, y);
        nos.put(nodeId, no);
        return no;
    }
    
    /*
    * ***************************************************************
    * Metodo: criarAresta
    * Funcao: cria uma aresta (linha) entre dois nós e exibe o valor associado no meio da linha.
    * Parametros: dois objetos Roteador representando os nós conectados e o valor da aresta.
    * Retorno: void.
    * ***************************************************************
    */
    private void criarAresta(Roteador no1, Roteador no2, int valor, Pane pane) {
        no1.adicionarAdjacencia(no2, valor);
        no2.adicionarAdjacencia(no1, valor);

        Line line = new Line(
                no1.getButton().getLayoutX() + NODE_RADIUS, no1.getButton().getLayoutY() + NODE_RADIUS,
                no2.getButton().getLayoutX() + NODE_RADIUS, no2.getButton().getLayoutY() + NODE_RADIUS);
        line.setStroke(Color.GRAY);
        pane.getChildren().add(line);

        // Adicionar texto com o valor no meio da linha
        double midX = (line.getStartX() + line.getEndX()) / 2;
        double midY = (line.getStartY() + line.getEndY()) / 2;
        Text valueLabel = new Text(midX, midY, String.valueOf(valor));
        valueLabel.setFill(Color.WHITE); // Define a cor do texto para branco
        pane.getChildren().add(valueLabel);
    }


    /*
    * ***************************************************************
    * Metodo: selecionarNo
    * Funcao: manipula a seleção de um no, definindo-o como inicio ou fim.
    * Parametros: recebe o ID do no e o botao associado ao no.
    * Retorno: void.
    * ***************************************************************
    */
    private void selecionarNo(int nodeId, Button node) {
        if (inicio != null && inicio == nodeId) {
            // Se o nó clicado for o nó de início já selecionado, desmarque-o
            inicio = null;
            node.setStyle("-fx-background-color: cornflowerblue; -fx-text-fill: white;");
        } else if (fim != null && fim == nodeId) {
            // Se o nó clicado for o nó de fim já selecionado, desmarque-o
            fim = null;
            node.setStyle("-fx-background-color: cornflowerblue; -fx-text-fill: white;");
        } else if (inicio == null) {
            // Se o nó de início não estiver selecionado, selecione este nó como início
            inicio = nodeId;
            node.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        } else if (fim == null && nodeId != inicio) {
            // Se o nó de fim não estiver selecionado, selecione este nó como fim
            fim = nodeId;
            node.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        }
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacote
    * Funcao: enviar o pacote baseado na opcao de simulacao selecionada.
    * Parametros: nenhum.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacote() {
        if (inicio != null && fim != null) {
            switch (opcao) {
                case "opcao 1":
                    enviarPacoteTodos();
                    break;
                case "opcao 2":
                    enviarPacoteTodosExcetoOQueChegou();
                    break;
                case "opcao 3":
                    enviarPacoteTodosExcetoOQueChegouTTL();
                    break;
                case "opcao 4":
                    enviarPacoteOpcao4();
                    break;

                default:
                    enviarPacoteTodos();
                    break;
            }
        }
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodos
    * Funcao: simula o envio do pacote para todos os roteadores.
    * Parametros: nenhum.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodos() {
        Roteador roteadorInicial = nos.get(inicio);
        // roteadorInicial.setPacotesGerados();
        Roteador roteadorFinal = nos.get(fim);
        Pacote pacote = new Pacote(roteadorFinal);
        // Resetar o contador de pacotes gerados antes de iniciar
        Roteador.setPacotesGerados(0);
        Principal.pacotesGeradosLabel.setText("Pacotes Gerados: 0");
        roteadorInicial.enviarPacoteTodos(pacote);
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodosExcetoOQueChegou
    * Funcao: envia um pacote para todos os roteadores adjacentes, exceto para o roteador de onde ele veio.
    * Parametros: o roteador de onde o pacote veio e o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodosExcetoOQueChegou() {
        Roteador roteadorInicial = nos.get(inicio);
        // roteadorInicial.setPacotesGerados();
        Roteador roteadorFinal = nos.get(fim);
        Pacote pacote = new Pacote(roteadorFinal);
        // Resetar o contador de pacotes gerados antes de iniciar
        Roteador.setPacotesGerados(0);
        Principal.pacotesGeradosLabel.setText("Pacotes Gerados: 0");
        roteadorInicial.enviarPacoteTodosExcetoOQueChegou(roteadorInicial, pacote);
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodosExcetoOQueChegouTTL
    * Funcao: envia um pacote para todos os roteadores adjacentes, exceto para o roteador de onde ele veio, considerando o TTL (Time To Live).
    * Parametros: o roteador de onde o pacote veio e o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteTodosExcetoOQueChegouTTL() {
        Roteador roteadorInicial = nos.get(inicio);
        // roteadorInicial.setPacotesGerados();
        Roteador roteadorFinal = nos.get(fim);
        Pacote pacote = new Pacote(roteadorFinal);
        // Resetar o contador de pacotes gerados antes de iniciar
        Roteador.setPacotesGerados(0);
        Principal.pacotesGeradosLabel.setText("Pacotes Gerados: 0");
        roteadorInicial.enviarPacoteTodosExcetoOQueChegouTTL(roteadorInicial, pacote);
    }

    /*
    * ***************************************************************
    * Metodo: enviarPacoteTodosExcetoOQueChegouTTLComProbabilidade
    * Funcao: envia um pacote para todos os roteadores adjacentes, exceto para o roteador de onde ele veio, considerando o TTL (Time To Live) com uma probabilidade definida.
    * Parametros: o roteador de onde o pacote veio e o pacote a ser enviado.
    * Retorno: void.
    * ***************************************************************
    */
    public void enviarPacoteOpcao4() {
        Roteador roteadorInicial = nos.get(inicio);
        // roteadorInicial.setPacotesGerados();
        Roteador roteadorFinal = nos.get(fim);
        Pacote pacote = new Pacote(roteadorFinal);
        // Resetar o contador de pacotes gerados antes de iniciar
        Roteador.setPacotesGerados(0);
        Principal.pacotesGeradosLabel.setText("Pacotes Gerados: 0");
        roteadorInicial.enviarPacoteTodosExcetoOQueChegouTTLComProbabilidade(roteadorInicial, pacote);
    }
}