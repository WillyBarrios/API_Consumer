package org.example;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;

public class Main {
    static class RaizProceso{
        @JsonProperty("id")
        public String id;

        @JsonProperty ("sistemaOrigen")
        public String sistemaOrigen;

        @JsonProperty("fechaGeneracion")
        public String fechaGeneracion;

        @JsonProperty("estadoGeneral")
        public String estadoGeneral;

        @JsonProperty("procesos")
        public List<Proceso> procesos;

        @JsonProperty("auditor")
        public Auditor auditor;

        @JsonProperty("metadata")
        public Metadata metadata;
    }
    public class Auditor {
        @JsonProperty("nombre")
        public String nombre;

        @JsonProperty("departamento")
        public String departamento;
    }

    static class Metadata{
        @JsonProperty("version")
        public String version;

        @JsonProperty("entorno")
        public String entorno;
    }
    static class Proceso{
        @JsonProperty("id")
        public Integer id;  // Cambiado de String a Integer

        @JsonProperty("nombre")
        public String nombre;

        @JsonProperty("tipo")
        public String tipo;

        @JsonProperty("estado")
        public String estado;

        @JsonProperty("prioridad")
        public String prioridad;

        @JsonProperty("fechaInicio")
        public String fechaInicio;

        @JsonProperty("responsable")
        public String responsable;

        @JsonProperty("childs")
        public List<Proceso> hijos;

        @JsonProperty("recursos")
        public List<Recurso> recursos;

        @JsonProperty("metricas")
        public Metricas metricas;
    }

    static class Recurso{
        @JsonProperty("id")
        public String id;

        @JsonProperty("tipo")
        public String tipo;

        @JsonProperty("nombre")
        public String nombre;

        @JsonProperty("url")
        public String url;
    }
    static class Metricas{
        @JsonProperty("tiempoEjecucion")
        public Integer tiempoEjecucion;

        @JsonProperty("costo")
        public Double costo;

        @JsonProperty("eficiencia")
        public Double eficiencia;
    }
    static class ProcesoAntiguo{
        public String id;
        public String nombre;
        public String FechaInicio;
    }

    static class ResultadoBusqueda{
        @JsonProperty("totalProcesos")
        private int totalProcesos;

        @JsonProperty("procesosCompletos")
        private int procesosCompletos;

        @JsonProperty("procesosPendientes")
        private int procesosPendientes;

        @JsonProperty("recursosTipoHerramienta")
        private int recursosTipoHerramienta;

        @JsonProperty("eficienciaPromedio")
        private double eficienciaPromedio;

        @JsonProperty("procesoMasAntiguo")
        private ProcesoAntiguo procesoMasAntiguo;
    }
    static class RequestBody {
        @JsonProperty("nombre")
        private String nombre;

        @JsonProperty("carnet")
        private String carnet;

        @JsonProperty("seccion")
        private String seccion;

        @JsonProperty("resultadoBusqueda")
        private ResultadoBusqueda resultadoBusqueda;

        @JsonProperty("payload")
        private Proceso payload;
    }

    static class Contador {
        int total = 0;
        int completos = 0;
        int pendientes = 0;
        int herramientas = 0;
        double sumaEficiencia = 0;
        int eficienciaCount = 0;
        ProcesoAntiguo masAntiguo = null;
        LocalDateTime fechaAntigua = null;
    }

    static class RespuestaEvaluacion {
        public String nombre;
        public String carnet;
        public String seccion;
        public ResultadoBusqueda resultadoBusqueda;
        public JsonNode payload;  // Cambiado de String a JsonNode para manejar el JSON completo
    }
    private static void procesarProceso(Proceso proceso, Contador contador) {
        // Incrementar contador total
        contador.total++;

        // Contar estado
        if ("completo".equals(proceso.estado)) {
            contador.completos++;
        } else if ("pendiente".equals(proceso.estado)) {
            contador.pendientes++;
        }

        // Contar recursos tipo herramienta
        if (proceso.recursos != null) {
            for (Recurso recurso : proceso.recursos) {
                if ("herramienta".equals(recurso.tipo)) {
                    contador.herramientas++;
                }
            }
        }

        // Sumar eficiencia
        if (proceso.metricas != null && proceso.metricas.eficiencia != null) {
            contador.sumaEficiencia += proceso.metricas.eficiencia;
            contador.eficienciaCount++;
        }

        // Verificar si es el más antiguo
        if (proceso.fechaInicio != null) {
            LocalDateTime fechaProceso = LocalDateTime.parse(proceso.fechaInicio.substring(0, proceso.fechaInicio.indexOf('.')));
            if (contador.fechaAntigua == null || fechaProceso.isBefore(contador.fechaAntigua)) {
                contador.fechaAntigua = fechaProceso;
                contador.masAntiguo = new ProcesoAntiguo();
                contador.masAntiguo.id = proceso.id.toString();  // Convertir de Integer a String
                contador.masAntiguo.nombre = proceso.nombre;
                contador.masAntiguo.fechaInicio = proceso.fechaInicio;
            }
        }

        // Procesar hijos recursivamente
        if (proceso.hijos != null) {
            for (Proceso hijo : proceso.hijos) {
                procesarProceso(hijo, contador);
            }
        }
    }
    private static Proceso buscarProcesoPorId(List<Proceso> procesos, int idBuscado) {
        for (Proceso p : procesos) {
            if (p.id != null && p.id == idBuscado) {
                return p;
            }
            if (p.hijos != null) {
                Proceso encontrado = buscarProcesoPorId(p.hijos, idBuscado);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
    private static void imprimirPayload(Proceso proceso, String indentacion) {
        System.out.println(indentacion + "Proceso ID: " + proceso.id);
        System.out.println(indentacion + "Nombre: " + proceso.nombre);
        System.out.println(indentacion + "Estado: " + proceso.estado);
        System.out.println(indentacion + "Fecha de inicio: " + proceso.fechaInicio);

        if (proceso.recursos != null && !proceso.recursos.isEmpty()) {
            System.out.println(indentacion + "Recursos:");
            for (Recurso recurso : proceso.recursos) {
                System.out.println(indentacion + "    • ID: " + recurso.id + " (Tipo: " + recurso.tipo + ")");
            }
        }

        if (proceso.metricas != null) {
            System.out.println(indentacion + "Eficiencia: " + proceso.metricas.eficiencia + "%");
        }

        if (proceso.hijos != null && !proceso.hijos.isEmpty()) {
            System.out.println(indentacion + "Procesos hijos:");
            for (Proceso hijo : proceso.hijos) {
                imprimirPayload(hijo, indentacion + "    ");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Consumir API Generadora
        URL url = new URL("https://58o1y6qyic.execute-api.us-east-1.amazonaws.com/default/taskReport");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStream is = conn.getInputStream();

        // En el método main, después de obtener el InputStream

        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        String jsonResponse = scanner.hasNext() ? scanner.next() : "";
        System.out.println("JSON recibido:");
        System.out.println(jsonResponse);

        // Ahora sí intentamos deserializar
        RaizProceso raiz = mapper.readValue(jsonResponse, RaizProceso.class);

        // Procesar todos los procesos
        Contador contador = new Contador();
        for (Proceso p : raiz.procesos) {
            procesarProceso(p, contador);
        }

        // Crear el objeto de solicitud
        RequestBody requestBody = new RequestBody();
        requestBody.nombre = "Willy Barrios";
        requestBody.carnet = "0909164589";
        requestBody.seccion = "4";

        // Configurar el resultadoBusqueda
        ResultadoBusqueda busqueda = new ResultadoBusqueda();
        busqueda.totalProcesos = contador.total;
        busqueda.procesosCompletos = contador.completos;
        busqueda.procesosPendientes = contador.pendientes;
        busqueda.recursosTipoHerramienta = contador.herramientas;
        busqueda.eficienciaPromedio = contador.eficienciaCount > 0 ?
                contador.sumaEficiencia / contador.eficienciaCount : 0.0;
        busqueda.procesoMasAntiguo = contador.masAntiguo;
        requestBody.resultadoBusqueda = busqueda;

        // Configurar el payload como el proceso más antiguo
        if (contador.masAntiguo != null) {
            int idAntiguo = Integer.parseInt(contador.masAntiguo.id);
            Proceso procesoAntiguoCompleto = buscarProcesoPorId(raiz.procesos, idAntiguo);
            if (procesoAntiguoCompleto != null) {
                requestBody.payload = procesoAntiguoCompleto;
            }
        }

        // Configuración del POST
        URL urlPost = new URL("https://t199qr74fg.execute-api.us-east-1.amazonaws.com/default/taskReportVerification");
        HttpURLConnection connPost = (HttpURLConnection) urlPost.openConnection();
        connPost.setRequestMethod("POST");
        connPost.setDoOutput(true);
        connPost.setRequestProperty("Content-Type", "application/json");

        String jsonBody = mapper.writeValueAsString(requestBody);
        System.out.println("\nEnviando JSON:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));

        try (OutputStream os = connPost.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Lectura de respuesta
        int responseCode = connPost.getResponseCode();
        InputStream responseStream = (responseCode >= 400) ? connPost.getErrorStream() : connPost.getInputStream();

        if (responseStream != null) {
            Scanner sc = new Scanner(responseStream).useDelimiter("\\A");
            String response = sc.hasNext() ? sc.next() : "";
            System.out.println("\nCódigo de respuesta: " + responseCode);
            System.out.println("Respuesta API Evaluadora: " + response);
        }
    }
}

