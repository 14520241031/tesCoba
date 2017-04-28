package com.AstroGPS.cobaCoba;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AstroGPS.cobaCoba.helper.DbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    EditText txt_id;
    TextView txt_name;
    TextView txt_address;
    ImageView btn_submit, btn_cancel;
    DbHelper SQLite = new DbHelper(this);
    String id, name, address;

    GoogleMap googleMap;
    double latitude;
    double longitude;
    private Handler handler = new Handler();
    public double titik[];
    public Double longarray[];
    public Double latarray[];
    private TextView waktujalan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txt_id = (EditText) findViewById(R.id.txt_id);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_address = (TextView) findViewById(R.id.txt_address);
        btn_submit = (ImageView) findViewById(R.id.btn_submit);
        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);

        id = getIntent().getStringExtra(MainActivity.TAG_ID);
        name = getIntent().getStringExtra(MainActivity.TAG_NAME);
        address = getIntent().getStringExtra(MainActivity.TAG_ADDRESS);

        if (id == null || id == "") {
            setTitle("Add Data");
        } else {
            setTitle("Edit Data");
            txt_id.setText(id);
            txt_name.setText(name);
            txt_address.setText(address);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(String.valueOf(txt_id.getText()).equals(null) || String.valueOf(txt_id.getText()).equals("")) {
                        Toast.makeText(getApplicationContext(),"Masukkan mana Penyimpanan",Toast.LENGTH_SHORT).show();
                        txt_id.isFocused();
                    }else{
                        handler.postDelayed(runnable, 1000);
                        txt_id.setEnabled(false);
                    }
                    /*if (txt_id.getText().toString().equals("")) {
                        save();
                    } else {
                        edit();
                    }*/
                } catch (Exception e){
                    Log.e("Submit", e.toString());
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                //save();
                //blank();
                //finish();
            }
        });

        waktujalan = (TextView) findViewById(R.id.timer);

        //koordinat = (Button) findViewById(R.id.koordinat);
        //posisi_user = (Button) findViewById(R.id.posisi_user);

        //koordinat.setOnClickListener(this);
        //posisi_user.setOnClickListener(this);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        CekGPS();

        fm.getMapAsync(this);

        googleMap = fm.getMap();

        googleMap.setMyLocationEnabled(true);

        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        /*googleMap.addMarker(new MarkerOptions().position(new LatLng(-7.796713, 110.365407)).title("shovsfhio")
                .snippet("isishfoivhfi")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_oleh)));*/

        if (latitude != 0 && longitude != 0) {
            Toast.makeText(getApplicationContext(), "Latitude : " + latitude + " Longitude : " + longitude, Toast.LENGTH_LONG).show();
        }
        handler.removeCallbacks(runnable);
        //handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                blank();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Make blank all Edit Text
    private void blank() {
        txt_name.requestFocus();
        txt_id.setText(null);
        txt_name.setText(null);
        txt_address.setText(null);
    }

    // Save data to SQLite database
    private void save() {
        if (String.valueOf(txt_name.getText()).equals(null) || String.valueOf(txt_name.getText()).equals("") ||
                String.valueOf(txt_address.getText()).equals(null) || String.valueOf(txt_address.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please input name or address ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.insert(txt_id.getText().toString().trim(), txt_name.getText().toString().trim(), txt_address.getText().toString().trim());
            //blank();
            //finish();
        }
    }

    // Update data in SQLite database
    private void edit() {
        if (String.valueOf(txt_name.getText()).equals(null) || String.valueOf(txt_name.getText()).equals("") ||
                String.valueOf(txt_address.getText()).equals(null) || String.valueOf(txt_address.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please input name or address ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.update(txt_id.getText().toString().trim(), txt_name.getText().toString().trim(),
                    txt_address.getText().toString().trim());
            blank();
            finish();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
    */
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    private int para;
    private String nama;
    private String alamat;
    private int det;
    private String lala;
    private String lolo;
    private int jumData;
    //jam-------------------------------------------------------------------------------------------------------------------------
    public Runnable runnable = new Runnable() {

        @Override
        public void run() {
        if (para > 5) {
            if (latitude != 0 && longitude != 0) {
                try {
                    id = "";
                    lala = Double.toString(latitude).trim();
                    lolo = Double.toString(longitude).trim();
                    para = 0;
                    //alamat=alamat+" and "+lala+" / "+lolo;
                    jumData++;
                    longarray = new Double[jumData];
                    latarray = new Double[jumData];
                    //System.out.print("pindah : "+alamat);
                    //Log.d("pindah ",""+alamat);
                    //Toast.makeText(getApplicationContext(), "Latitude : " + latitude + " Longitude : " + longitude, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "" + alamat, Toast.LENGTH_LONG).show();
                    txt_name.setText(lala);
                    txt_address.setText(lolo);
                    save();
                }catch (Exception e){

                }
            }else {}
        }else {para++;}
        det++;
        waktujalan.setText(""+det);
        //}else{para++;}
        handler.postDelayed(this, 1000);
        }
    };

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void CekGPS() {
        try {
        /* Pengecekan GPS hidup / tidak */
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Info");
                builder.setMessage("Anda akan mengaktifkan GPS ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {

                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        } catch (Exception e) {
            // TODO: handle exception

        }
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // menampilkan status google play service
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            // Google Play Service Tersedia
            try {
                LocationManager locationManager = (LocationManager)
                        getSystemService(LOCATION_SERVICE);

                // Membuat Kriteria Untuk Penumpangan Provider
                Criteria criteria = new Criteria();

                // Mencari Provider Terbaik
                String provider = locationManager.getBestProvider(criteria, true);

                // Mendapatkan Lokasi Terakhir
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    Toast.makeText(getApplicationContext(), "Latitude : " + latitude + " Longitude : " + longitude, Toast.LENGTH_LONG).show();
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(provider, 50000, 0, this);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onLocationChanged(Location lokasi) {
        //TODO Auto-generated method stub
        latitude = lokasi.getLatitude();
        longitude = lokasi.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stud
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    //ini error (gagal ambil data)
    public void onMapReady(GoogleMap Mapa) {
        Mapa.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(-33.866, 151.195))  // Sydney
                .add(new LatLng(37.423, -122.091))  // Mountain View
        );
        try{
            //Log.d("gariscoba ",""+jumlahData);
            //Log.d("gariscoba lati ",""+latarray[1]);
            //Log.d("gariscoba longi ",""+longarray[1]);

            LatLng moveCam = new LatLng(latitude, longitude);
            Mapa.addMarker(new MarkerOptions().position(moveCam).title("Lokasi terakhir").snippet("Update lokasi terakir"));
            Mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(moveCam,9000));

            for(int aa=0; aa<jumData-1; aa++){
                // Polylines are useful for marking paths and routes on the map.
                Mapa.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(latarray[aa], longarray[aa]))  // Sydney
                        .add(new LatLng(latarray[aa+1], longarray[aa+1]))  // Mountain View
                );
            }
            Log.d("gariscoba","tidak ada yang salah");
        }catch (Exception e){
            Log.d("gariscoba","salah saat pengambilan data");
        }
    }

    public void mulai(View view){
        handler.postDelayed(runnable, 1000);
    }
    public void berhenti(View view){
        handler.removeCallbacks(runnable);
    }
}
