package controller.TDA.graph.algoritmos;

import controller.TDA.graph.GraphLabelNoDirect;
import java.util.*;

public class DFS {
    private GraphLabelNoDirect<String> grafo;
    private int origen;

    public DFS(GraphLabelNoDirect<String> grafo, int origen) {
        this.grafo = grafo;
        this.origen = origen;
    }

    public String recorrerGrafo() throws Exception {
        int n = grafo.nro_vertices();
        boolean[] visitados = new boolean[n + 1];  // Marcamos los nodos visitados
        Arrays.fill(visitados, false);

        // Pila para DFS
        Stack<Integer> pila = new Stack<>();
        pila.push(origen);
        visitados[origen] = true;

        // Lista para almacenar el recorrido
        List<Integer> recorrido = new ArrayList<>();

        // DFS
        while (!pila.isEmpty()) {
            int nodoActual = pila.pop();
            recorrido.add(nodoActual);

            // Aquí obtenemos los vecinos del nodo actual
            List<Integer> vecinos = obtenerVecinos(nodoActual);
            for (int vecino : vecinos) {
                if (!visitados[vecino]) {
                    visitados[vecino] = true;
                    pila.push(vecino);
                }
            }
        }

        // Retornamos el recorrido en formato de lista
        return "Recorrido DFS: " + recorrido.toString();
    }

    private List<Integer> obtenerVecinos(int nodo) throws Exception {
        List<Integer> vecinos = new ArrayList<>();
        int n = grafo.nro_vertices();

        // Suponiendo que puedes obtener los vecinos mediante una iteración sobre todos los vértices
        for (int i = 1; i <= n; i++) {
            try {
                // Comprobamos si hay una arista entre 'nodo' y 'i'
                Float peso = grafo.getWigth2(nodo, i);
                if (peso != null && peso > 0) {
                    vecinos.add(i);  // Si existe una arista, 'i' es un vecino de 'nodo'
                }
            } catch (Exception e) {
                // Si no existe una arista, simplemente ignoramos el error
            }
        }

        return vecinos;
    }
}
