import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.geometry.Pos;

public class Principal extends Application {
    public Pane root = new Pane();
    public static Pane grafoPane = new Pane(); // Pane para o grafo
    private Grafo grafo = new Grafo(); // Instancia a classe Grafo

    @Override
    public void start(Stage primaryStage) {
        // Criar o menu lateral
        VBox menuLateral = new VBox(10);
        menuLateral.setStyle("-fx-padding: 20;");
        menuLateral.setPrefWidth(200); // Define a largura do menu lateral
        menuLateral.setLayoutX(30);
        menuLateral.setLayoutY(20);
        menuLateral.setAlignment(Pos.CENTER_LEFT); // Alinha os elementos do VBox à esquerda

        // Estilizando o menu lateral
        menuLateral.setStyle("-fx-background-color: #2E2E2E; -fx-padding: 20; -fx-border-color: #444444; -fx-border-width: 2px;");

        Label escolhaLabel = new Label("Escolha uma Opcao:");
        escolhaLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton opcao1 = new RadioButton("Opcao 1");
        RadioButton opcao2 = new RadioButton("Opcao 2");
        RadioButton opcao3 = new RadioButton("Opcao 3");
        RadioButton opcao4 = new RadioButton("Opcao 4");

        RadioButton[] opcoes = { opcao1, opcao2, opcao3, opcao4 };
        for (RadioButton opcao : opcoes) {
            opcao.setToggleGroup(toggleGroup);
            opcao.setStyle("-fx-text-fill: #DDDDDD;");
        }

        Button iniciarButton = new Button("Iniciar");
        Button sobreButton = new Button("Sobre");

        // Estilizando botões
        String buttonStyle = "-fx-background-color: #3A3A3A; -fx-text-fill: #FFFFFF; -fx-border-radius: 5px; -fx-background-radius: 5px;";
        iniciarButton.setStyle(buttonStyle + "-fx-border-color: #5A5A5A;");
        sobreButton.setStyle(buttonStyle + "-fx-border-color: #5A5A5A;");

        // Estilo para o estado hover (passando o mouse sobre o botão)
        aplicarEfeitoHover(iniciarButton);
        aplicarEfeitoHover(sobreButton);

        iniciarButton.setOnAction(event -> {
          RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
          if (selectedRadioButton != null) {
              String opcaoEscolhida = selectedRadioButton.getText().toLowerCase();
              grafo.setOpcao(opcaoEscolhida);
              grafo.enviarPacote();
          }
      });
      

        // Ação ao clicar no botão "Sobre"
        sobreButton.setOnAction(event -> exibirInformacoesSobre(primaryStage));

        // Colocar os botões em um HBox
        HBox botoesBox = new HBox(10, iniciarButton, sobreButton);
        botoesBox.setStyle("-fx-padding: 10;");
        botoesBox.setAlignment(Pos.CENTER_LEFT); // Alinha os elementos do HBox à esquerda

        menuLateral.getChildren().addAll(escolhaLabel, opcao1, opcao2, opcao3, opcao4, botoesBox);

        // Configurar a posição do menu lateral
        root.getChildren().add(menuLateral);

        // Configurar a posição e o tamanho do Pane do grafo
        grafoPane.setLayoutX(220); // Definir posição X do grafo
        grafoPane.setLayoutY(20); // Definir posição Y do grafo
        grafoPane.setPrefSize(860, 660); // Definir o tamanho da área do grafo
        grafoPane.setStyle("-fx-background-color: #1E1E1E;"); // Cor de fundo do grafo

        root.setStyle("-fx-background-color: #1A1A1A;"); // Cor de fundo da aplicação
        root.getChildren().add(grafoPane);

        // Exibir o grafo ao iniciar a aplicação
        exibirGrafo();

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Camada de Rede");
        primaryStage.setResizable(false); // Bloqueio da maximização da tela
        primaryStage.show();

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    // Método para exibir o grafo
    private void exibirGrafo() {
        // Grafo grafo = new Grafo(); // Instancia a classe Grafo
        Pane novoGrafoPane = grafo.criarGrafoAPartirDeArquivo("backbone.txt"); // Cria o grafo a partir do arquivo
        grafoPane.getChildren().clear(); // Limpa o Pane do grafo antes de adicionar o novo grafo
        grafoPane.getChildren().add(novoGrafoPane); // Adiciona o novo grafo ao Pane
    }

    // Método para exibir a janela de informações sobre a aplicação
    private void exibirInformacoesSobre(Window owner) {
        Stage infoStage = new Stage();
        infoStage.initModality(Modality.APPLICATION_MODAL);
        infoStage.initOwner(owner);
        infoStage.setTitle("Sobre");

        VBox infoLayout = new VBox(10);
        infoLayout.setStyle("-fx-padding: 20; -fx-background-color: #2E2E2E;"); // Fundo escuro para o modal
        infoLayout.setAlignment(Pos.CENTER_LEFT); // Alinha os elementos do VBox à esquerda

        Label infoLabel = new Label("Texto informativo.");
        infoLabel.setStyle("-fx-text-fill: #FFFFFF;");

        Button fecharButton = new Button("Fechar");
        fecharButton.setStyle("-fx-background-color: #3A3A3A; -fx-text-fill: #FFFFFF; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #5A5A5A;");

        // Aplicar efeito hover no botão Fechar
        aplicarEfeitoHover(fecharButton);

        // Ação ao clicar no botão "Fechar"
        fecharButton.setOnAction(event -> infoStage.close());

        infoLayout.getChildren().addAll(infoLabel, fecharButton);

        Scene infoScene = new Scene(infoLayout, 300, 200);
        infoStage.setScene(infoScene);
        infoStage.show();
    }

    // Método para aplicar o efeito hover aos botões
    private void aplicarEfeitoHover(Button button) {
        button.setOnMouseEntered(event -> button.setStyle(
            "-fx-background-color: #FFFFFF; -fx-text-fill: #3A3A3A; -fx-cursor: hand; -fx-border-color: #5A5A5A; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        button.setOnMouseExited(event -> button.setStyle(
            "-fx-background-color: #3A3A3A; -fx-text-fill: #FFFFFF; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #5A5A5A;"));
    }

    /*
     * ***************************************************************
     * Metodo: main.
     * Funcao: metodo para iniciar a aplicacao.
     * Parametros: padrao java.
     * Retorno: sem retorno.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
