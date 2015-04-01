package org.groolot.mallarme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;


public class MainActivity extends Activity implements OnTouchListener {

    final String constAddrServ = "0.0.0.0";
    final Integer constPortServ = 0;
    TextView textView;
    ImageView imgView;
    ImageButton blueCircle, pinkCircle;
    RelativeLayout rl;
    String addrServ;
    Integer portServ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getConnectedStatus(this) == false) {
            Toast.makeText(this, "Activez votre connexion internet", Toast.LENGTH_SHORT).show();
        }

        blueCircle = (ImageButton) findViewById(R.id.annabelle);
        blueCircle.setOnTouchListener(this);

        pinkCircle = (ImageButton) findViewById(R.id.florence);
        pinkCircle.setOnTouchListener(this);

        rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        if (prefs.getString("serveur", "NULL") != "") {
            addrServ = prefs.getString("serveur", "NULL");
        } else {
            addrServ = constAddrServ;
        }
        if (!addrServ.matches("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b")) {
            startActivity(new Intent(this, SettingsActivity.class));
            Toast.makeText(this, "Adresse IP incorrecte !", Toast.LENGTH_SHORT).show();
        }
        try {
            portServ = Integer.parseInt(prefs.getString("port", ""));
        } catch (Exception e) {
            portServ = constPortServ;
        }
        if (portServ <= 0 || portServ >= 65536) {
            startActivity(new Intent(this, SettingsActivity.class));
            Toast.makeText(this, "Port incorrect !", Toast.LENGTH_SHORT).show();
        }

        Log.i("SETTINGS", "Port : " + portServ.toString());
        Log.i("SETTINGS", "Addr : " + addrServ);
        boolean checkBox = prefs.getBoolean("checkBox", false);

        if (checkBox == true) {
            rl.setBackgroundResource(R.drawable.grille);
        } else {
            rl.setBackgroundResource(0);
        }
    }

    private boolean getConnectedStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    public int limitScreen(int size, float position) {
        int new_position = (int) position - size / 2;
        return new_position;
    }

    public void OSCSend(final String addr, final int port, final OSCMessage msg) {
        Thread temp = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OSCPortOut sender = new OSCPortOut(InetAddress.getByName(addr), port);
                    sender.send(msg);
                } catch (SocketException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        temp.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((v.getTop() + v.getHeight()) > rl.getHeight()) {
                v.offsetTopAndBottom(-v.getHeight());
            } else if (v.getTop() - v.getHeight() < 0) {
                v.offsetTopAndBottom(+v.getHeight());
            } else if (v.getRight() + v.getWidth() > rl.getWidth()) {
                v.offsetLeftAndRight(-v.getHeight());
            } else if (v.getRight() - v.getWidth() < 0) {
                v.offsetLeftAndRight(+v.getHeight());
            }
        } else {
            v.offsetTopAndBottom(limitScreen(v.getHeight(), event.getY()));
        }
        v.offsetLeftAndRight(limitScreen(v.getWidth(), event.getX()));

        float X = (float) v.getLeft() / (float) rl.getWidth() + (float) v.getWidth() / 2 / (float) rl.getWidth();
        float Y = (float) v.getTop() / (float) rl.getHeight() + (float) v.getHeight() / 2 / (float) rl.getHeight();

        Vector send = new Vector(2);
        send.add(X);
        send.add(Y);

        OSCMessage msg = new OSCMessage("/mallarme/" + v.getResources().getResourceEntryName(v.getId()) + "/position", send);
        OSCSend(addrServ, portServ, msg);

        return false;
    }
}