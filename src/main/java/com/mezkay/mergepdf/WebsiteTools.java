package com.mezkay.mergepdf;

import com.mezkay.mergepdf.exceptions.FormatNotFoundException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WebsiteTools {

    private static ArrayList<String> commonExtension = new ArrayList<>(Arrays.asList(".jpg", ".jpeg", ".png"));

    public static String determineExtension(String websiteURL) throws IOException, FormatNotFoundException {

        boolean formatFound = false;
        int i = 0;
        String extFound = null;

        while (!formatFound && i < commonExtension.size()) {
            String ext = commonExtension.get(i);
            URL url = new URL(websiteURL + ext);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            if(status <= 299) {
                extFound = ext;
                formatFound = true;
            }
            i++;
        }

        if(extFound == null) {
            throw new FormatNotFoundException();
        }
        return extFound;

    }

    public String getExtension(String pageURL, String extension) throws FormatNotFoundException, IOException {
        if (extension == null) {
            return determineExtension(pageURL);
        } else {
            URL url = new URL(pageURL + extension);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            if (status <= 299) {
                return extension;
            } else {
                return determineExtension(pageURL);
            }
        }
    }

    //public HashMap<String, >

    //public String
}
