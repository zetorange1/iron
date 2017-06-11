/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.location.Location
 *  android.location.LocationManager
 */
package com.ironsource.environment;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import com.ironsource.environment.ApplicationContext;
import java.util.List;

public class LocationService {
    public static Location getLastLocation(Context context) {
        Location bestLocation = null;
        long bestLocationTime = Long.MIN_VALUE;
        if (!ApplicationContext.isPermissionGranted(context, "android.permission.ACCESS_FINE_LOCATION")) {
            return bestLocation;
        }
        LocationManager locationManager = (LocationManager)context.getApplicationContext().getSystemService("location");
        for (String provider : locationManager.getAllProviders()) {
            long currentTime;
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null || (currentTime = location.getTime()) <= bestLocationTime) continue;
            bestLocation = location;
        }
        return bestLocation;
    }
}

