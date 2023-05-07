package com.example.iot_project;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.nio.charset.Charset;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtAirHumi, txtSoilHumi, txtLight;
    LabeledSwitch btnLed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemperature);
        txtAirHumi = findViewById(R.id.txtAirHumidity);
        txtSoilHumi = findViewById(R.id.txtSoilHumidity);
        txtLight = findViewById(R.id.txtLight);
        btnLed = findViewById(R.id.btnLed);
        btnLed.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("minhphan811/feeds/button1", "1");
                }else{
                    sendDataMQTT("minhphan811/feeds/button1", "0");
                }
            }
        });

        startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e) {

        }
    }


    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                if(topic.contains("sensor1")){
                    txtTemp.setText(message.toString() + "°C");
                }
                else if(topic.contains("sensor2")){
                    txtAirHumi.setText(message.toString() + "%");
                }
                else if(topic.contains("sensor3")){
                    txtSoilHumi.setText(message.toString() + "%");
                }
                else if(topic.contains("sensor4")){
                    txtLight.setText(message.toString() + "lux");
                }
                else if(topic.contains("button1")){
                    if(message.toString().equals("1")){
                        btnLed.setOn(true);
                    }else{
                        btnLed.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}