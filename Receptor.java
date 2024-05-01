/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 22/03/2023
* Ultima alteracao.: 07/04/2023
* Nome.............: Camada Fisica
* Funcao...........: Simular a camada fisica de uma rede
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import javafx.scene.control.TextArea;

public class Receptor {
  private int qtdCaracters = 0;
  private int tipoDeDecodificacao = 0;
  TextArea text = new TextArea();

  public Receptor(){
    text.setLayoutX(918);
    text.setLayoutY(242);
    text.setStyle(
    "-fx-font-size: 14px;" + // Tamanho da fonte
    "-fx-border-radius: 10px;" + // Bordas arredondadas
    "-fx-padding: 10px;" + // Padding
    "-fx-background-color: #435D7A;" + // Cor de fundo
    "-fx-focus-color: transparent;" + // Cor de foco
    "-fx-faint-focus-color: transparent;" // Cor de foco fraco
    );
    // Definindo o tamanho do TextArea e setando a mensagem
    text.setPrefSize(145, 90);
    text.setEditable(false);
    Principal.root.getChildren().add(text);
  }
  
  /* ***************************************************************
  * Metodo: setQtdCaracters.
  * Funcao: metodo para setar a quantidade de caracters da mensagem.
  * Parametros: recebe uma quantidade de caracters do tipo inteiro.
  * Retorno: sem retorno.
  *************************************************************** */

  public void setQtdCaracters(int qtd){
    this.qtdCaracters = qtd;
  }
  
  /* ***************************************************************
  * Metodo: setTipoDeCodificacao.
  * Funcao: metodo para setar tipo de codificacao da mensagem.
  * Parametros: recebe uma quantidade de caracters do tipo inteiro.
  * Retorno: sem retorno.
  *************************************************************** */
  public void setTipoDeCodificacao(int codificacao){
    this.tipoDeDecodificacao = codificacao;
  }
  
  /* ***************************************************************
  * Metodo: CamadaFisicaReceptora.
  * Funcao: metodo para chamar a codificacao necessaria para decodificar a mensagem com base no tipo de codificacao e depois chamar a CamadaDeAplicacaoReceptora.
  * Parametros: recebe um array do tipo inteiro referenete a mensagem codificada.
  * Retorno: sem retorno.
  *************************************************************** */
  public void CamadaFisicaReceptora (int fluxoBrutoDeBits[]) {
    int [] quadro = new int[0]; //ATENÇÃO: trabalhar com BITS!!!
    switch (tipoDeDecodificacao) {
    case 0 : //codificao binaria
      quadro = CamadaFisicaReceptoraDecodificacaoBinaria(fluxoBrutoDeBits);
      break;
    case 1 : //codificacao manchester
      quadro = CamadaFisicaReceptoraDecodificacaoManchester(fluxoBrutoDeBits);
      break;
    case 2 : //codificacao manchester diferencial
       quadro = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
       break;
    }//fim do switch/case
    //chama proxima camada
    CamadaDeAplicacaoReceptora(quadro);
  }//fim do metodo CamadaFisicaTransmissora
    
  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoBinaria.
  * Funcao: metodo para decodificar a mensagem binaria.
  * Parametros: recebe o array de inteiros.
  * Retorno: retorna um array de interios decodificado.
  *************************************************************** */

  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int fluxoBrutoDeBits []){
    int [] quadro = new int[(qtdCaracters + 3)/4];
    int indexQuadro = 0;
    for (int i = 0; i < fluxoBrutoDeBits.length; i++){
      for (int j = 31; j >= 0; j--){
        int bit = (fluxoBrutoDeBits[i] >> j) & 1;
        quadro[indexQuadro] = quadro[indexQuadro] | (bit << j);
      }
      indexQuadro++;
    }
    /*
    for (int i = 0; i < quadro.length; i++){
      System.out.println("Esse e o quadro receptor codificacao binaria "+String.format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    }   
    */
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoMancherster.
  * Funcao: metodo para decodificar a mensagem em Mancherster (cada par O1 representa o 0 e cada para 10 representa o 1).
  * Parametros: recebe o array de inteiros.
  * Retorno: retornar o array decodificado.
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int fluxoBrutoDeBits []){
    /*
    for (int i = 0; i < fluxoBrutoDeBits.length; i++){
      System.out.println("Esse e o fluxoBrutoDeBits receptor codificacao Mancherster "+String.format("%32s", Integer.toBinaryString(fluxoBrutoDeBits[i])).replace(' ', '0'));
    }  
    */
    int [] quadro = new int[(qtdCaracters + 3)/4];
    int indexQuadro = 0;
    int posBit = 31;
    for (int i = 0; i < fluxoBrutoDeBits.length; i++){
      for (int j = 31; j >= 0; j-=2){
        int posAnterior = j;
        int posSucessor = j-1;
        int bitAnterior = (fluxoBrutoDeBits[i] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[i]>> posSucessor) & 1;
        //System.out.println("Bit anterior: " + bitAnterior + "\nBit sucessor: " + bitSucessor);
        if (bitAnterior == 1 && bitSucessor == 0){
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<posBit);
        }
        else if (bitAnterior == 0 && bitSucessor == 1){
          quadro[indexQuadro] = quadro[indexQuadro] | (0<<posBit);                
        }
        posBit--;
      }
      if ((i+1)%2==0){
        indexQuadro++;
        posBit = 31;
      }
    }
    /*
    for (int i = 0; i < quadro.length; i++){
      System.out.println("Esse e o quadro receptor codificacao Mancherster "+String.format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    } 
    */  
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptorarDecodificacaoManchersterDiferencial.
  * Funcao: metodo para decodificar a mensagem em ManchersterDiferencial (onda a inversao de sinal representa o 0 e a falta de inversao de sinall representa o 1).
  * Parametros: recebe o array de inteiros.
  * Retorno: retornar o array decodificado.
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int fluxoBrutoDeBits []){
    /*
    for (int i = 0; i < fluxoBrutoDeBits.length; i++){
      System.out.println("Esse e o fluxoBrutoDeBits receptor codificacao Mancherster "+String.format("%32s", Integer.toBinaryString(fluxoBrutoDeBits[i])).replace(' ', '0'));
    }
    */  
    int [] quadro = new int[(qtdCaracters + 3)/4];
    int indexQuadro = 0;
    int posBit = 30;
    int primeiroBit = (fluxoBrutoDeBits[0] >> 31) & 1;
    int segundoBit = (fluxoBrutoDeBits[0] >> 30) & 1;
    boolean flag = false;
    if (primeiroBit == 1 && segundoBit == 0){
      quadro[0] = quadro[0] | (1<<31);
    }
    else if (primeiroBit == 0 && segundoBit == 1){
      quadro[0] = quadro[0] | (0<<31);
    }
    for (int i = 0; i < fluxoBrutoDeBits.length; i++){
      for (int j = 29; j >= 0; j-=2){
        if(i == fluxoBrutoDeBits.length-1 && (j == 15 && qtdCaracters%2 == 1)){
          flag = true;
          break;
        }
        int posAnterior = j+1;
        int posSucessor = j;
        int bitAnterior = (fluxoBrutoDeBits[i] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[i]>> posSucessor) & 1;
        //System.out.println("Bit anterior: " + bitAnterior + "\nBit sucessor: " + bitSucessor);
        if ((bitAnterior == bitSucessor)){
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<posBit);
        }
        else if (bitAnterior != bitSucessor){
          quadro[indexQuadro] = quadro[indexQuadro] | (0<<posBit);                
        }
        posBit--;
        }
        if (flag){
          break;
        }
        if ((i+1)%2==0){
          indexQuadro++;
          posBit = 31;
        }
        if (i+1 != fluxoBrutoDeBits.length){
          if (((fluxoBrutoDeBits[i] >> 0) & 1) == ((fluxoBrutoDeBits[i+1] >> 31) & 1)){
            quadro[indexQuadro] = quadro[indexQuadro] | (1<<posBit);
          }
          else if(((fluxoBrutoDeBits[i] >> 0) & 1) != ((fluxoBrutoDeBits[i+1] >> 31) & 1)){
            quadro[indexQuadro] = quadro[indexQuadro] | (0<<posBit);
          }
          posBit--;
        }
    }
    /*
    for (int i = 0; i < quadro.length; i++){
      System.out.println("Esse e o quadro receptor codificacao Mancherster "+String.format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    }
    */  
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaDeAplicacaoReceptora.
  * Funcao: metodo para trasnformar o array de interos decodificado em mensagem e chamar a AplicacaoReceptora.
  * Parametros: recebe o array de inteiros.
  * Retorno: sem retorno.
  *************************************************************** */
  public void CamadaDeAplicacaoReceptora (int quadro []) {
    /*
    for (int i = 0; i < quadro.length; i++){
      System.out.println("Esse e o quadro CamadaDeAplicacaoReceptora "+String.format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    }
    */  
    String mensagem = "";
    // String representando a sequência de bits
    String binaryString = "";
    int contador = 0;
    for (int i = 0; i < quadro.length; i++){
      for (int j = 31; j >=0; j--){
        int bit = (quadro[i] >> j) & 1;
        if (bit == 1){
          binaryString += "1";
        }
        else{
          binaryString += "0";
        }
        contador += 1;
        if (contador == 8){
          int intValue = Integer.parseInt(binaryString, 2);
          mensagem += (char) intValue;
          binaryString = "";
          contador = 0;
        }                
      }
    }
    //System.out.println("Essa é a mensagem:" + mensagem);
    //chama proxima camada
    AplicacaoReceptora(mensagem);
    }//fim do metodo CamadaDeAplicacaoReceptora

  /* ***************************************************************
  * Metodo: AplicacaoTransmissora.
  * Funcao: fazer a mensagem aparecer na tela.
  * Parametros: recebe uma mensagem do tipo String.
  * Retorno: sem retorno.
  *************************************************************** */
    public void AplicacaoReceptora (String mensagem) {
        text.setText(mensagem);
    }//fim do metodo AplicacaoReceptora
  
}
