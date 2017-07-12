package thingslabinternetservicespvtltd.firenotify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
MqttAndroidClient client;
    NotificationCompat.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://www.alpha.primemq.com:8721",
                        clientId);
        builder=new NotificationCompat.Builder(this);
        Button btn=(Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MqttConnectOptions opt=new MqttConnectOptions();
                opt.setUserName("yugandharc9");
                System.out.println("123123123");
                opt.setPassword("Qq1-yugan".toCharArray());
                opt.setCleanSession(true);

                try {
                    IMqttToken token = client.connect(opt);
                    System.out.println("client is "+client.isConnected());
                    System.out.println(token.toString());
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            //Log.d("TAG", "onSuccess");
                            System.out.println("Sucessfully Connected MQTT");
                            IMqttToken tok = null;
                            try {
                                tok = client.subscribe("room/fire",2);
                                client.setCallback(new MqttCallback() {
                                    @Override
                                    public void connectionLost(Throwable cause) {

                                    }

                                    @Override
                                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                                        String str=new String(message.getPayload());
                                        System.out.println(str+" is message");
                                        Integer in=new Integer(str);
                                      if (1==in){
                                          System.out.println("yes its running");
                                            builder.setSmallIcon(R.drawable.if_flame);
                                            builder.setContentTitle("Fire Alert");
                                            builder.setContentText("Fire has been detected on your property. Please evacuate the place immediately." +
                                                    "We are sending help.");
                                            builder.setPriority(2);
                                            builder.setAutoCancel(false);
                                            Notification n = builder.build();
                                            NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            mgr.notify(2154, n);
                                        }
                                    }

                                    @Override
                                    public void deliveryComplete(IMqttDeliveryToken token) {

                                    }
                                });
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            tok.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    System.out.println("subscritption suceess");
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                                }
                            });


                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            //Log.d("TAG", "onFailure");
                            System.out.println("Not Connected MQTT");
                        }
                    });
                } catch (MqttException e) {
                    System.out.println(e.getMessage().toString());
                    e.printStackTrace();
                }
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println(message.getPayload().toString() + " is message");
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }



                });


            }
        });

    }
}
