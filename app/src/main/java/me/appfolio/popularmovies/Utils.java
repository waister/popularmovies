package me.appfolio.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Utils {

    static boolean isOnline(Context context) {
        ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    static String formatDate(String dateString) {
        if (!dateString.isEmpty()) {
            try {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                DateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

}
