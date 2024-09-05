/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 31/08/2024
* Ultima alteracao.: 05/09/2024
* Nome.............: Roteomento por Inundacao
* Funcao...........: Simular o algoritmo de roteamento por inundacao da rede
*************************************************************** */

import java.util.concurrent.Semaphore;

public class Pacote {
    private Semaphore semaforo;
    private int ttl;
    private Roteador destino;

    public Pacote(Roteador destino) {
        this.destino = destino;
        this.ttl = 15;
        this.semaforo = new Semaphore(1); // Semáforo para controle de acesso
    }

    /*
    * ***************************************************************
    * Metodo: setTTL
    * Funcao: Define o valor do TTL do pacote.
    * Parametros: ttl - o novo valor do TTL.
    * Retorno: void.
    * ***************************************************************
    */
    public void setTTL(int ttl) {
        this.ttl = ttl;
    }

    /*
    * ***************************************************************
    * Metodo: getDestino
    * Funcao: Obtém o roteador destino do pacote.
    * Parametros: nenhum.
    * Retorno: o roteador destino do pacote.
    * ***************************************************************
    */
    public Roteador getDestino () {
        return destino;
    }

    /*
    * ***************************************************************
    * Metodo: getTTL
    * Funcao: Obtém o valor atual do TTL do pacote.
    * Parametros: nenhum.
    * Retorno: o valor do TTL.
    * ***************************************************************
    */
    public int getTTL() {
        return ttl;
    }

    /*
    * ***************************************************************
    * Metodo: decrementaTTL
    * Funcao: Decrementa o valor do TTL do pacote de forma segura com controle de acesso usando semáforo.
    * Parametros: nenhum.
    * Retorno: void.
    * ***************************************************************
    */
    public void decrementaTTL() throws InterruptedException {
        semaforo.acquire();
        this.ttl--;
        semaforo.release();
    }
}
