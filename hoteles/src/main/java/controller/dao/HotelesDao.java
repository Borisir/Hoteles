package controller.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.TDA.list.LinkedList;
import controller.dao.implement.AdapterDao;
import controller.TDA.list.ListEmptyException;
import controller.TDA.graph.GraphLabelNoDirect;
import controller.TDA.graph.algoritmos.BellmanFord;
import controller.TDA.graph.algoritmos.DFS;
import controller.TDA.graph.algoritmos.Floyd;
import models.Hoteles;
public class HotelesDao extends AdapterDao<Hoteles> {
    private Hoteles hoteles;
    private LinkedList<Hoteles> listAll;
    private GraphLabelNoDirect<String> graph;
    private LinkedList<String> vertexName;
    private String name = "HotelesGrafo.json";
    
    public GraphLabelNoDirect<String> createGraph() {
        if (vertexName == null) {
            vertexName = new LinkedList<>();
        }


        LinkedList<Hoteles> list = this.getListAll();
        if (!list.isEmpty()) {
            if (graph == null) {
                graph = new GraphLabelNoDirect<>(list.getSize(), String.class);
            }


            Hoteles[] hotel = list.toArray();
            for (int i = 0; i < hotel.length; i++) {
                this.graph.labelsVertices(i + 1, hotel[i].getNombre());
                vertexName.add(hotel[i].getNombre());
            }
            this.graph.drawGraph();
        }
        return this.graph;
    }


    public void saveGraph() throws Exception {
        this.graph.saveGraphLabel(name);
    
}

public JsonObject getGraphData() throws Exception {
    if (graph == null) {
        createGraph();
    }

    if (graph.existsFile(name)) {
        graph.cargarModelosDesdeDao();
        graph.loadGraph(name);

        // Asegúrate de que el tipo de dato sea correcto
        JsonObject graphData = graph.getVisGraphData(); // Aquí debe devolver el formato correcto
        return graphData; // Se asume que graphData es un JsonObject que contiene datos correctos
    } else {
        throw new Exception("No existe el archivo");
    }
}


public JsonArray obtainWeights() throws Exception {
    if (graph == null) {
        createGraph();
    }

    if (graph.existsFile(name)) {
        graph.cargarModelosDesdeDao();
        graph.loadGraph(name);
        JsonArray graphData = graph.obtainWeights();
        return graphData;
    } else {
        throw new Exception("No existe el archivo");
    }
}

public GraphLabelNoDirect<String> obtenerGrafo() throws Exception {
    if (graph == null) {
        createGraph();
    }

    if (graph.existsFile(name)) {
        graph.cargarModelosDesdeDao();
        graph.loadGraph(name);
        System.out.println("modelo asociado al grafo: " + name);
    } else {
        throw new Exception("No existe el archivo");
    }
            return graph;

}

public GraphLabelNoDirect<String> adyacencias() throws Exception {
    if (graph == null) {
        createGraph();
    }

    if (graph.existsFile(name)) {
        graph.cargarModelosDesdeDao();
        graph.loadGraphWithRandomEdges(name);
    } else {
        throw new Exception("No existe el archivo");
    }
    saveGraph();
    System.out.println("Grafo con adyacencias creado" + graph);
    return graph;

}

public String caminoCorto(int origen, int destino, int algoritmo) throws Exception {
    if (graph == null) {
        throw new Exception("Grafo no existe");
    }
    String recorrido = "";

    if (algoritmo == 1) { // Usar algoritmo de Floyd
        BellmanFord bellmanFord = new BellmanFord(graph, origen, destino);
        recorrido = bellmanFord.caminoCorto(); 
    } else { 
        Floyd floydWarshall = new Floyd(graph, origen, destino);
        recorrido = floydWarshall.caminoCorto(); 
    }
    return recorrido; 

}

public String dFS(int origen) throws Exception {
    if (graph == null) {
        throw new Exception("El grafo no existe");
    }

    // Crear la instancia de BFS con el grafo y el nodo de origen
    DFS bfsAlgoritmo = new DFS(graph, origen);

    // Llamamos al método de recorrerGrafo() de la clase BFS para obtener el recorrido
    String recorrido = bfsAlgoritmo.recorrerGrafo();
    return recorrido;
}




    public HotelesDao() {
        super(Hoteles.class);
    }

    public Hoteles getHoteles(){
        if(hoteles == null){
            hoteles = new Hoteles();
        }
        return this.hoteles;
    }

    public void setHoteles(Hoteles hoteles){
        this.hoteles = hoteles;
    }

    public LinkedList<Hoteles> getListAll(){
        if(listAll == null){
            this.listAll =  listAll();
        }
        return this.listAll;
    }

    public Boolean save() throws Exception {
        Integer idHotel = getListAll().getSize() + 1;
        hoteles.setIdHotel(idHotel);
        this.persist(this.hoteles);
        this.listAll = listAll();
        return true;
    }

    
}
