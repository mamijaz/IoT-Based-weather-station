package ijaz.iot;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


//imports for the data handling begins
import java.io.*;
import java.net.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//data handling begins ends

public class webData extends AppCompatActivity {

    private final int chanelID=97668;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_data);
        Update();
    }

    public void buttonClickUpdateData(View v) {
        Update();
    }

    public void buttonClickBack(View v){
        finish();
    }

    public void Update(){
        if(connectStatus()) {
            try {
                URL url = new URL("https://api.thingspeak.com/channels/" + chanelID + "/feeds/last.xml");
                new ThingspeakBacground().execute(url);
            } catch (Exception ex) {
                Log.d("exception", ex.getLocalizedMessage());
            }
        }
        else{
            Toast.makeText(webData.this, "No Internet Acces", Toast.LENGTH_SHORT).show();
        }

    }

    private class ThingspeakBacground extends AsyncTask<URL, Void, String> {
        protected String doInBackground(URL... urls) {
            int count = urls.length;
            for (int i = 0; i < count; i++) {
                try {
                    StringBuilder result = new StringBuilder();
                    HttpURLConnection conn = (HttpURLConnection) urls[i].openConnection();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    rd.close();
                    return result.toString();
                } catch (Exception ex) {
                    Log.d("exception",ex.getLocalizedMessage());
                }
                return null;
            }
            return null;
        }

        protected void onPostExecute(String result) {
            Element element= convertToXML(result).getDocumentElement();
            updateFields(element);
        }

        protected Document convertToXML(String xml) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(xml)));
                return document;
            } catch (Exception ex) {
                Log.d("exception", ex.getLocalizedMessage());
            }
            return null;
        }

        protected String getItem(String tagName, Element element) {
            NodeList list = element.getElementsByTagName(tagName);
            if (list != null && list.getLength() > 0) {
                NodeList subList = list.item(0).getChildNodes();
                if (subList != null && subList.getLength() > 0) {
                    return subList.item(0).getNodeValue();
                }
            }
            return null;
        }

        private void updateFields(Element element){
            TextView textViewt = (TextView) findViewById(R.id.textViewDisTemp);
            textViewt.setText(getItem("field1", element)+"\u00b0"+"C");
            TextView textViewh = (TextView) findViewById(R.id.textViewDisHum);
            textViewh.setText(getItem("field2", element)+"%");
            TextView textViewp = (TextView) findViewById(R.id.textViewDisPre);
            textViewp.setText(getItem("field3", element)+" Bar");
            TextView textViewpr = (TextView) findViewById(R.id.textViewDisRain);
            textViewpr.setText(getItem("field4", element)+"%");
        }
    }

    private boolean connectStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }
}
