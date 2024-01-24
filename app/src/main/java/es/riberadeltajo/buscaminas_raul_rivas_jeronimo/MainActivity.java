package es.riberadeltajo.buscaminas_raul_rivas_jeronimo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private GridLayout g;
    private int filas, columnas, numMinas, contMinas;
    private int ib;
    private ConstraintLayout c;
    private Tablero t;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = MediaPlayer.create(this, R.raw.submarina);
        g = findViewById(R.id.grid);
        c = findViewById(R.id.layOut);
        c.post(new Runnable() {
            @Override
            public void run() {
                //Para que se inicie por defecto a 8x8
                filas = 8;
                columnas = 8;
                numMinas = 10;
                t = new Tablero(filas, columnas);
                t.configBombas(numMinas);
                ib=R.drawable.bomba;
                configJuego(numMinas,ib);
                iniciar(filas, columnas, numMinas, ib);
            }
        });
    }

    private void iniciar(int filas, int columnas, int numMinas, int ib) {
        // Sin esto, se sobrepone una vista encima de la otra
        g.removeAllViews(); // Elimina las vistas anteriores
        // Configuramos la cuadrícula
        g.setRowCount(filas);
        g.setColumnCount(columnas);
        t.configBombas(numMinas);
        configJuego(numMinas,ib);
    }

    private void configJuego(int numMinas,  int ib) {
        // Mover el cálculo del ancho y alto de los botones aquí
        int bWidth = c.getWidth() / t.getColumnas();
        int bHeight = c.getHeight() / t.getFilas();
        Random r = new Random();

        // Elimina las vistas anteriores
        g.removeAllViews();

        for (int i = 0; i < t.getFilas(); i++) {
            for (int j = 0; j < t.getColumnas(); j++) {
                int filaActual = i;
                int columnaActual = j;

                if (t.getCasillas(filaActual, columnaActual) == -1) {
                    ImageButton minaButton = new ImageButton(getApplicationContext());
                    minaButton.setBackgroundResource(R.drawable.colorborder);
                    minaButton.setLayoutParams(new ViewGroup.LayoutParams(bWidth, bHeight));
                    g.addView(minaButton);

                    minaButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            minaButton.setBackgroundResource(ib);
                            Toast.makeText(MainActivity.this, "Has tocado una mina DERROTA", Toast.LENGTH_SHORT).show();
                            mediaPlayer.start();
                            //He buscado para poner un Thread sleep y me ha salido Handler como mejor opcion para que espere la app 2 segundos
                            //antes de reiniciar y asi solucionaba el problema de que no me pintaba el imageButton y llamo a mi metodo de reiniciar
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    reiniciar(t.getFilas(), t.getColumnas(), numMinas, ib);
                                }
                            }, 2000); // Pausa durante 2 segundos

                        }
                    });
                    contMinas = numMinas;
                    minaButton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            minaButton.setBackgroundResource(R.drawable.bandera);
                            contMinas--;
                            if (contMinas == 0) {
                                Toast.makeText(MainActivity.this, "VICTORIA", Toast.LENGTH_SHORT).show();
                                // Cuando ganas, se ponen las minas en verde
                                c.setBackgroundColor(Color.GREEN);
                            }
                            return true;
                        }
                    });
                } else {
                    Button b = new Button(this);
                    b.setText("");  // Inicialmente, no muestra ningún contenido en el botón
                    b.setBackgroundResource(R.drawable.colorborder);
                    b.setLayoutParams(new ViewGroup.LayoutParams(bWidth, bHeight));
                    g.addView(b);

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int valorCasilla = t.getCasillas(filaActual, columnaActual);
                            b.setText(String.valueOf(valorCasilla));

                            if (valorCasilla > 0) {
                                // Mostrar el valor en el botón
                                b.setText(String.valueOf(valorCasilla));
                            } else {
                                b.setText("");
                                b.setBackgroundColor(Color.YELLOW);

                            }
                        }
                    });

                    b.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Toast.makeText(MainActivity.this, "Eso no era una mina", Toast.LENGTH_SHORT).show();

                            mediaPlayer.start();
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    reiniciar(t.getFilas(), t.getColumnas(), numMinas, ib);
                                }
                            }, 2000); // Pausa durante 2 segundos
                            return true;
                        }
                    });
                }
            }
        }
    }

    public void reiniciar(int filas, int columnas, int numMinas, int ib) {
        t = new Tablero(filas, columnas);
        t.configBombas(numMinas);
        ib=R.drawable.bomba;
        configJuego(numMinas, ib);
        iniciar(filas, columnas, numMinas, ib);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buscaminas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.instrucciones) {
            mostrarInstrucciones();
        } else if (item.getItemId() == R.id.dificultad) {
            mostrarDificultad();
        } else if (item.getItemId() == R.id.reiniciar) {
            reiniciar(t.getFilas(), t.getColumnas(), numMinas, ib);
        } else if (item.getItemId() == R.id.tipoBomba) {
            mostrarTipoBomba();
        }
        t = new Tablero(filas, columnas);
        configJuego(numMinas, ib);
        iniciar(filas, columnas, numMinas, ib);
        return super.onOptionsItemSelected(item);
    }
    private void mostrarTipoBomba() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogLayout = inflater.inflate(R.layout.spinner, null);

        Spinner spinner = dialogLayout.findViewById(R.id.spinner);

        Mina[] tipoBomba = new Mina[6];
        tipoBomba[0] = new Mina("Mina clásica", R.drawable.bomba);
        tipoBomba[1] = new Mina("Mina Bomber", R.drawable.bomber);
        tipoBomba[2] = new Mina("Dinamita", R.drawable.dinamita);
        tipoBomba[3] = new Mina("Granada", R.drawable.granada);
        tipoBomba[4] = new Mina("Mina submarina", R.drawable.minasubmarina);
        tipoBomba[5] = new Mina("Coctel Molotov", R.drawable.coctel);

        spinner.setAdapter(new MiAdaptadorBombas(this, R.layout.fila_spinner, tipoBomba));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Mina minaSeleccionada = tipoBomba[position];
                String nombre = minaSeleccionada.getDescripcion();
                int imagenSeleccionada = minaSeleccionada.getImagen();
                ib=imagenSeleccionada;
                // Después de cambiar la imagen, actualizo el juego
                configJuego(numMinas, ib);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarInstrucciones() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Instrucciones");
        alerta.setMessage("Cuando pulsas una casilla, sale un número que identifica cuántas minas hay alrededor.Ten cuidado porque si pulsas en una casilla que tenga una mina escondida perderás. Si crees o tienes la certeza de que hay mina, haz un click largo sobre la casilla para señalarla.No hagas un click largo en una casilla donde no hay una mina porque perderás. Ganas una vez hayas encontrado todas lasa minas" );

        //Vamos a ponerle un ok al Dialog para que se cierre al hacerle click
        alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cierra el dialogo cuando se hagae clic en ok
                dialog.dismiss();
            }
        });
        alerta.show();
    }
    private void mostrarDificultad() {
        // Crear un diálogo personalizado para mostrar las opciones de dificultad
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.radio_group);

        // Obtener las vistas de los radio buttons
        RadioButton rbPrincipiante = dialog.findViewById(R.id.principiante);
        RadioButton rbAmateur = dialog.findViewById(R.id.amateur);
        RadioButton rbAvanzado = dialog.findViewById(R.id.avanzado);

        // Agrego 3 listener dependiendo del boton que seleccione
        rbPrincipiante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filas = 8;
                columnas = 8;
                numMinas = 10;
                t = new Tablero(filas,columnas);
                configJuego(numMinas, ib);
                iniciar(filas,columnas,numMinas,ib);  // Llama a reiniciar con la nueva configuración
                dialog.dismiss();  // Cierra el diálogo
            }
        });

        rbAmateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filas = 12;
                columnas = 12;
                numMinas = 30;
                t = new Tablero(filas,columnas);
                configJuego(numMinas, ib);
                iniciar(filas,columnas,numMinas,ib);  // Llama a reiniciar con la nueva configuración
                dialog.dismiss();  // Cierra el diálogo
            }
        });

        rbAvanzado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filas = 16;
                columnas = 16;
                numMinas = 60;
                t = new Tablero(filas,columnas);
                configJuego(numMinas, ib);
                iniciar(filas,columnas,numMinas,ib);  // Llama a iniciar con la nueva configuración
                dialog.dismiss();  // esto cierra el diálogo
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }


    private class MiAdaptadorBombas extends ArrayAdapter<Mina> {
            Mina [] misMinas;
            //constructor adaptador
            public MiAdaptadorBombas(@NonNull Context context, int resource, @NonNull Mina[] objects) {
                super(context, resource, objects);
                misMinas = objects;
            }

            //truco para flexibilidad de app -->
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return crearFila(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return crearFila(position, convertView, parent);
            }

            //Va a rellenar el layout con los datos que le pasamos
            public View crearFila(int position, View convertView, ViewGroup parent){
                //Para programar el adpatador, crearFila va a ser invocado una vez por cada objeto del array de ciudades

                //1º Inflamos el xml con nuestra vista personalizada
                LayoutInflater miInflador = getLayoutInflater(); //Esto llega del contexto. Ya está creado y lo guardamos en esa variable
                View miFila = miInflador.inflate(R.layout.fila_spinner, parent, false); //Vista que representa todos los objetos de mifila_ciudad

                //2º encontramos referencias a los objetos dec ada una de las filas infladas
                TextView txtTipoBomba =  miFila.findViewById(R.id.txtTipoBomba);
                ImageView imgMina = miFila.findViewById(R.id.imgBomba);

                //3º Rellenar los datos, con el objeto i-ésimo del array de objetos (position)
                txtTipoBomba.setText(misMinas[position].descripcion);
                imgMina.setImageResource(misMinas[position].imagen);

                //retornar la fila "instanciada"
                return miFila;
            }

    }
}

