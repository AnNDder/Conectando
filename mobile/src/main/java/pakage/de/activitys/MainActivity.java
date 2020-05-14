package pakage.de.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.content.BroadcastReceiver;
import android.util.Log;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Wearable;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button talkbutton;
    TextView textview;
    protected Handler myHandler;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        talkbutton = findViewById(R.id.talkButton);
        textview = findViewById(R.id.textView);





        /** Crear un manejador de mensajes **/
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                messageText(stuff.getString("messageText"));
                return true;
            }
        });
        /**Regístrese para recibir transmisiones locales, que crearemos en el siguiente paso**/
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    public void messageText(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            textview.append("\n" + newinfo);
        }
    }
///////////////////////////////////////////////////
    public void abrir(View view) {
        Log.d(LOG_TAG, "Boton presonado");
    }
/////////////////////////////////////////////////////

    /**Defina una clase anidada que amplíe BroadcastReceiver**/

    public class Receiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {

      /**Al recibir cada mensaje del wearable, muestre el siguiente texto**/

            String message = "I just received a message from the wearable " + receivedMessageNumber++;;

            textview.setText(message);

        }
    }

    public void talkClick(View v) {
        String message = "Enviando Mensaje.... ";
        textview.setText(message);

        /**Enviar un mensaje puede bloquear el hilo principal de la interfaz de usuario, así que use un nuevo hilo**/
        new NewThread("/my_path", message).start();

    }
        /**Use un paquete para encapsular nuestro mensaje**/

    public void sendmessage(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);

    }

    class NewThread extends Thread {
        String path;
        String message;

         /**Constructor para enviar información a la capa de datos**/

        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

        /**Recuperar los dispositivos conectados, conocidos como nodos**/

            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =

        /**Enviar el mensaje**/

                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

        /**Bloquee una tarea y obtenga el resultado sincrónicamente**/

                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage("I just sent the wearable a message " + sentMessageNumber++);

                        /**Si la tarea falla, entonces...**/

                    } catch (ExecutionException exception) {

                        /**PARA HACER: Manejar la excepción**/

                    } catch (InterruptedException exception) {

                        /**PARA HACER: Manejar la excepción**/

                    }

                }

            } catch (ExecutionException exception) {

                /**PARA HACER: Manejar la excepción**/

            } catch (InterruptedException exception) {

                /**PARA HACER: Manejar la excepción**/
            }

        }
















    }//FIN DEL ONCREATE
}
