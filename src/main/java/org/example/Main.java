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
    public class auditor {
        @JsonProperty("nombre")
        public String nombre;

        @JsonProperty("departamento")
        public String departamento;
    }
}