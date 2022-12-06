package kz.kairulla.lab7_kre17;

import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.SNIServerName;

import javax.net.ssl.SSLContext;

/* 17) Найти виды игрушек в интернет-магазине "Wildberries": https://content-suppliers.wildberries.ru/ns/characteristics-configurator-api/content-configurator/api/v1/config/get/object/list?pattern=игр&lang=ru */

public class MyClass extends AppCompatActivity {

//    String url = "https://api.coindesk.com/v1/bpi/currentprice.json"; // Адрес получения JSON - данных
//    String url = "http://api.openweathermap.org/data/2.5/weather?units=metric&q=Pavlodar,%20KZ&lang=ru&appid=52c336c8ceb8a6d8e4ee482985660291"; // Адрес получения JSON - данных
    String url = "https://content-suppliers.wildberries.ru/ns/characteristics-configurator-api/content-configurator/api/v1/config/get/object/list?pattern=%D0%B8%D0%B3%D1%80&lang=ru"; // Адрес получения JSON - данных

    private TextView textView; // Компонент для отображения данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        // ЭТОТ КУСОК КОДА НЕОБХОДИМ ДЛЯ ТОГО, ЧТОБЫ ОТКРЫВАТЬ САЙТЫ С HTTPS!
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
/* ---------------------------------------------------------------------------------------------- */

        // Разрешаем запуск в общем потоке выполнеия длительных задач (например, чтение с сети)
        // ЭТО ТОЛЬКО ДЛЯ ПРИМЕРА, ПО-НОРМАЛЬНОМУ НАДО ВСЕ В ОТДЕЛЬНЫХ ПОТОКАХ
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textView = findViewById(R.id.textView);

        onClick(null); // Нажмем на кнопку "Обновить"
    }
















    // Кнопка "Обновить"
    public void onClick(View view) {
        textView.setText(R.string.net_dannikh);
        String json = getHTMLData(url); // получаем содержимое json
        if (json != null) {
            JSONObject _root = null;
            try {
                _root = new JSONObject(json); // отсортированный по алфавиту json, содержимое одинаковое, просто отсортировали
                Log.i("_root", _root.toString());
                JSONObject data = _root.getJSONObject("data");
                Iterator<String> dataKeys = data.keys();
                textView.setText("");
                for (Iterator<String> it = dataKeys; it.hasNext(); ) {
                    String s = it.next();
                    Log.i("dataKeys", s);
                    textView.append(s);
                    textView.append("\n");
                };


//                JSONObject data = data.names(data);

//                JSONObject game_tech = data.getJSONObject("Игровая техника");


            } catch (Exception e) {
                textView.setText(R.string.oshibka);
            }
        }
    }













    // Метод чтения данных с сети по протоколу HTTP
    public static String getHTMLData(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection(); // com.android.okhttp.internal.http.HttpsURLConnectionImpl$HttpUrlConnectionDelegate:https://api.coindesk.com/v1/bpi/currentprice.json
//            Log.i("conn", conn.toString());
            int response = conn.getResponseCode(); // 200, ответ от сервера
//            Log.i("response", String.valueOf(response));
            if (response == HttpURLConnection.HTTP_OK) { // HTTP_OK = 200, это ответ от сервера
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
//                    int i = 0;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line); /* построчно подготавливается неотсортированный json, т.е. такой же как на сервере
                                            в цикле была только одна итерация, значит json считывается целиком, либо там всё в одной строке */
                        Log.i("line", line);
//                        ++i;
//                        Log.i("i", String.valueOf(i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data.toString();
            } else {
                return null;
            }
        } catch (Exception ignored) {
        } finally {
            conn.disconnect();
        }
        return null;
    }
}
