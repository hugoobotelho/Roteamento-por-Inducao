/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 28/04/2023
* Ultima alteracao.: 04/05/2023
* Nome.............: Camada Enlace
* Funcao...........: Simular o enquadramento da camada de Enlace de dados
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import javafx.scene.control.TextArea;

public class Receptor {
  private int tipoDeDecodificacao = 0;
  private int tipoDeEnquadramento = 0;
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
  * Metodo: setTipoDeCodificacao.
  * Funcao: metodo para setar tipo de codificacao da mensagem.
  * Parametros: recebe uma quantidade de caracters do tipo inteiro.
  * Retorno: sem retorno.
  *************************************************************** */
  public void setTipoDeCodificacao(int codificacao){
    this.tipoDeDecodificacao = codificacao;
  }

  /* ***************************************************************
  * Metodo: setTipoDeEnquadramento.
  * Funcao: metodo para setar tipo de enquadramento da mensagem.
  * Parametros: recebe o tipo do enquadramento.
  * Retorno: sem retorno.
  *************************************************************** */
  public void setTipoDeEnquadramento(int tipoDeEnquadramento){
    this.tipoDeEnquadramento = tipoDeEnquadramento;
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
      if (tipoDeEnquadramento == 3){
        fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
      }
      quadro = CamadaFisicaReceptoraDecodificacaoManchester(fluxoBrutoDeBits);
      break;
    case 2 : //codificacao manchester diferencial
      if (tipoDeEnquadramento == 3){
        fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
      }
       quadro = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
       break;
    }//fim do switch/case
    //chama proxima camada
    CamadaEnlaceDadosReceptoraDesenquadramento(quadro);
    //CamadaDeAplicacaoReceptora(quadro);
  }//fim do metodo CamadaFisicaTransmissora

  public void CamadaEnlaceDadosReceptoraDesenquadramento (int quadro[]) {
    
    int quadroDesenquadrado[] = new int[0]; //mudar depois

    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        quadroDesenquadrado = quadro;
        break;
    }// fim do switch/case
    CamadaDeAplicacaoReceptora(quadroDesenquadrado);
  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento
    
  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoBinaria.
  * Funcao: metodo para decodificar a mensagem binaria.
  * Parametros: recebe o array de inteiros.
  * Retorno: retorna um array de interios decodificado.
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int fluxoBrutoDeBits []){
    int [] quadro = new int[fluxoBrutoDeBits.length];
    quadro = fluxoBrutoDeBits;
    /*
    int indexQuadro = 0;
    for (int i = 0; i < fluxoBrutoDeBits.length; i++){
      for (int j = 31; j >= 0; j--){
        int bit = (fluxoBrutoDeBits[i] >> j) & 1;
        quadro[indexQuadro] = quadro[indexQuadro] | (bit << j);
      }
      indexQuadro++;
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
    int [] quadro = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFLuxo = 0;
    int deslocaFluxo = 31;
    for (int i = 0; i < fluxoBrutoDeBits.length*32; i++){ // qtdBitsTotais
        int posAnterior = deslocaFluxo;
        int posSucessor = deslocaFluxo-1;
        int bitAnterior = (fluxoBrutoDeBits[indexFLuxo] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[indexFLuxo]>> posSucessor) & 1;
        if (bitAnterior == 1 && bitSucessor == 0){
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
        }
        else if (bitAnterior == 0 && bitSucessor == 1){
          quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro); 
          deslocaQuadro--;               
        }
        else if (bitAnterior == 1 && bitSucessor == 1){ //igora o par 11 caso o enquadramento seja violacao de camada
          i++;
          /*
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
          if (deslocaQuadro < 0){
            deslocaQuadro = 31;
            indexQuadro++;
          }
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro); 
          */               
        }
        if (deslocaQuadro < 0){
          deslocaQuadro = 31;
          indexQuadro++;
        }
        deslocaFluxo-=2;
        if (deslocaFluxo < 0){
          deslocaFluxo = 31;
          indexFLuxo++;
        }
        if (indexFLuxo >= fluxoBrutoDeBits.length || indexQuadro >= quadro.length){
          break;
        }
      
    }
    /*
    System.out.println("Esse e o quadro decodificado Mancherster");
    for (int i = 0; i < quadro.length; i++){
      System.out.println(String.format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
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
    //o problema acontece quando ele le uma sequencia de 1111 1 que seria a flag e o proximo bit
    int [] quadro = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFluxo = 0;
    int deslocaFluxo = 29;
    int primeiroBit = (fluxoBrutoDeBits[0] >> 31) & 1;
    int segundoBit = (fluxoBrutoDeBits[0] >> 30) & 1;
    if (tipoDeEnquadramento == 3) {
      fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
    }
    for (int i = 0; i < fluxoBrutoDeBits.length*32; i++){ // qtdBitsTotais
      if (i == 0){ //se esta na primeira iteracao
        if (primeiroBit == 1 && segundoBit == 0){
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
        }
        else if (primeiroBit == 0 && segundoBit == 1){
          quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro);
          deslocaQuadro--;
        }
        else if (primeiroBit == 1 && segundoBit == 1){
          i++;
          /*
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
          if (((fluxoBrutoDeBits[0] >> 29) & 1)==1 & ((fluxoBrutoDeBits[0] >> 28) & 1) == 0){
            quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);        
          }
          else if (((fluxoBrutoDeBits[0] >> 29) & 1)==0 & ((fluxoBrutoDeBits[0] >> 28) & 1) == 1){
            quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro);   
          }
          */
        }
      }
      else{
        int posAnterior = deslocaFluxo+1;
        int posSucessor = deslocaFluxo;
        int proximo = deslocaFluxo-1;
        int bitAnterior = (fluxoBrutoDeBits[indexFluxo] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[indexFluxo]>> posSucessor) & 1;
        int bitProximo = (fluxoBrutoDeBits[indexFluxo]>> proximo) & 1;
        if (bitSucessor == 1 && bitProximo == 1){ //verifica se achou o par 11 (serve para a violacao de camada)
          i++;
          /*
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
          if (deslocaQuadro < 0){
            deslocaQuadro = 31;
            indexQuadro++;
          }
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          */
        }
        else if (bitSucessor == 0 && bitProximo == 0){
          //deslocaQuadro--;
        }
        else if ((bitAnterior == bitSucessor)){
          quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
        }
        else if (bitAnterior != bitSucessor){
          quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro);  
          deslocaQuadro--;              
        }
        if (deslocaQuadro < 0){
          deslocaQuadro = 31;
          indexQuadro++;
        }
        deslocaFluxo-=2;
        if (deslocaFluxo <= 0){
          posAnterior = 0;
          bitAnterior = (fluxoBrutoDeBits[indexFluxo] >> posAnterior) & 1;
          indexFluxo++;
          if (indexFluxo >= fluxoBrutoDeBits.length){
            break;
          }
          posSucessor = 31;
          bitSucessor = (fluxoBrutoDeBits[indexFluxo]>> posSucessor) & 1; 
          proximo = 30;
          bitProximo = (fluxoBrutoDeBits[indexFluxo]>> proximo) & 1;
          /*if (bitSucessor == 1 && bitProximo == 1){ //verifica se achou o par 11 (serve para a violacao de camada)
            i++;
            /*
            quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
            deslocaQuadro--;
            if (deslocaQuadro < 0){
              deslocaQuadro = 31;
              indexQuadro++;
            }
            quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
            
          }
          else*/if (bitSucessor == 0 && bitProximo == 0){
            //deslocaQuadro--;
          }
          else if ((bitAnterior == bitSucessor)){
            quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
            deslocaQuadro--;
          }
          else if (bitAnterior != bitSucessor){
            quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro); 
            deslocaQuadro--;               
          }
          if (deslocaQuadro < 0){
            deslocaQuadro = 31;
            indexQuadro++;
          }
          deslocaFluxo = 29;
        }
        if (indexFluxo >= fluxoBrutoDeBits.length | indexQuadro >= quadro.length){
          break;
        }
      }
    }
    /*
    System.out.println("Esse e o quadro descodificado Mancherster Diferencial");
    for (int i = 0; i < quadro.length; i++){
      System.out.println(String.format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    } 
    */ 
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres.
  * Funcao: metodo para desenquadrar a mensagem do tipo Contagem de Caracteres.
  * Parametros: recebe o array de inteiros.
  * Retorno: retornar o array desenquadrado.
  *************************************************************** */
  public int [] CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres (int[] quadroEnquadrado) {
    int [] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;    
    String contador4 = "00000100"; //represeta o 4 em binario, dessa forma, estou dizendo que vai contar de 4 em 4
    String contador3 = "00000011"; //representa o 3
    String contador2 = "00000010"; //representa o 2
    String aux = "";
    int qtdProximaIteracao = 0;
    for (int i = 0; i < quadroEnquadrado.length*32; i++){  // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (bit == 1){
        aux+= '1';
      }
      else {
        aux+= '0';
      }
      if (aux.equals(contador4)){
        qtdProximaIteracao = 24;
      }
      else if (aux.equals(contador3)){
        qtdProximaIteracao = 16;
      }
      else if (aux.equals(contador2)){
        qtdProximaIteracao = 8;
      }
      if (aux.length()==8){ //verifica se ja leu a informacao de controle completa
        for (int j = 0; j < qtdProximaIteracao; j++){
          bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
          if (bit == 1){
            quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          }
          deslocaQuadro--;
          if (deslocaQuadro < 0){
            deslocaQuadro = 31;
            indexQuadro++;
          }
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado<0){
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
          if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
            break;
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0){
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
        break;
      }
    }

    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes.
  * Funcao: metodo para desenquadrar a mensagem do tipo Insercao de Bytes.
  * Parametros: recebe o array de inteiros.
  * Retorno: retornar o array desenquadrado.
  *************************************************************** */
  public int [] CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes (int[] quadroEnquadrado) {
    int [] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;   
    String flag = "00111111";
    String esc = "01000000";
    String aux = "";
    for (int i = 0; i < quadroEnquadrado.length*32; i++){ //  qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado<0){
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (bit == 1){
        aux+= '1';
      }
      else {
        aux+= '0';
      }
      if (aux.length()==8){ //verifica se ja leu a informacao de controle completa
        if (aux.equals(esc)){ //insere os proximos 8 bits em quadro  
          for (int j = 0; j < 8; j++){
            bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
            if (bit == 1){
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0){
              deslocaQuadro = 31;
              indexQuadro++;
            }
            deslocaQuadoEnquadrado--;
            if (deslocaQuadoEnquadrado < 0){
              deslocaQuadoEnquadrado = 31;
              indexQuadroEnquadrado++;
            }
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
              break;
            }
          }
        }
        else if (aux.equals(flag)){ //ignora
        }
        else if (aux.equals("00000000")){
          break;
        }
        else {
          for (int j = 0; j < 8; j++){
            if (aux.charAt(j) == '1'){
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0){
              deslocaQuadro = 31;
              indexQuadro++;
            }/*
            deslocaQuadoEnquadrado--;
            if (deslocaQuadoEnquadrado<0){
              deslocaQuadoEnquadrado = 31;
              indexQuadroEnquadrado++;
            }*/
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
              break;
            }
          }          
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0){
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
        break;
      }
    }

    return quadro;
  }
  
  /* ***************************************************************
  * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits.
  * Funcao: metodo para desenquadrar a mensagem do tipo Insercao de Bits.
  * Parametros: recebe o array de inteiros.
  * Retorno: retornar o array desenquadrado.
  *************************************************************** */
  public int [] CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits (int[] quadroEnquadrado) {
    int [] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int qtdBitsUm = 0;  
    String flag = "01111110";
    String aux = "";
    for (int i = 0; i < quadroEnquadrado.length*32; i++){ // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado<0){
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (bit == 1){
        aux+= '1';
        qtdBitsUm++;
        bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
        if (qtdBitsUm == 5 && bit == 0){
          qtdBitsUm = 0;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado<0){
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
      }
      else {
        aux+= '0';
        qtdBitsUm = 0;
      }
      if (aux.length()==8){ //verifica se ja leu a informacao de controle completa
        if (aux.equals(flag)){
          //ignora
        }
        else if (aux.equals("00000000")){
          break;
        }
        else {
          for (int j = 0; j < 8; j++){
            if (aux.charAt(j) == '1'){
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0){
              deslocaQuadro = 31;
              indexQuadro++;
            }/*
            deslocaQuadoEnquadrado--;
            if (deslocaQuadoEnquadrado<0){
              deslocaQuadoEnquadrado = 31;
              indexQuadroEnquadrado++;
            }*/
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
              break;
            }
          }          
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0){
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length){
        break;
      }
    }

    return quadro;
  }

  /* ***************************************************************
  * Metodo: DecodificacaoViolacaoCamadaFisica.
  * Funcao: metodo para desenquadrar a mensagem do tipo Violacao de Camada.
  * Parametros: recebe o array de inteiros.
  * Retorno: retornar o array desenquadrado.
  *************************************************************** */
  public int[] DecodificacaoViolacaoCamadaFisica(int[] fluxoBrutoDeBits) {    
    int [] quadroDesenquadrado = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFLuxo = 0;
    int deslocaFluxo = 31;
    for (int i = 0; i < fluxoBrutoDeBits.length*32; i++){ // qtdBitsTotais
        int posAnterior = deslocaFluxo;
        int posSucessor = deslocaFluxo-1;
        int bitAnterior = (fluxoBrutoDeBits[indexFLuxo] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[indexFLuxo]>> posSucessor) & 1;
        if (bitAnterior == 1 && bitSucessor == 0){
          quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
          quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (0<<deslocaQuadro);
          deslocaQuadro--;
        }
        else if (bitAnterior == 0 && bitSucessor == 1){
          quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (0<<deslocaQuadro); 
          deslocaQuadro--;
          quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (1<<deslocaQuadro);
          deslocaQuadro--;
        }
        else if (bitAnterior == 1 && bitSucessor == 1){ //igora o par 11 caso o enquadramento seja violacao de camada
          i++;              
        }
        else {
          break;
        }
        if (deslocaQuadro < 0){
          deslocaQuadro = 31;
          indexQuadro++;
        }
        deslocaFluxo-=2;
        if (deslocaFluxo < 0){
          deslocaFluxo = 31;
          indexFLuxo++;
        }
        if (indexFLuxo >= fluxoBrutoDeBits.length || indexQuadro >= quadroDesenquadrado.length){
          break;
        }
    }
    /*
    System.out.println("Esse e o quadroDesenquadrado Violacao de camada");
    for (int i = 0; i < quadroDesenquadrado.length; i++){
      System.out.println(String.format("%32s", Integer.toBinaryString(quadroDesenquadrado[i])).replace(' ', '0'));
    }
    */
    return quadroDesenquadrado;
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
