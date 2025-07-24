package org.example;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.anotation.JsonProperty;
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
}
