package nl.wildlands.wildlandseducation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class GPSTracker extends Service {

    private Context context;

    private LocationListener listener;

    private LocationManager locationManager;

    public GPSTracker(Context context, LocationListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public void startTracking() throws GPSNotEnabledException {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            throw new GPSNotEnabledException();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    public void stopTracking() {
        locationManager.removeUpdates(listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
