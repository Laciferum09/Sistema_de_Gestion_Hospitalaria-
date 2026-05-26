/*
 * Sistema de Gestión Hospitalaria
 */
package com.mycompany.chacienda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * CHacienda - Busca nombre por cedula costarricense.
 * Usa la API de GoMeta (padron TSE) y Hacienda como respaldo.
 * Deshabilita verificacion SSL para compatibilidad con Java antiguo.
 */
public class CHacienda {

    private static final int TIMEOUT_MS = 15000;

    static {
        // Deshabilitar verificacion SSL globalmente para estas APIs
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            System.err.println("[CHacienda] Error SSL init: " + e.getMessage());
        }
    }

    public String buscarNombre(String cedula) {
        cedula = cedula.trim().replaceAll("[^0-9]", "");

        // Intentar GoMeta primero
        String resultado = intentarGoMeta(cedula);
        if (resultado != null) return resultado;

        // Intentar Hacienda como respaldo
        resultado = intentarHacienda(cedula);
        if (resultado != null) return resultado;

        return "No encontrado";
    }

    private String intentarGoMeta(String cedula) {
        try {
            String body = hacerGet("https://apis.gometa.org/cedulas/" + cedula);
            if (body == null) return null;

            String nombre    = extraer(body, "\"nombre\":\"");
            String apellido1 = extraer(body, "\"apellido1\":\"");
            String apellido2 = extraer(body, "\"apellido2\":\"");

            if (nombre == null) return null;

            StringBuilder completo = new StringBuilder(nombre);
            if (apellido1 != null) completo.append(" ").append(apellido1);
            if (apellido2 != null) completo.append(" ").append(apellido2);
            return completo.toString().trim().toUpperCase();

        } catch (Exception e) {
            System.err.println("[CHacienda] GoMeta error: " + e.getMessage());
            return null;
        }
    }

    private String intentarHacienda(String cedula) {
        try {
            String body = hacerGet("https://api.hacienda.go.cr/fe/ae?identificacion=" + cedula);
            if (body == null) return null;

            // El JSON de Hacienda usa espacios: "nombre": "NOMBRE"
            String nombre = extraerConEspacios(body, "\"nombre\"");
            return nombre != null ? nombre.toUpperCase() : null;

        } catch (Exception e) {
            System.err.println("[CHacienda] Hacienda error: " + e.getMessage());
            return null;
        }
    }

    private String hacerGet(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(TIMEOUT_MS);
            con.setReadTimeout(TIMEOUT_MS);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            System.out.println("[CHacienda] " + urlStr + " -> HTTP " + status);
            if (status != 200) return null;

            BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) sb.append(linea);
            br.close();
            String body = sb.toString();
            System.out.println("[CHacienda] Body: " + body);
            return body;

        } catch (Exception e) {
            System.err.println("[CHacienda] Error GET " + urlStr + ": " + e.getMessage());
            return null;
        }
    }

    private String extraer(String json, String clave) {
        int inicio = json.indexOf(clave);
        if (inicio == -1) return null;
        inicio += clave.length();
        int fin = json.indexOf("\"", inicio);
        if (fin == -1) return null;
        String valor = json.substring(inicio, fin).trim();
        return valor.isEmpty() ? null : valor;
    }

    // Extrae un campo con formato: "clave": "valor" (con espacios)
    private String extraerConEspacios(String json, String clave) {
        int idx = json.indexOf(clave);
        if (idx == -1) return null;
        idx += clave.length();
        // Saltar espacios y los dos puntos
        while (idx < json.length() && (json.charAt(idx) == ' ' || json.charAt(idx) == ':')) idx++;
        if (idx >= json.length() || json.charAt(idx) != '"') return null;
        idx++; // saltar la comilla de apertura
        int fin = json.indexOf("\"", idx);
        if (fin == -1) return null;
        String valor = json.substring(idx, fin).trim();
        return valor.isEmpty() ? null : valor;
    }
}
