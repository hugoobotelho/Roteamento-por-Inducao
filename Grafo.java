import javafx.scene.control.Button;
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

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    public static Pane getPane() {
        return grafoPane;
    }

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
        Roteador no = new Roteador(nodeId, nodeButton, x, y);
        nos.put(nodeId, no);
        return no;
    }

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

    public void enviarPacoteTodos() {
        Roteador roteadorInicial = nos.get(inicio);
        roteadorInicial.enviarPacoteTodos();
    }

    public void enviarPacoteTodosExcetoOQueChegou() {
        System.out.println("entrou aqui");
        Roteador roteadorInicial = nos.get(inicio);
        roteadorInicial.enviarPacoteTodosExcetoOQueChegou(roteadorInicial);
    }

    public void enviarPacoteTodosExcetoOQueChegouTTL() {

    }

    public void enviarPacoteOpcao4() {

    }
}
