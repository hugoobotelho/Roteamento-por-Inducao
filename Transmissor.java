/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 28/04/2023
* Ultima alteracao.: 04/05/2023
* Nome.............: Camada Enlace
* Funcao...........: Simular o enquadramento da camada de Enlace de dados
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.concurrent.Semaphore;

public class Transmissor {
  private int qtdCaracters;
  private int tipoDeCodificacao;
  private int tipoDeEnquadramento;
  private int qtdBitsTotais = 0;
  private int tipoControleErro;
  private int porcentagemErro;
  private int qtdBitsErrados;
  // MeioDeComunicao meioDeComunicao = new MeioDeComunicao();

  public void tipoDeCodificacao(int n) {
    this.tipoDeCodificacao = n;
  };

  public void tipoDeEnquadramento(int n) {
    this.tipoDeEnquadramento = n;
  };

  public void tipoControleErro(int n) {
    this.tipoControleErro = n;
  };

  public void porcentagemErro(int n) {
    this.porcentagemErro = n;
  };

  public void qtdBitsErrados(int n) {
    this.qtdBitsErrados = n;
  };

  /*
   * ***************************************************************
   * Metodo: AplicacaoTransmissora.
   * Funcao: fazer aparacer o campo para digitar a mensagem e chamar a
   * CamadaDeAplicacaoTransmissora.
   * Parametros: sem parametros.
   * Retorno: sem retorno.
   */
  public void AplicacaoTransmissora() {
    TextArea textArea = new TextArea();
    Principal.root.getChildren().add(textArea);
    textArea.setLayoutX(63);
    textArea.setLayoutY(252);
    textArea.setStyle(
        "-fx-font-size: 14px;" + // Tamanho da fonte
            "-fx-border-radius: 10px;" + // Bordas arredondadas
            "-fx-padding: 10px;" + // Padding
            "-fx-background-color: #FFEEE5;" + // Cor de fundo
            "-fx-focus-color: transparent;" + // Cor de foco
            "-fx-faint-focus-color: transparent;" // Cor de foco fraco
    );

    // Definindo o tamanho do TextArea
    textArea.setPrefSize(110, 55);
    Button enviarButton = new Button("Enviar");
    enviarButton.setStyle(
        "-fx-font-size: 16px; " + // Tamanho da fonte
            "-fx-background-color: white; " + // Cor de fundo
            "-fx-text-fill: #435D7A; " + // Cor do texto
            "-fx-padding: 10px; " + // Padding
            "-fx-background-radius: 10px; " + // Bordas arredondadas
            "-fx-border-radius: 10px; " + // Bordas arredondadas
            "-fx-border-color: transparent;" // Cor da borda
    );
    // Mudando a cor do texto quando o mouse passa por cima
    enviarButton.setOnMouseEntered(e -> enviarButton.setStyle(
        "-fx-font-size: 16px; -fx-background-color: #435D7A; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 10px; fx-border-radius: 10px; -fx-border-color: transparent;"));
    // Voltando à cor original do texto quando o mouse sai de cima
    enviarButton.setOnMouseExited(e -> enviarButton.setStyle(
        "-fx-font-size: 16px; -fx-background-color: white; -fx-text-fill: #435D7A; -fx-padding: 10px; -fx-background-radius: 10px; fx-border-radius: 10px; -fx-border-color: transparent;"));

    enviarButton.setLayoutX(90);
    enviarButton.setLayoutY(400);

    enviarButton.setOnAction(e -> {
      String mensagem = textArea.getText();
      Principal.receptor.zeraBuffer();
      CamadaDeAplicacaoTransmissora(mensagem);
    });

    Principal.root.getChildren().add(enviarButton);

  }

  /*
   * ***************************************************************
   * Metodo: CamadaDeAplicacaoTransmissora.
   * Funcao: metodo para inserir os bits de cada caracter da mensagem em um array
   * de inteiros e chama a CamadaFisicaTransmissora.
   * Parametros: recebe uma mensagem do tipo String.
   * Retorno: sem retorno.
   */

  public void CamadaDeAplicacaoTransmissora(String mensagem) {
    char[] arrayDeCaracteres = mensagem.toCharArray();
    qtdCaracters = arrayDeCaracteres.length;
    int[] quadro = new int[(qtdCaracters + 3) / 4];
    int index = 0;
    int desloca = 31;
    for (int i = 0; i < qtdCaracters; i++) {
      char caractere = mensagem.charAt(i);
      String caractere8Bits = String.format("%8s", Integer.toBinaryString(caractere)).replace(' ', '0');
      // System.out.println(caractere8Bits);
      for (int j = 0; j < 8; j++) {
        if (caractere8Bits.charAt(j) == '1') {
          quadro[index] = quadro[index] | (1 << desloca);
        }
        desloca--;
        if (desloca < 0) {
          desloca = 31;
          index++;
        }
      }
    }
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[1])).replace(' ', '0'));
    CamadaEnlaceDadosTransmissora(quadro);
    // CamadaFisicaTransmissora(quadro);
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissora.
   * Funcao: metodo para fazer apenas o enquadramendo por enquanto e chamar a
   * proxima camada
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: sem retorno.
   */
  void CamadaEnlaceDadosTransmissora(int quadro[]) {
    // int[] quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramento(quadro);
    new Thread(() -> {
      try {
        CamadaEnlaceDadosTransmissoraEnquadramento(quadro);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }).start();
    // CamadaDeEnlaceTransmissoraControleDeErro(quadro);
    // CamadaDeEnlaceTransmissoraControleDeFluxo(quadro);
    // chama proxima camada
    // CamadaFisicaTransmissora(quadroEnquadrado);
  }// fim do metodo CamadaEnlaceDadosTransmissora

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramento.
   * Funcao: metodo para fazer o enquadramento com base no tipo de enquadramento
   * escolhido pelo usuario
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: retorna o quadro enquadrado.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramento(int quadro[]) throws InterruptedException {

    // int quadroEnquadrado[] = new int[0]; // mudar depois
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);

        // try {
        // Platform.runLater(() -> {
        // try {
        // CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // });
        CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        break;
      case 1: // insercao de bytes
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
        CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        // quadroEnquadrado =
        // CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
    }// fim do switch/case

    // return quadroEnquadrado;
  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres.
   * Funcao: metodo para fazer o enquadramento Contagem de Caracteres
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: retorna o quadro enquadrado.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(int quadro[])
      throws InterruptedException {
    // int tamanhoQuadroEnquadrado = (int) Math.ceil((double) qtdCaracters / 3);
    // System.out.println(tamanhoQuadroEnquadrado);
    int[] quadroEnquadrado = new int[1];
    int indexQuadroEnquadrado = 0;
    int indexQuadro = 0;
    String contador4 = "00000100"; // represeta o 4 em binario, dessa forma, estou dizendo que vai contar de 4 em 4
    String contador3 = "00000011";
    String contador2 = "00000010";
    int deslocaQuadroEnquadrado = 31;
    int deslocaQuadro = 31;
    int indiceCaracter = 0;
    int qtdIteracao = (qtdCaracters * 8) + 8 * ((int) Math.ceil((double) qtdCaracters / 3));
    int qtdCaractersInseridos = 0;
    int qtdBitsInseridos = 0;
    int aux = 0;
    // System.out.println(qtdIteracao);

    for (int i = 0; i < qtdIteracao; i++) {
      if (deslocaQuadroEnquadrado >= 24) {
        int num;
        if (qtdCaracters % 3 == 2 && qtdCaractersInseridos + 2 >= qtdCaracters) {
          num = contador3.charAt(indiceCaracter); // muda a contagem para 3 caracteres, isto e, a informacao de controle
                                                  // mais 2 caracteres
          aux = 3;
        } else if (qtdCaracters % 3 == 1 && qtdCaractersInseridos + 1 >= qtdCaracters) {
          //
          aux = 2;
          num = contador2.charAt(indiceCaracter); // muda a contagem para 2 caracteres, isto e, a informacao de controle
                                                  // mais 1 caracter
        } else {
          aux = 4;
          //
          num = contador4.charAt(indiceCaracter);
        }
        if (num == '1') {
          quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
              | (1 << deslocaQuadroEnquadrado);
        }
        deslocaQuadroEnquadrado--;
        if (indiceCaracter >= 7) {
          indiceCaracter = 0;
        } else {
          indiceCaracter++;
        }
        qtdBitsTotais++;
      } else {
        // System.out.println("Vai pegar os bits de quadro");
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
              | (1 << deslocaQuadroEnquadrado);
        }
        deslocaQuadroEnquadrado--;

        qtdBitsTotais++;
        if (deslocaQuadroEnquadrado < 0 && aux == 4) {
          // envia quadro chamando o controle de erro;
          Principal.semaforoEnviarQuadro.acquire();
          CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
          quadroEnquadrado = new int[1];
          deslocaQuadroEnquadrado = 31;
          indexQuadroEnquadrado = 0;
        } else if (deslocaQuadroEnquadrado == 7 && aux == 3) {
          // envia quadro chamando o controle de erro;
          Principal.semaforoEnviarQuadro.acquire();
          CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado);
          quadroEnquadrado = new int[1];
        } else if (deslocaQuadroEnquadrado == 15 && aux == 2) {
          // envia quadro chamando o controle de erro;
          Principal.semaforoEnviarQuadro.acquire();
          CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
          quadroEnquadrado = new int[1];
        }
        if (deslocaQuadroEnquadrado < 0) {
          deslocaQuadroEnquadrado = 31;
          indexQuadroEnquadrado++;
        }
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
          // System.out.println("Mudou o index de quadro");
          if (indexQuadro >= quadro.length) {
            break;
          }
        }
      }
      qtdBitsInseridos++;
      if (qtdBitsInseridos == 8) {
        qtdBitsInseridos = 0;
        qtdCaractersInseridos++;
      }
    }
    /*
     * System.out.println("Esse e o quadroEnquadrado contagem de caracteres");
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */

    // return quadroEnquadrado;
  }// fim do metodo CamadaEnlaceDadosTransmissoraContagemDeCaracteres

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes.
   * Funcao: metodo para fazer o enquadramento Insercao de Bytes.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: retorna o quadro enquadrado.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(int quadro[])
      throws InterruptedException {
    int[] quadroEnquadrado = new int[2];
    String flag = "00111111"; // minha flag sera a ? "01001000" seria H de Hugotoso
    String esc = "01000000"; // minha esc sera o @
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int indiceCaracter = 0;
    String auxCompara = "";
    int indexQuadro = 0;
    int deslocaQuadro = 31;

    // insere o primeiro flag
    for (int i = 0; i < 8; i++) {
      // System.out.println("Inserindo");
      char num = flag.charAt(indiceCaracter);
      // System.out.println(num);
      if (num == '1') {
        // System.out.println("colocou");
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      indiceCaracter++;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      qtdBitsTotais++;
    }
    indiceCaracter = 0;
    // insere a informacao de controle ESC caso a mensagem tenha uma flag ou uma esc
    // fake, caso contrario, apenas insere o caracter
    for (int j = 0; j < qtdCaracters; j++) { // percorre a qtd de caracters
      // insere a flag do proximo caso ja tenha lido tres caractes
      if (j % 3 == 0 && j != 0) {
        for (int i = 0; i < 8; i++) {
          // System.out.println("Inserindo");
          char num = flag.charAt(indiceCaracter);
          // System.out.println(num);
          if (num == '1') {
            // System.out.println("colocou");
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;

          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
      }
      auxCompara = "";
      for (int i = 0; i < 8; i++) { // para cada caracter perroce os bits e adiciona em auxCompara
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          auxCompara = auxCompara + '1';
        } else {
          auxCompara = auxCompara + '0';
        }
        deslocaQuadro--;
        qtdBitsTotais++;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
          if (indexQuadro >= quadro.length) {
            break;
          }
        }
      }
      if (auxCompara.equals(flag) || auxCompara.equals(esc)) { // verifica se o caracter e igual a flag ou esc
        // System.out.println("Vai inserir o caracter fake");
        for (int i = 0; i < 8; i++) { // colocar o esc antes
          // System.out.println("Vai inserir o esc antes");
          char num = esc.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
        for (int i = 0; i < 8; i++) { // coloca o caracter depois
          char num = auxCompara.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
        }
        indiceCaracter = 0;
        if (indexQuadroEnquadrado >= quadroEnquadrado.length) {
          break;
        }
      } else {
        // System.out.println("Vai inserir o caracter normal");
        for (int i = 0; i < 8; i++) {
          // System.out.println("Inserindo");
          char num = auxCompara.charAt(indiceCaracter);
          // System.out.println(num);
          if (num == '1') {
            // System.out.println("colocou");
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
        indiceCaracter = 0;
      }
      // verifica se ja leu tres caracteres ou se acabou de ler o ultimo, se sim,
      // adiciona a Flag
      if (j % 3 == 2 || j == qtdCaracters - 1) {
        for (int i = 0; i < 8; i++) {
          // System.out.println("Inserindo");
          char num = flag.charAt(indiceCaracter);
          // System.out.println(num);
          if (num == '1') {
            // System.out.println("colocou");
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        // envia quadro chamando o controle de erro;
        Principal.semaforoEnviarQuadro.acquire();
        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
        quadroEnquadrado = new int[2];
        indexQuadroEnquadrado = 0;
        deslocaQuadoEnquadrado = 31;
      }
    } // fim da insercao dos esc
    /*
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */
    // return quadroEnquadrado;
    // Chama o controle de Erro
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits.
   * Funcao: metodo para fazer o enquadramento Insercao de Bits.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: retorna o quadro enquadrado.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(int quadro[])
      throws InterruptedException {
    int[] quadroEnquadrado = new int[2];
    String flag = "01111110";
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int indiceCaracter = 0;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int contaBitsUm = 0;
    int contaCaracteres = 0;

    // insere o primeiro flag
    for (int i = 0; i < 8; i++) {
      // System.out.println("Inserindo");
      char num = flag.charAt(indiceCaracter);
      // System.out.println(num);
      if (num == '1') {
        // System.out.println("colocou");
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      indiceCaracter++;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }
      qtdBitsTotais++;
    }
    indiceCaracter = 0;

    for (int i = 0; i < 8 * qtdCaracters; i++) { // percorre o total de bits da qtd de caracters
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        contaBitsUm++;
        if (contaBitsUm == 6) {
          // inserir zero
          qtdBitsTotais++;
          // quadroEnquadrado[indexQuadroEnquadrado] =
          // quadroEnquadrado[indexQuadroEnquadrado] | (0 << deslocaQuadoEnquadrado);
          // //nao precisa inseri, basta pular pois o zero ja esta la
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          // zera o contador
          contaBitsUm = 0;
        }
        // depois inserir o bit
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      } else {
        contaBitsUm = 0;
      }
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }

      if ((i + 1) % 8 == 0) {
        contaCaracteres++;
      }
      // insere a flag a cada 3 caracters
      if ((i + 1) % 24 == 0 && contaCaracteres != qtdCaracters) { // se leu tres caracteres e o proximo nao e o utlimo,
                                                                  // entao insere flag do proximo tambem
        for (int j = 0; j < 8; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;

        }
        indiceCaracter = 0; // envia o quadro
        Principal.semaforoEnviarQuadro.acquire();
        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
        quadroEnquadrado = new int[2];
        indexQuadroEnquadrado = 0;
        deslocaQuadoEnquadrado = 31;
        for (int j = 0; j < 8; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
      } else if (contaCaracteres == qtdCaracters) { // se chegou no ultimo caracter insere apenas uma flag
        for (int j = 0; j < 8; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
        Principal.semaforoEnviarQuadro.acquire();
        CamadaDeEnlaceTransmissoraControleDeErro(quadroEnquadrado); // colocar um acquire depois
        break;
      }
      deslocaQuadro--;
      qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }
    }
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica.
   * Funcao: metodo para fazer o enquadramento Violacao da Camada.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: retorna o quadro enquadrado.
   */
  public void /* int[] */ CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // Decodificacao realizada na Camada Fisica
    // Uma vez que ele viola a camada fisica, a decodificacao deve ser feita la
    // quando passamos o algoritmo de binario para manchester ou Manchester
    // Differencial
    // return quadro;
    // Chama o controle de Erro
    CamadaDeEnlaceTransmissoraControleDeErro(quadro); // mudar depois
  }// fim do metodo CamadaEnlaceDadosReceptoraViolacaoDaCamadaFisica

  public void CamadaDeEnlaceTransmissoraControleDeErro(int[] quadroEnquadrado) {
    int[] quadroControleErro = new int[0]; // mudar depois o tamanho
    // Implementar logia de controle de erro
    switch (tipoControleErro) {
      case 0: // bit de paridade par
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(quadroEnquadrado);
        break;
      case 1: // bit de paridade impar
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(quadroEnquadrado);
        break;
      case 2: // CRC
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroCRC(quadroEnquadrado);
        break;
      case 3: // codigo de Hamming
        quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(quadroEnquadrado);
        break;
    }// fim do switch/case
     // System.out.println("Essa e a mensagem a ser enviada");
     // for (int i = 0; i < quadroControleErro.length; i++){
     // System.out.println(String.format("%32s",
     // Integer.toBinaryString(quadroControleErro[i])).replace(' ', '0'));
     // }
    CamadaFisicaTransmissora(quadroControleErro);
  }// fim do medoto CamadaEnlaceDadosTransmissoraControleDeErro

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(int quadro[]) {
    // implementacao do algoritmo
    int[] quadroControleErro = new int[quadro.length * 2];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        qtdBitsUm++;
        quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
            | (1 << deslocaQuadroControleErro);
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for

    if (qtdBitsUm % 2 != 0) { // se a quantidade de 1 for impar, entao adiciona um 1 em quadroControleErro
      quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
          | (1 << deslocaQuadroControleErro);
    }
    deslocaQuadroControleErro--;
    qtdBitsTotais++;
    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadePar

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(int quadro[]) {
    // implementacao do algoritmo
    int[] quadroControleErro = new int[quadro.length * 2];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        qtdBitsUm++;
        quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
            | (1 << deslocaQuadroControleErro);
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for

    if (qtdBitsUm % 2 == 0) { // se a quantidade de 1 for par, entao adiciona um 1 em quadroControleErro
      quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
          | (1 << deslocaQuadroControleErro);
    }
    deslocaQuadroControleErro--;
    qtdBitsTotais++;
    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadeImpar

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroCRC(int quadro[]) {
    // implementacao do algoritmo
    String crc32 = "100000100110000010001110110110111";
    char[] arrayDeCaracteres = crc32.toCharArray();
    int crc32Mascara = 0;
    int deslocaCRC32Mascara = 31;
    // cria a mascara crc32
    for (int i = 0; i < arrayDeCaracteres.length - 1; i++) {
      char caractere = crc32.charAt(i);
      if (caractere == '1') {
        crc32Mascara = crc32Mascara | (1 << deslocaCRC32Mascara);
      }
      deslocaCRC32Mascara--;
    } // terminou de criar a mascara do CRC32
    deslocaCRC32Mascara = 31;
    // System.out.println("CRC32");
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(crc32Mascara)).replace(' ', '0'));

    int[] quadroResto = new int[quadro.length * 2];
    int deslocaResto = 31;
    int indexResto = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    // coloca os bits de quadro em quadroResto
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for

    indexResto = 0;
    deslocaResto = 31;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadroResto[indexResto] >> deslocaResto) & 1;
      int deslocaRestoAux = deslocaResto;
      int indexRestoAux = indexResto;
      int deslocaCRC32MascaraAux = 31; // Vamos percorrer do bit mais significativo ao menos significativo do CRC32

      if (bit == 1) {
        for (int j = 0; j < 32; j++) {
          // System.out.println("Desloca resto: " + deslocaRestoAux + "\nIndex resto aux:
          // " + indexRestoAux);
          // System.out.println("Fazendo o XOR");

          int bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
          // System.out.println("Bit do quadro: " + bitQuadroResto);

          int bitCRC = (crc32Mascara >> deslocaCRC32MascaraAux) & 1;
          // System.out.println("Bit do crc: " + bitCRC);

          int xorResult = bitQuadroResto ^ bitCRC;
          // System.out.println("XOR " + xorResult);

          // Limpa o bit na posição atual e insere o bit resultante do XOR
          quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
              | (xorResult << deslocaRestoAux);

          deslocaRestoAux--;
          deslocaCRC32MascaraAux--;

          if (deslocaRestoAux < 0) {
            deslocaRestoAux = 31;
            indexRestoAux++;
          }
          if (deslocaCRC32MascaraAux < 0) {
            // Fazer XOR com o bit implícito 1 do CRC-32
            bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
            xorResult = bitQuadroResto ^ 1; // XOR com 1
            quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
                | (xorResult << deslocaRestoAux);

            deslocaRestoAux--;
            if (deslocaRestoAux < 0) {
              deslocaRestoAux = 31;
              indexRestoAux++;
            }

            // Reseta o deslocamento da máscara do CRC para começar do bit mais
            // significativo novamente
            deslocaCRC32MascaraAux = 31;
          }
        }
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
    }

    // System.out.println("Esse é o quadro");
    // for (int i = 0; i < quadro.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }
    // System.out.println("Esse é o quadro com o Resto");
    // for (int i = 0; i < quadroResto.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }
    deslocaQuadro = 31;
    indexQuadro = 0;
    deslocaResto = 31;
    indexResto = 0;
    // coloca os bits de quadro em quadroResto para enviar a mensagem com o resto
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for
    qtdBitsTotais += 32; // acrescenta 32 bits pq o crc tem 33 bits
    System.out.println(qtdBitsTotais);
    // System.out.println("Essa e a mensagem com o Resto no CRC");
    // for (int i = 0; i < quadroResto.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }
    return quadroResto;

    // usar polinomio CRC-32(IEEE 802)
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroCRC

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(int quadro[]) {
    // implementacao do algoritmo // implementacao do algoritmo para VERIFICAR SE
    // HOUVE ERRO
    int[] quadroHamming = new int[quadro.length * 3];
    int indexQuadroHamming = 0;
    int deselocaQuadroHamming = 31;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int expoente = 0;
    // coloca os bits de quadro em quadroHamming pulando as posicoes onde e potencia
    // de 2
    for (int i = 0; i < qtdBitsTotais; i++) {
      double potencia = Math.pow(2, expoente) - 1;
      if (i == (int) potencia) { // se a posicao for igual a uma potencia de 2 (menos 1, pq o array comeca de 0), entao apenas pula a posicao
        expoente++;
        i--;
        deselocaQuadroHamming--;
        if (deselocaQuadroHamming < 0) {
          deselocaQuadroHamming = 31;
          indexQuadroHamming++;
        }
      } else { // se nao, insere o bit de quadro na devida posicao de quadroHamming
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          quadroHamming[indexQuadroHamming] = quadroHamming[indexQuadroHamming] | (1 << deselocaQuadroHamming);
        }
        deselocaQuadroHamming--;
        if (deselocaQuadroHamming < 0) {
          deselocaQuadroHamming = 31;
          indexQuadroHamming++;
        }
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
      }
      if (indexQuadro >= quadro.length || indexQuadroHamming >= quadroHamming.length) {
        break;
      }

    } // fim do for
    
    System.out.println("Essa e o quadro Hamming sem a informacao de controle");
    for (int i = 0; i < quadroHamming.length; i++) {
      System.out.println(String.format("%32s", Integer.toBinaryString(quadroHamming[i])).replace(' ', '0'));
    }

    indexQuadroHamming = 0;
    //deselocaQuadroHamming = 31;
    //inserir os valores dos bits de paridade
    for (int i = 0; i < 7; i++){
      double posBitParidade = ((Math.pow(2, i)) - 1);
      int qtdUm = 0;
      deselocaQuadroHamming = 31 - (int)posBitParidade;
      int indexQuadroHammingAux = indexQuadroHamming;
      for (int j = 0; j < qtdBitsTotais; j++){ //vai verificar todos os bits relacionados ao bit de paridade
        for (int k = 0; k < (int)(posBitParidade)+1; k++){ //conta
          int bit = (quadroHamming[indexQuadroHammingAux] >> deselocaQuadroHamming) & 1;
          if (bit == 1) {
            qtdUm++;
          }
        }
        deselocaQuadroHamming -= posBitParidade+2; //pula a quantidade necessaria para somar os proximos bits
        if (deselocaQuadroHamming < 0){
          deselocaQuadroHamming = 31 - (int)posBitParidade;
          indexQuadroHammingAux++;
        }
        if (indexQuadroHammingAux >= quadroHamming.length){
          break;
        }
      }
      if (qtdUm%2 == 1) { //insere o 1 na posicao do bit de paridade, pois a quantidade de 1 e impar
        quadroHamming[indexQuadroHamming] = quadroHamming[indexQuadroHamming] | (1 << (int)posBitParidade);
      }  
    }

    System.out.println("Essa e o quadro Hamming");
    for (int i = 0; i < quadroHamming.length; i++) {
      System.out.println(String.format("%32s", Integer.toBinaryString(quadroHamming[i])).replace(' ', '0'));
    }
    return quadroHamming;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErroCodigoDehamming

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissora.
   * Funcao: transforma o array de inteiros em outro array codificado com base na
   * tipo de codificacao e chama o MeioDeComunicacao.
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno.
   */
  public void CamadaFisicaTransmissora(int quadro[]) {
    int[] fluxoBrutoDeBits = new int[0]; // ATENÇÃO: trabalhar com BITS!!!
    switch (tipoDeCodificacao) {
      case 0: // codificao binaria
        // qtdBitsTotais = qtdBitsTotais + (8 * qtdCaracters);
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        qtdBitsTotais = qtdBitsTotais + (8 * qtdCaracters);
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchester(quadro);
        // se o tipo do enquadramento for violacao, o calculo da qtdBitsTotais muda pois
        // quando adiciona informacao de controle, ela nao dobra pois nao passa pela
        // codificacao mancherter
        /*
         * if (tipoDeEnquadramento == 3) {
         * qtdBitsTotais = 2 * qtdBitsTotais - (qtdBitsTotais - qtdCaracters * 8);
         * } else {
         * qtdBitsTotais = 2 * qtdBitsTotais;
         * }
         */
        break;
      case 2: // codificacao manchester diferencial
        qtdBitsTotais = qtdBitsTotais + (8 * qtdCaracters);
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        // se o tipo do enquadramento for violacao, o calculo da qtdBitsTotais muda pois
        // quando adiciona informacao de controle, ela nao dobra pois nao passa pela
        // codificacao mancherterDiferencial
        /*
         * if (tipoDeEnquadramento == 3) {
         * qtdBitsTotais = 2 * qtdBitsTotais - (qtdBitsTotais - qtdCaracters * 8);
         * } else {
         * qtdBitsTotais = 2 * qtdBitsTotais;
         * }
         */
        break;
    }// fim do switch/case
    Principal.meioDeComunicao.setTipoDeCodificacao(tipoDeCodificacao);
    Principal.meioDeComunicao.setTipoDeEnquadramento(tipoDeEnquadramento);
    Principal.meioDeComunicao.setQtdBitsTotais(qtdBitsTotais);
    Principal.meioDeComunicao.setTipoControleErro(tipoControleErro);
    Principal.meioDeComunicao.setPorcentagemErro(porcentagemErro);
    Principal.meioDeComunicao.setQtdBistErrados(qtdBitsErrados);
    qtdBitsTotais = 0;
    Principal.meioDeComunicao.meioDeComunicacao(fluxoBrutoDeBits);
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoBinaria.
   * Funcao: metodo para codificar a mensagem em binario.
   * Parametros: recebe o array de inteiros.
   * Retorno: retorna o mesmo array de interios porque a codificacao eh binaria.
   */

  public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int quadro[]) {
    // implementacao do algoritmo
    /*
     * for (int i = 0; i < quadro.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadro[i])).replace(' ', '0'));
     * }
     */
    return quadro;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoBinaria

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoMancherster.
   * Funcao: metodo para codificar a mensagem em Mancherster (o O eh representado
   * por O1 e o 1 eh representado por 10).
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array codificado.
   */

  public int[] CamadaFisicaTransmissoraCodificacaoManchester(int quadro[]) {
    int[] fluxoCodificacaoMancherster = new int[quadro.length * 2]; // (qtdCaracters+1)/2
    int deslocaFluxo = 31;
    int indexFLuxo = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (1 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (0 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
      } else {
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (0 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
        fluxoCodificacaoMancherster[indexFLuxo] = fluxoCodificacaoMancherster[indexFLuxo] | (1 << deslocaFluxo);
        deslocaFluxo--;
        if (deslocaFluxo < 0) {
          deslocaFluxo = 31;
          indexFLuxo++;
        }
      }
      deslocaQuadro--;
      qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }
    }

    // Verifica Se o enquadramento selecionado foi a violacao da camada fisica, caso
    // tenha sido realiza o enquadramento
    // antes de enviar para o meio de comunicacao
    int quadroEnquadrado[];
    if (tipoDeEnquadramento == 3) {
      quadroEnquadrado = CodificacaoViolacaoCamadaFisica(fluxoCodificacaoMancherster);
    } else {
      quadroEnquadrado = fluxoCodificacaoMancherster;
    }
    /*
     * System.out.println("Esse é o quadro codificado mancherster");
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */
    return quadroEnquadrado;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoManchester

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoManchersterDiferencial.
   * Funcao: metodo para codificar a mensagem em Mancherster (o O eh representado
   * por uma inversao de sinal e o 1 eh representado por uma falta de inversao de
   * sinal).
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array codificado.
   */

  public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int quadro[]) {
    int[] fluxoCodificacaoManchersterDiferencial = new int[quadro.length * 2]; // (qtdCaracters+1)/2
    int deslocaFluxo = 31;
    int indexFLuxo = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int sinalAnterior = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        if (i == 0) {
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (1 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 1;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (0 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 0;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
        } else {
          // int num = deslocaFluxo + 1;
          // int sinalAnterior =
          // (fluxoCodificacaoManchersterDiferencial[indexFLuxo]>>(num))&1;
          if (sinalAnterior == 1) {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          } else {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          }
        }
      } else {
        if (i == 0) {
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (0 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 0;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
          fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
              | (1 << deslocaFluxo);
          deslocaFluxo--;
          sinalAnterior = 1;
          if (deslocaFluxo < 0) {
            deslocaFluxo = 31;
            indexFLuxo++;
          }
        } else {
          // int num = deslocaFluxo + 1;
          // int sinalAnterior =
          // (fluxoCodificacaoManchersterDiferencial[indexFLuxo]>>(num))&1;
          if (sinalAnterior == 1) {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          } else {
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (1 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 1;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
            fluxoCodificacaoManchersterDiferencial[indexFLuxo] = fluxoCodificacaoManchersterDiferencial[indexFLuxo]
                | (0 << deslocaFluxo);
            deslocaFluxo--;
            sinalAnterior = 0;
            if (deslocaFluxo < 0) {
              deslocaFluxo = 31;
              indexFLuxo++;
            }
          }
        }
      }
      deslocaQuadro--;
      qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }

    }
    /*
     * System.out.println("Esse e o quadro codificado diferencial");
     * for (int i = 0; i < fluxoCodificacaoManchersterDiferencial.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(fluxoCodificacaoManchersterDiferencial[i])).replace('
     * ', '0'));
     * }
     */

    // Verifica Se o enquadramento selecionado foi a violacao da camada fisica, caso
    // tenha sido realiza o enquadramento
    // antes de enviar para o meio de comunicacao
    int quadroEnquadrado[];
    if (tipoDeEnquadramento == 3) {
      quadroEnquadrado = CodificacaoViolacaoCamadaFisica(fluxoCodificacaoManchersterDiferencial);
    } else {
      quadroEnquadrado = fluxoCodificacaoManchersterDiferencial;
    }
    return quadroEnquadrado;
  }// fim do CamadaFisicaTransmissoraCodificacaoManchesterDiferencial

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica.
   * Funcao: metodo para fazer o enquadramento Violacao da Camada.
   * Parametros: recebe um quadro do tipo inteiro.
   * Retorno: retorna o quadro enquadrado.
   */
  public int[] CodificacaoViolacaoCamadaFisica(int[] quadro) {
    int qtdFlags = 2 * ((int) Math.ceil((double) qtdCaracters / 3)); // calcula a quantdidade de flags que sera inserida
    int qtdBits = (8 * qtdCaracters * 2) + (qtdFlags * 2); // mudar para qtdFlags*2 caso a flag seja 11
    int tamanhoQuadroEnquadrado = qtdBits / 32 + 1;
    /*
     * if (qtdBits % 32 == 0){
     * tamanhoQuadroEnquadrado = qtdBits / 32; // Index do Array ENQUADRADO
     * }
     * else{
     * tamanhoQuadroEnquadrado = qtdBits / 32 + 1;
     * }
     */
    // Criando Novo quadro com o novo tamanhoint [] quadroEnquadrado = new
    // int[quadro.length*3];
    int[] quadroEnquadrado = new int[tamanhoQuadroEnquadrado];
    String flag = "11";
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int indiceCaracter = 0;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int contaCaracteres = 0;

    // insere o primeiro flag
    for (int i = 0; i < 2; i++) {
      // System.out.println("Inserindo");
      char num = flag.charAt(indiceCaracter);
      // System.out.println(num);
      if (num == '1') {
        // System.out.println("colocou");
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      indiceCaracter++;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }
      qtdBitsTotais++;
    }
    indiceCaracter = 0;

    for (int i = 0; i < 16 * qtdCaracters; i++) { // percorre o total de bits da qtd de caracters
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      // insere os bits de quadro em quadroEnquadrado
      if (bit == 1) {
        quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
            | (1 << deslocaQuadoEnquadrado);
      }
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
        /*
         * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
         * break;
         * }
         */
      }
      if ((i + 1) % 16 == 0) {
        contaCaracteres++;
      }
      // insere a flag a cada 3 caracters
      if ((i + 1) % 48 == 0 && contaCaracteres != qtdCaracters) { // se leu tres caracteres e o proximo nao e o utlimo,
                                                                  // entao insere flag do proximo tambem
        for (int k = 0; k < 2; k++) {
          for (int j = 0; j < 2; j++) {
            char num = flag.charAt(indiceCaracter);
            if (num == '1') {
              quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                  | (1 << deslocaQuadoEnquadrado);
            }
            indiceCaracter++;
            deslocaQuadoEnquadrado--;
            if (deslocaQuadoEnquadrado < 0) {
              deslocaQuadoEnquadrado = 31;
              indexQuadroEnquadrado++;
              /*
               * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
               * break;
               * }
               */
            }
            qtdBitsTotais++;
          }
          indiceCaracter = 0;
        }
      } else if (contaCaracteres == qtdCaracters) { // se chegou no ultimo caracter insere apenas uma flag
        for (int j = 0; j < 2; j++) {
          char num = flag.charAt(indiceCaracter);
          if (num == '1') {
            quadroEnquadrado[indexQuadroEnquadrado] = quadroEnquadrado[indexQuadroEnquadrado]
                | (1 << deslocaQuadoEnquadrado);
          }
          indiceCaracter++;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
            /*
             * if (indexQuadroEnquadrado>=quadroEnquadrado.length){
             * break;
             * }
             */
          }
          qtdBitsTotais++;
        }
        indiceCaracter = 0;
      }
      deslocaQuadro--;
      qtdBitsTotais++;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
        if (indexQuadro >= quadro.length) {
          break;
        }
      }
    }
    /*
     * System.out.println("Esse e o quadroEnquadrado Violacao de Camada");
     * for (int i = 0; i < quadroEnquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroEnquadrado[i])).replace(' ', '0'));
     * }
     */

    return quadroEnquadrado;
  }

}