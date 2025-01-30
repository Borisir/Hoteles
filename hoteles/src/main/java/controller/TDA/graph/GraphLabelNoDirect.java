package controller.TDA.graph;
import controller.TDA.list.ListEmptyException;


public class GraphLabelNoDirect<E> extends GraphLableDirect<E> {
    public GraphLabelNoDirect(Integer nro_vertices, Class<E> clazz) {
        super(nro_vertices, clazz);
    }

    public void insertEdge(E v1, E v2, Float weigth) throws Exception {
        if (isLabelsGraph()) {
            add_edge(getVerticeL(v1), getVerticeL(v2), weigth);
            add_edge(getVerticeL(v1), getVerticeL(v2), weigth);
        } else {
            throw new Exception("Grafo no etiquetado");
        }
    }

    public Float getWigth2(Integer v1, Integer v2) throws Exception {
        return wieght_edge(v1, v2);
    }
}

