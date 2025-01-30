package controller.TDA.graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import controller.dao.HotelesDao;
import controller.TDA.list.LinkedList;
import controller.TDA.list.ListEmptyException;
import models.Hoteles;

import static java.lang.Math.*;

public abstract class Graph {

    // Ruta para guardar el archivo
    public static String filePath = "data/";

    private Map<Integer, Hoteles> vertexModels = new HashMap<>();

    // Métodos abstractos
    public abstract Integer nro_vertices();
    public abstract Integer nro_edges();
    public abstract Boolean is_edges(Integer v1, Integer v2) throws Exception;
    public abstract Float wieght_edge(Integer v1, Integer v2) throws Exception;
    public abstract void add_edge(Integer v1, Integer v2) throws Exception;
    public abstract void add_edge(Integer v1, Integer v2, Float weight) throws Exception;
    public abstract LinkedList<Adyecencia> adyecencias(Integer v1);

    // Método para obtener el ID de un vértice
    public Integer getVertex(Integer label) throws Exception {
        return label;
    }

    @Override
    public String toString() {
        StringBuilder grafo = new StringBuilder();
        try {
            for (int i = 1; i <= this.nro_vertices(); i++) {
                grafo.append("Vertice: ").append(i).append("\n");
                LinkedList<Adyecencia> lista = this.adyecencias(i);
                if (!lista.isEmpty()) {
                    Adyecencia[] ady = lista.toArray();
                    for (Adyecencia a : ady) {
                        grafo.append("ady: V").append(a.getdestination())
                             .append(" weight: ").append(a.getweight()).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grafo.toString();
    }

    // Método para guardar el grafo en formato JSON
    public void saveGraphLabel(String filename) throws Exception {
        JsonArray graphArray = new JsonArray();
        for (int i = 1; i <= this.nro_vertices(); i++) {
            JsonObject vertexObject = new JsonObject();
            vertexObject.addProperty("labelId", this.getVertex(i));

            JsonArray destinationsArray = new JsonArray();
            LinkedList<Adyecencia> adyacencias = this.adyecencias(i);
            if (!adyacencias.isEmpty()) {
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adyecencia adj = adyacencias.get(j);
                    JsonObject destinationObject = new JsonObject();
                    destinationObject.addProperty("from", this.getVertex(i));
                    destinationObject.addProperty("to", adj.getdestination());
                    destinationObject.addProperty("weight", adj.getweight());
                    destinationsArray.add(destinationObject);
                }
            }
            vertexObject.add("destinations", destinationsArray);
            graphArray.add(vertexObject);
        }

        Gson gson = new Gson();
        String json = gson.toJson(graphArray);

        // Crear el directorio si no existe
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Escribir el archivo JSON
        try (FileWriter fileWriter = new FileWriter(filePath + filename)) {
            fileWriter.write(json);
        }
    }

    public void cargarModelosDesdeDao() throws  ListEmptyException {
    HotelesDao hotelDao = new HotelesDao();
    LinkedList<Hoteles> hotelist = hotelDao.getListAll();

    for (int i = 0; i < hotelist.getSize(); i++) {
        Hoteles hotel = hotelist.get(i);
        vertexModels.put(hotel.getIdHotel(), hotel);
    }
}


    // Método para cargar el grafo desde un archivo JSON
    public void loadGraph(String filename) throws Exception {
    
        try (FileReader fileReader = new FileReader(filePath + filename)) {
            Gson gson = new Gson();
            JsonArray graphArray = gson.fromJson(fileReader, JsonArray.class);
    
            // Iterar sobre los vértices en el grafo
            for (JsonElement vertexElement : graphArray) {
                JsonObject vertexObject = vertexElement.getAsJsonObject();
    
                // Obtener el ID del vértice
                Integer labelId = vertexObject.get("labelId").getAsInt();    
                // Obtener el modelo ya existente en lugar de crear uno nuevo
                Hoteles model = vertexModels.get(labelId);
    
                if (model == null) {
                    continue; // Si no existe, pasar al siguiente vértice
                }
    

                // Asociar el modelo al vértice
                this.addVertexWithModel(labelId, model);
    
                // Obtener el array de destinos
                JsonArray destinationsArray = vertexObject.getAsJsonArray("destinations");
    
                for (JsonElement destinationElement : destinationsArray) {
                    JsonObject destinationObject = destinationElement.getAsJsonObject();
    
                    // Obtener los valores "from", "to"
                    Integer from = destinationObject.get("from").getAsInt();
                    Integer to = destinationObject.get("to").getAsInt();
    
                    // Obtener los modelos correspondientes a los vértices 'from' y 'to'
                    Hoteles modelFrom = vertexModels.get(from);
                    Hoteles modelTo = vertexModels.get(to);
    
                    if (modelFrom == null || modelTo == null) {
                    } else {
    
                        // Calcular la distancia utilizando la función 'calcularDistancia'
                        Float weight = (float) calcularDistancia(modelFrom, modelTo);
    
                        // Agregar la arista solo con "from" y "to" (sin el peso en el JSON)
                        this.add_edge(from, to, weight); // Aquí el peso se calcula y se usa internamente, pero no se guarda en el JSON.
                        System.out.println("Arista añadida de " + from + " a " + to + " con peso calculado: " + weight);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Método para borrar todas las adyacencias de todos los vértices
    public void clearEdges() {
        for (int i = 1; i <= this.nro_vertices(); i++) {
            this.adyecencias(i).reset();
        }
    }


    public void loadGraphWithRandomEdges(String filename) throws Exception {
        // Primero se carga el grafo del archivo JSON
    loadGraph(filename);

        // Segundo obtener los parques desde el Dao para asociarlos a los vértices
    cargarModelosDesdeDao();

    clearEdges(); // Borrar todas las adyacencias


    //Se agregan al menos 3 conexiones aleatorias por cada vértice
    Random random = new Random();
    for (int i = 1; i <= this.nro_vertices(); i++) {
        LinkedList<Adyecencia> existingEdges = this.adyecencias(i);
        int connectionsCount = existingEdges.getSize();

        // Asegurar al menos 3 conexiones por vértice
        while (connectionsCount < 2) {
            //Generar un vértice aleatorio diferente al actual
            int randomVertex = random.nextInt(this.nro_vertices()) + 1;
            while (randomVertex == i || is_edges(i, randomVertex)) {
                randomVertex = random.nextInt(this.nro_vertices()) + 1;
            }

            // Obtener los modelos correspondientes a los vértices
            Hoteles modelFrom = vertexModels.get(i);
            Hoteles modelTo = vertexModels.get(randomVertex);

            // Calcular un peso aleatorio (o usar el método calcularDistancia si ya tienes coordenadas)
            Float weight = (float) calcularDistancia(modelFrom, modelTo); // Peso aleatorio entre 0 y 100
            add_edge(i, randomVertex, weight); // Agregar la arista con el peso calculado
            connectionsCount++;
        }
    }


    saveGraphLabel(filename); // Guardar el grafo modificado
    }

    // Método para agregar un vértice con su modelo asociado
    public void addVertexWithModel(Integer vertexId, Hoteles model) {
        vertexModels.put(vertexId, model);  // Asociar el vértice con su modelo
    }

    // Método para verificar si un archivo existe en la ruta especificada
    public boolean existsFile(String filename) {
        File file = new File(filePath + filename);
        return file.exists();
    }

    public static double calcularDistancia(Hoteles hotel1, Hoteles hotel2) {
        double lat1 = toRadians(hotel1.getLatitud());
        double lon1 = toRadians(hotel1.getLongitud());
        double lat2 = toRadians(hotel2.getLatitud());
        double lon2 = toRadians(hotel2.getLongitud());
    
        // Fórmula de Haversine
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
        final double R = 6371000.0; 
        double distancia = R * c;
    
        return Math.round((distancia / 100.0) * 100.0) / 100.0;  // Redondear a 2 decimales
    }
    

    // Método para obtener los pesos de las aristas
    public JsonArray obtainWeights() throws Exception {
        JsonArray result = new JsonArray();
        
        // Iterar sobre todos los vértices del grafo
        for (int i = 1; i <= this.nro_vertices(); i++) {
            JsonObject vertexInfo = new JsonObject();
            Hoteles model = vertexModels.get(i);
            if (model != null) {
                vertexInfo.addProperty("name", model.getNombre()); // Nombre del vértice
            }
            vertexInfo.addProperty("labelId", this.getVertex(i)); // ID del vértice actual
            JsonArray destinations = new JsonArray(); // Lista de conexiones para el vértice
            LinkedList<Adyecencia> adyacencias = this.adyecencias(i);

            if (!adyacencias.isEmpty()) {
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adyecencia adj = adyacencias.get(j);
                    JsonObject destinationInfo = new JsonObject();
                    destinationInfo.addProperty("from", this.getVertex(i)); // Desde el vértice actual
                    destinationInfo.addProperty("to", adj.getdestination()); // Al destino
                    destinationInfo.addProperty("weight", adj.getweight()); // Peso de la arista
                    destinations.add(destinationInfo);
                }
            }

            vertexInfo.add("destinations", destinations); // Agregar las conexiones al vértice
            result.add(vertexInfo); // Agregar la información del vértice al resultado
        }

        return result;
    }

    // Método principal para guardar el grafo con el nombre "grafo.json"
    public void guardarGrafo() {
        try {
            // Asigna el nombre del archivo como "grafo.json"
            String filename = "grafo.json";  // El nombre que deseas para el archivo
            saveGraphLabel(filename);        // Llamas a tu método con el nombre del archivo
        } catch (Exception e) {
            e.printStackTrace();  // En caso de error, imprime la traza del error
        }
    }


    public JsonObject getVisGraphData() throws Exception {
        JsonObject visGraph = new JsonObject();
    
        // Arrays para los nodos y las aristas
        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();
        
    
        // Iteramos sobre los vértices
        for (int i = 1; i <= this.nro_vertices(); i++) {
            JsonObject node = new JsonObject();
            Hoteles model = vertexModels.get(i);
            if (model != null) {
                node.addProperty("name", model.getNombre());  // Nombre del vértice
            }
            node.addProperty("id", i);  // ID del nodo
            node.addProperty("label", "V" + i);  // Etiqueta del nodo (puedes personalizarlo)
            
            nodes.add(node);
    
            LinkedList<Adyecencia> adyacencias = this.adyecencias(i);
            if (!adyacencias.isEmpty()) {
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adyecencia adj = adyacencias.get(j);
                    JsonObject edge = new JsonObject();
                    edge.addProperty("from", i);  // Nodo origen
                    edge.addProperty("to", adj.getdestination());  // Nodo destino
                    edge.addProperty("weight", adj.getweight());
                    edges.add(edge);
                }
            }
        }
    
        visGraph.add("nodes", nodes);
        visGraph.add("edges", edges);
        
        return visGraph;
    }
}
