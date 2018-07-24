package mensajes.team.mx.best_provider_gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class Main_Activity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    TextView txt_location;
    TextView txt_direccion;
    public static final int SOLICITUD_PERMISO_LOCALIZACION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);

        ActivityCompat.requestPermissions(Main_Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_direccion = (TextView) findViewById(R.id.txt_direccion);

        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.getBestProvider(criterio, true);

        ultimaLocalizacion();
    }


    @Override
    protected void onResume() {
        super.onResume();
        activarProveedores();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(Main_Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(Main_Activity.this);
        }
    }

    private void activarProveedores() {
        if (ContextCompat.checkSelfPermission(Main_Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, Main_Activity.this);
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 10, Main_Activity.this);
            }
        } else {
            ActivityCompat.requestPermissions(Main_Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION) {
            if (grantResults.length== 1 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ultimaLocalizacion();
                activarProveedores();
            }
        }
    }

    void ultimaLocalizacion(){
        if(ContextCompat.checkSelfPermission(Main_Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocalizador(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocalizador(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        actualizaMejorLocalizador(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String provider) {
        activarProveedores();
    }

    @Override
    public void onProviderDisabled(String provider) {
        activarProveedores();
    }

    private void actualizaMejorLocalizador(Location localiz) {
        txt_location.setText(localiz.getLongitude() + "\n" + localiz.getLatitude());

        try {

            Geocoder geocoder = new Geocoder(Main_Activity.this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(localiz.getLatitude(), localiz.getLongitude(), 1);
            if(!list.isEmpty()) {
                Address c = list.get(0);
                txt_direccion.setText(c.getAddressLine(0));
            }

        } catch(Exception e) {
            e.getMessage();
        }

    }

}
