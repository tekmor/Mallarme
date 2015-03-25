package org.groolot.mallarme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;


public class MainActivity extends Activity implements OnTouchListener {

    TextView textView;
    ImageView imgView;
    ImageButton blueCircle, pinkCircle;
    RelativeLayout rl;

    String addrServ = "172.16.100.245";
    Integer portServ = 12345;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blueCircle = (ImageButton) findViewById(R.id.blueCircle);
        blueCircle.setOnTouchListener(this);

        pinkCircle = (ImageButton) findViewById(R.id.pinkCircle);
        pinkCircle.setOnTouchListener(this);

        rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        //addrServ = prefs.getString("serveur", "NULL");
        //portServ = prefs.getInt("port", 0);
        boolean checkBox = prefs.getBoolean("checkBox", false);

        if (checkBox == true) {
            rl.setBackgroundResource(R.drawable.grille);
        } else {
            rl.setBackgroundResource(0);
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

        OSCMessage msg = new OSCMessage("/mallarme/masque/" + v.getResources().getResourceEntryName(v.getId()), send);
        OSCSend(addrServ, portServ, msg);

        return false;
    }
}