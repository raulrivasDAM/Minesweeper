package es.riberadeltajo.buscaminas_raul_rivas_jeronimo;
import java.util.Random;

public class Tablero {
    private int filas;
    private int columnas;
    private int[][] casillas;

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.casillas = new int[filas][columnas];
    }

    public Tablero(int filas, int columnas, int[][]casillas) {
        this.filas = filas;
        this.columnas = columnas;
        this.casillas = new int[filas][columnas];
    }
    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getCasillas(int fila, int columna) {
        return casillas[fila][columna];
    }

    public int [][] configBombas(int numMinas) {
        // Llenar todas las casillas con 0 (sin minas)
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                casillas[i][j] = 0;
            }
        }

        // Colocar minas aleatoriaas
        Random r = new Random();
        int minas = 0;
        while (minas < numMinas) {
            int fila = r.nextInt(filas);
            int columna = r.nextInt(columnas);

            // Si la casilla no tiene una mina, coloca una
            if (casillas[fila][columna] != -1) {
                casillas[fila][columna] = -1; //-1 representa una mina
                minas++;
            }
        }

        //Voy a calcular el número de minas adyacentes
        //Recorro la matriz, hare 4 for ya que quiero recorrer la posicion anterior y siguiente a la fila y a la columna
        //para pillar las 8 posiciones que tocan a la mina
       for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (casillas[i][j] != -1) {
                    int cont = 0;
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            int nuevaFila = i + k;
                            int nuevaColumna = j + l;
                            //Para comprobar que no se salga de los limites del tablero
                            if (nuevaFila >= 0 && nuevaFila < filas) {
                                if(nuevaColumna >= 0 && nuevaColumna < columnas){
                                    if(casillas [nuevaFila][nuevaColumna] == -1){
                                        cont++;
                                    }
                                }
                            }
                        }//Cierre del for anidado(4)
                    }//Cierre del for anidado(3)
                    casillas[i][j] = cont;
                }
            }//Cierre del segundo fot
        }//Cierre del primer for

        //Este metodo lo pense primero pero me daba un error de que se salia de los limites
        /*
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if( casillas[i][j] ==-1){
                    casillas[i][j-1]+=1;
                    casillas[i][j+1]+=1;
                    casillas[i-1][j]+=1;
                    casillas[i+1][j]+=1;

                }
            }
        }
        */
        //devuelvo la matriz para usarla en el main
        return casillas;
    }// cerrar configBombas
}
