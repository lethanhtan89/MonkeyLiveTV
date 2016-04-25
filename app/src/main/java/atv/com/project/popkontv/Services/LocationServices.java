package atv.com.project.popkontv.Services;

/**
 * Created by arjun on 5/13/15.
 */

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import atv.com.project.popkontv.Application.Popkon;

public class LocationServices {
    private static LocationManager locationManager;
    private static String provider;

    private static LocationServices locationServiceInstance;

    public static LocationServices getInstance(){
        if(locationServiceInstance == null){
            locationServiceInstance = new LocationServices();
        }
        return locationServiceInstance;
    }


    public Location getCurrentLocation(){
        locationManager = (LocationManager) Popkon.context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteriaToGetLocation = new Criteria();
        provider = locationManager.getBestProvider(criteriaToGetLocation, false);
        return locationManager.getLastKnownLocation(provider);
    }
}
