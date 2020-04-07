package ca.uqac.truckie.component;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anupamchugh on 27/11/15.
 */

public class DirectionsJSONParser {

    public static List<LatLng> parseRoute0(String input){
        List<LatLng> output = new ArrayList<>();
        Gson gson = new Gson();
        Routes routes = gson.fromJson(input, Routes.class);
        Routes.Route.Leg.Step[] steps = routes.routes[0].legs[0].steps;
        for (Routes.Route.Leg.Step step : steps) {
            output.addAll(decodePoly(step.polyline.points));
        }
        return output;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private class Routes {
        Route[] routes;
        class Route{
            Leg[] legs;
            class Leg{
                Step[] steps;
                class Step{
                    MyPolyline polyline;
                    class MyPolyline{
                        String points;
                    }
                }
            }
        }
    }
}