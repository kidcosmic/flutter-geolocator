package com.baseflow.flutter.plugin.geolocator.tasks;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;

import com.baseflow.flutter.plugin.geolocator.data.GeolocationAccuracy;
import com.baseflow.flutter.plugin.geolocator.data.LocationMapper;
import com.google.android.gms.common.util.Strings;

public class StreamLocationUpdatesTask extends LocationTask {
    private GeolocatorLocationListener mLocationListener;

    public StreamLocationUpdatesTask(TaskContext context) {
        super(context);
    }

    @Override
    protected void acquirePosition() {

        LocationManager locationManager = getLocationManager();

        // Make sure we remove existing listeners before we register a new one
        if(mLocationListener != null) {
            locationManager.removeUpdates(mLocationListener);
        }

        // Try to get the best possible location provider for the requested accuracy
        String bestProvider = getBestProvider(
                locationManager,
                mLocationOptions.accuracy);

        if(Strings.isEmptyOrWhitespace(bestProvider)) {
            handleError(
                    "INVALID_LOCATION_SETTINGS",
                    "Location settings are inadequate, check your location settings.");

            return;
        }

        mLocationListener = new GeolocatorLocationListener(
                getTaskContext(),
                locationManager,
                mLocationOptions.accuracy,
                false,
                bestProvider);

        Looper looper = Looper.myLooper();
        if(looper == null) {
            looper = Looper.getMainLooper();
        }

        locationManager.requestLocationUpdates(
                bestProvider,
                0,
                mLocationOptions.distanceFilter,
                mLocationListener,
                looper);
    }

    @Override
    protected void handleError(String code, String message) {
        getTaskContext().getResult().error(
                code,
                message,
                null);
    }
}
