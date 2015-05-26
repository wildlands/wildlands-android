package nl.wildlands.wildlandseducation.GPS;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class GPSTracker extends Service {

    private String usedProvider = LocationManager.NETWORK_PROVIDER;

    private Context context;
    private LocationListener listener;
    private LocationManager locationManager;

    public GPSTracker(Context context, LocationListener listener) {
        this.context = context;
        this.listener = listener;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        usedProvider = locationManager.getBestProvider(criteria, true);
    }

    public Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(usedProvider);
    }

    public void startTracking() throws GPSNotEnabledException {
        if (!locationManager.isProviderEnabled(usedProvider))
            throw new GPSNotEnabledException();
        locationManager.requestLocationUpdates(usedProvider, 0, 0, listener);
    }

    public void stopTracking() {
        locationManager.removeUpdates(listener);
    }

    public String getUsedProvider() {
        return usedProvider;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
