/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 31/08/2024
* Ultima alteracao.: 05/09/2024
* Nome.............: Roteomento por Inundacao
* Funcao...........: Simular o algoritmo de roteamento por inundacao da rede
*************************************************************** */

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

    /*
    * ***************************************************************
    * Metodo: getDestino
    * Funcao: Obtém o roteador destino da aresta.
    * Parametros: nenhum.
    * Retorno: o roteador destino da aresta.
    * ***************************************************************
    */
    public Roteador getDestino() {
        return destino;
    }

    /*
    * ***************************************************************
    * Metodo: getValor
    * Funcao: Obtém o valor associado à aresta.
    * Parametros: nenhum.
    * Retorno: o valor da aresta.
    * ***************************************************************
    */
    public int getValor() {
        return valor;
    }

    /*
    * ***************************************************************
    * Metodo: getSemaforo
    * Funcao: Obtém o semáforo para controle de acesso à aresta.
    * Parametros: nenhum.
    * Retorno: o semáforo da aresta.
    * ***************************************************************
    */
    public Semaphore getSemaforo() {
        return semaforo;
    }
}
