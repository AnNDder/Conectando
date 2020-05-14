package pakage.de.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity {

    private TextView textView;
    Button talkButton;
    ImageButton abrirtelefono;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        abrirtelefono = findViewById(R.id.IMGopen);
        textView =  findViewById(R.id.text);
        talkButton =  findViewById(R.id.talkClick);




        /**creamos un OnClickListener**/
        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onClickMessage = "I just sent the handheld a message " + sentMessageNumber++;

        /**Usa el mismo path**/

                String datapath = "/my_path";
                new SendMessage(datapath, onClickMessage).start();

            }
        });
        /**Regístrese para recibir transmisiones locales, que crearemos en el siguiente paso**/

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);

    }
    /**ABRIR EN TELEFONO ANIMACION**/
    /////////////////////////////////////////////
    public void abrir(View view) {
        Intent intent = new Intent(this, OpenOnPhoneAnimationActivity.class);
        startActivity(intent);

    }
    ///////////////////////////////////////////

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        /**Mostrar lo siguiente cuando se recibe un nuevo mensaje**/

            String onMessageReceived = "Acabo de recibir un mensaje de un Wearable " + receivedMessageNumber++;
            textView.setText(onMessageReceived);

        }
    }

    class SendMessage extends Thread {
        String path;
        String message;

        /**Constructor para enviar información a la capa de datos**/

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

        /**Recuperar los dispositivos conectados.**/

            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

        /**Bloquear una tarea y obtener el resultado sincrónicamente**/

                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {

        /**Enviar el mensaje**/

                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

                        Integer result = Tasks.await(sendMessageTask);

        /**Manejar los errores**/

                    } catch (ExecutionException exception) {

        /**Para hacer**/

                    } catch (InterruptedException exception) {

        /**Para hacer**/

                    }

                }

            } catch (ExecutionException exception) {

                /**Para hacer**/

            } catch (InterruptedException exception) {

            /**Para hacer**/

            }
        }
    }
}
