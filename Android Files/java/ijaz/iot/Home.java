package ijaz.iot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    private String array_spinner[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        array_spinner=new String[5];
        array_spinner[0]="My Location";
        array_spinner[1]="Narammala";
        array_spinner[2]="Kandy";
        array_spinner[3]="Kurunagala";
        array_spinner[4]="Colombo";
        Spinner s = (Spinner) findViewById(R.id.spinnerLocations);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter(adapter);
    }

    public void buttonClickGetData(View v){
        if(connectStatus()) {
            Intent intent=new Intent(v.getContext(),webData.class);
            startActivityForResult(intent, 0);
        }
        else{
            Toast.makeText(Home.this, "No Internet Acces", Toast.LENGTH_SHORT).show();
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
