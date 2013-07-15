package risjarv.rpbl.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
//import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;

/**
 *
 * @author risjarv
 */
public class network {
    
     /*
     * Todo - make functions use ssl, in a case of /tunnistaudu serveri password
     * 
     */
    public static String getData(String url) {
        URL u;
        StringBuilder builder = new StringBuilder();
        try {
            u = new URL(url);
            try {
                BufferedReader theHTML = new BufferedReader(new InputStreamReader(u.openStream()));
                String thisLine = "";
                while ((thisLine = theHTML.readLine()) != null) {
                    builder.append(thisLine).append("\n");
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        } catch (MalformedURLException e) {
           // Bukkit.getLogger().severe("Oho, urlissa: "+url+" on vikaa.");
            Bukkit.getLogger().severe(e.getMessage());
        }
        return builder.toString();
    }
    
    public static String sendData(String url, Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        try {
            URL siteUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		
            Set keys = data.keySet();
            Iterator keyIter = keys.iterator();
            String content = "";
            for (int i = 0; keyIter.hasNext(); i++) {
                Object key = keyIter.next();
                if (i != 0) {
                    content += "&";
                }
                content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
            }

            out.writeBytes(content);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                //Bukkit.getLogger().info(line);
                builder.append(line).append("\n");
            }
            in.close();
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            return "";
        }
        return builder.toString();
    }
}
