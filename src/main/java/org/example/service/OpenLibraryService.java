package org.example.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.model.Libro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio de integracion con Open Library API.
 *
 * <p>Realiza busquedas por texto libre y transforma los resultados JSON
 * en objetos {@link Libro} listos para mostrar o registrar.</p>
 */
public class OpenLibraryService {

    private static final String BASE_URL = "https://openlibrary.org/search.json?q=";
    private static final String AUTOR_URL = "https://openlibrary.org/authors/%s.json";
    private static final Pattern PATRON_ANIO = Pattern.compile("(\\d{4})");

    /**
     * Busca libros en Open Library API.
     *
     * @param texto termino de busqueda ingresado por el usuario.
     * @return lista de libros encontrados; vacia si no hay resultados o si ocurre error.
     */
    public List<Libro> buscarLibros(String texto) {
        List<Libro> listaLibros = new ArrayList<>();
        Map<String, DatosAutorApi> cacheAutores = new HashMap<>();

        try {
            String urlStr = BASE_URL + URLEncoder.encode(texto, "UTF-8");
            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);

            if (con.getResponseCode() != 200) {
                System.out.println("Error: No se pudo conectar a Open Library. Codigo: " + con.getResponseCode());
                return listaLibros;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder contenido = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                contenido.append(inputLine);
            }
            in.close();
            con.disconnect();

            JsonObject json = JsonParser.parseString(contenido.toString()).getAsJsonObject();
            JsonArray docs = json.getAsJsonArray("docs");

            if (docs == null || docs.size() == 0) {
                return listaLibros;
            }

            for (int i = 0; i < Math.min(10, docs.size()); i++) {
                JsonObject libroJson = docs.get(i).getAsJsonObject();

                String titulo = libroJson.has("title")
                        ? libroJson.get("title").getAsString()
                        : "Sin titulo";

                String autor = "Desconocido";
                String authorKey = null;
                if (libroJson.has("author_name")) {
                    JsonArray autores = libroJson.getAsJsonArray("author_name");
                    if (autores.size() > 0) {
                        autor = autores.get(0).getAsString();
                    }
                }
                if (libroJson.has("author_key")) {
                    JsonArray keys = libroJson.getAsJsonArray("author_key");
                    if (keys.size() > 0) {
                        authorKey = keys.get(0).getAsString();
                    }
                }

                String editorial = "N/A";
                if (libroJson.has("publisher")) {
                    JsonArray publishers = libroJson.getAsJsonArray("publisher");
                    if (publishers.size() > 0) {
                        editorial = publishers.get(0).getAsString();
                    }
                }

                int year = libroJson.has("first_publish_year")
                        ? libroJson.get("first_publish_year").getAsInt()
                        : 2024;

                Libro libro = new Libro(0, titulo, autor, editorial, LocalDate.of(year, 1, 1));

                if (authorKey != null && !authorKey.isBlank()) {
                    DatosAutorApi datosAutor = cacheAutores.computeIfAbsent(authorKey, this::obtenerDatosAutor);
                    if (datosAutor != null) {
                        libro.setAutorNacionalidad(datosAutor.nacionalidad);
                        libro.setAutorAnioNacimiento(datosAutor.anioNacimiento);
                    }
                }

                listaLibros.add(libro);
            }

        } catch (Exception e) {
            System.out.println("Error al buscar libros en Open Library: " + e.getMessage());
        }

        return listaLibros;
    }

    private DatosAutorApi obtenerDatosAutor(String authorKey) {
        try {
            String urlStr = String.format(AUTOR_URL, URLEncoder.encode(authorKey, "UTF-8"));
            String contenido = leerJsonDesdeUrl(urlStr);
            if (contenido == null) {
                return null;
            }

            JsonObject json = JsonParser.parseString(contenido).getAsJsonObject();
            String birthDate = json.has("birth_date") ? json.get("birth_date").getAsString() : null;
            String birthPlace = json.has("birth_place") ? json.get("birth_place").getAsString() : null;

            Integer anioNacimiento = extraerAnio(birthDate);
            String nacionalidad = extraerNacionalidad(birthPlace);

            return new DatosAutorApi(nacionalidad, anioNacimiento);
        } catch (Exception e) {
            return null;
        }
    }

    private String leerJsonDesdeUrl(String urlStr) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);

            if (con.getResponseCode() != 200) {
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder contenido = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                contenido.append(inputLine);
            }
            in.close();
            return contenido.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private Integer extraerAnio(String textoFecha) {
        if (textoFecha == null || textoFecha.isBlank()) {
            return null;
        }

        Matcher matcher = PATRON_ANIO.matcher(textoFecha);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private String extraerNacionalidad(String birthPlace) {
        if (birthPlace == null || birthPlace.isBlank()) {
            return null;
        }

        String[] partes = birthPlace.split(",");
        String ultimaParte = partes[partes.length - 1].trim();
        return ultimaParte.isEmpty() ? null : ultimaParte;
    }

    private static final class DatosAutorApi {
        private final String nacionalidad;
        private final Integer anioNacimiento;

        private DatosAutorApi(String nacionalidad, Integer anioNacimiento) {
            this.nacionalidad = nacionalidad;
            this.anioNacimiento = anioNacimiento;
        }
    }
}

