package pakage.de.activitys;

import android.app.RemoteInput;
import android.content.Intent;
import android.graphics.drawable.Animatable2.AnimationCallback;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.wearable.phone.PhoneDeviceType;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.wear.ambient.AmbientModeSupport;
import androidx.wear.widget.ConfirmationOverlay;

import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.wearable.intent.RemoteIntent;

import java.util.Set;

import pakage.de.activitys.R;

public class OpenOnPhoneAnimationActivity extends FragmentActivity implements
        AmbientModeSupport.AmbientCallbackProvider,
        CapabilityClient.OnCapabilityChangedListener{
    private static final String ABRIR_APP_URI = "CelularActivity";
    private static final String TAG = "OpenOnPhoneAnimationActivity";
    private AnimationCallback mAnimationCallback;
    private AnimatedVectorDrawable mAnimatedVectorDrawablePhone;

    private Node mAndroidPhoneNodeWithApp;

    // Resultado del envío de RemoteIntent al teléfono para abrir la aplicación en play / app store.
    private final ResultReceiver mResultReceiver =new  ResultReceiver(new Handler())
    {
        @Override
                protected void onReceiveResult(int resultCode, Bundle resultData){

                if (resultCode == RemoteIntent.RESULT_OK)
                {
                    new ConfirmationOverlay().showOn(OpenOnPhoneAnimationActivity.this);
                }
                else if (resultCode == RemoteIntent.RESULT_FAILED){
                    new ConfirmationOverlay()
                            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                            .showOn(OpenOnPhoneAnimationActivity.this);
                }
                else{
                    throw new IllegalStateException("Resultado inesperado!" + resultCode);
                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_on_phone_animation);


        mAnimationCallback =
                new AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        super.onAnimationEnd(drawable);

                           OpenAPP();
                        finish();
                    }
                };

        ImageView phoneImage = findViewById(R.id.open_on_phone_animation_image);
        mAnimatedVectorDrawablePhone = (AnimatedVectorDrawable) phoneImage.getDrawable();
        mAnimatedVectorDrawablePhone.registerAnimationCallback(mAnimationCallback);
        mAnimatedVectorDrawablePhone.start();
    }

    private void OpenAPP() {

        int phoneDeviceType = PhoneDeviceType.getPhoneDeviceType(getApplicationContext());
        switch (phoneDeviceType){
            case PhoneDeviceType.DEVICE_TYPE_ANDROID:

                Intent intentAndroid =
                        new Intent(Intent.ACTION_VIEW)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                        .setData(Uri.parse(ABRIR_APP_URI));
                RemoteIntent.startRemoteActivity(
                        getApplicationContext(),
                        intentAndroid,
                        mResultReceiver);
                break;

                case PhoneDeviceType.DEVICE_TYPE_ERROR_UNKNOWN:
                    break;
        }
    }
    private Node pickBestNodeId(Set<Node> nodes)
    {
        Node bestNodeId = null;

        for(Node node : nodes){
            bestNodeId = node;
        }
        return bestNodeId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAnimatedVectorDrawablePhone.unregisterAnimationCallback(mAnimationCallback);
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }
    /**Capacidad_Cambiada**/
    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
        verifyNodeAndUpdateUI();

    }

    private void verifyNodeAndUpdateUI() {
        if(mAndroidPhoneNodeWithApp != null)
        {
            /**Agregue su código para comunicarse con la aplicación del teléfono a través de Wear APIs (MessageApi, DataApi, etc**/
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {}
}
