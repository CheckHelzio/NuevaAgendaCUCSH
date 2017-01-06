package ccv.checkhelzio.nuevaagendacucsh.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.ui.Principal;

/**
 * Created by check on 09/09/2016.
 */

 public class DescargarBD extends AsyncTask<Object, Void, String> {
    private Principal principal;

    @Override
    protected String doInBackground(Object... objects) {
        Log.v("ELIMINAR", "DESCARGAR BASE DE DATOS... BACKGROUND");
        principal = (Principal) objects[1];
        try {
            return loadFromNetwork(objects[0].toString());
        } catch (IOException e) {
            return "Error de conexión";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.v("ELIMINAR", "DESCARGAR BASE DE DATOS... POST EXECUTE");
        principal.postDescargar(s);
    }

    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";
        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(0);
        conn.setConnectTimeout(0);
        return conn.getInputStream();
    }

    private String readIt(InputStream stream) throws IOException{
        String a = "";
        String linea;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        while ((linea = br.readLine()) != null) {
            a += linea;
        }
        return a;
    }
}
