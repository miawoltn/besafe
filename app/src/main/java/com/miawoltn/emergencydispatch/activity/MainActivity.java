package com.miawoltn.emergencydispatch.activity;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.core.SOSDispatcher;

public class MainActivity extends AppCompatActivity {

    ImageButton fire;
    ImageButton robbery;
    ImageButton terrorist_attack;
    ImageButton murder;
    ImageButton accident;
    ImageButton suicide;
    ImageButton natural_disaster;

    LocationManager locationManager;
    SOSDispatcher sosDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sosDispatcher = new SOSDispatcher(MainActivity.this);

        /*Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        String best = locationManager.getBestProvider(crit, false);*/
       // locationManager.requestLocationUpdates(best, 0, 1, locationListener);

        fire = (ImageButton) findViewById(R.id.w_fire);
        robbery = (ImageButton) findViewById(R.id.w_robbery);
        terrorist_attack = (ImageButton) findViewById(R.id.terror_attack);
        murder = (ImageButton) findViewById(R.id.w_murder);
        accident = (ImageButton) findViewById(R.id.w_accident);
        suicide = (ImageButton) findViewById(R.id.w_suicide);
        natural_disaster = (ImageButton) findViewById(R.id.w_natural_disaster);

        fire.setOnClickListener(onClickListener);
        robbery.setOnClickListener(onClickListener);
        terrorist_attack.setOnClickListener(onClickListener);
        murder.setOnClickListener(onClickListener);
        accident.setOnClickListener(onClickListener);
        suicide.setOnClickListener(onClickListener);
        natural_disaster.setOnClickListener(onClickListener);
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.w_fire:
                    sosDispatcher.sendSMS(getString(R.string.phone_number), getString(R.string.fire_message));

                    break;
                case R.id.w_robbery:
                    sosDispatcher.sendSMS(getString(R.string.phone_number),getString(R.string.robbery_message));
                    break;
                case R.id.terror_attack:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Terror_Attack);
                   // LocationListener locationListener = new MyLocationListener();
                    /*if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        Toast.makeText(MainActivity.this,"Permission not granted.",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
                    Toast.makeText(MainActivity.this, locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)+" ",Toast.LENGTH_SHORT).show();*/
                    break;
                case R.id.w_murder:
                    sosDispatcher.sendSMS(getString(R.string.phone_number),getString(R.string.murder_message));
                    break;
                case R.id.w_accident:
                    sosDispatcher.sendSMS(getString(R.string.phone_number),getString(R.string.accident_message));
                    break;
                case R.id.w_suicide:
                    sosDispatcher.sendSMS(getString(R.string.phone_number),getString(R.string.suicide_message));
                    break;
                case R.id.w_natural_disaster:
                    sosDispatcher.sendSMS(getString(R.string.phone_number),getString(R.string.natural_disaster_message));
                    break;
            }
        }
    };

}
