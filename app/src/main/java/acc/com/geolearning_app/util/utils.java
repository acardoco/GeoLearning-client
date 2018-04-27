package acc.com.geolearning_app.util;

import java.math.BigDecimal;

public class utils {

    public static BigDecimal truncateDecimal(double x, int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }


    //CCNSTRUYE una url en formato String
    public static String getStringUrl(double lat, double lon){

        String urlbase = "https://maps.googleapis.com/maps/api/staticmap?center=";
        String urlcomun = "&zoom=18&format=jpg&size=400x400&maptype=satellite&key=";
        String urlkey= "AIzaSyB9qW-QzzGtT2xEsJlsuLgA5TOYNJS8ogo";

        StringBuilder url_total = new StringBuilder();
        url_total.append(urlbase)
                .append(lat)
                .append(",")
                .append(lon)
                .append(urlcomun)
                .append(urlkey);

        return url_total.toString();

    }
}
