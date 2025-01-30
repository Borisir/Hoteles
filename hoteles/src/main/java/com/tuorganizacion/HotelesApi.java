package com.tuorganizacion;


import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controller.TDA.graph.GraphLabelNoDirect;
import controller.TDA.list.LinkedList;
import controller.dao.servicies.HotelesServices;
import controller.dao.HotelesDao;
import models.Hoteles;
@Path("/hoteles")
public class HotelesApi {
    @Path("/lista")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospital() {
        HashMap<String, Object> map = new HashMap<>();
        HotelesServices ps = new HotelesServices();
        map.put("msg", "Lista de hospitales");
        map.put("data", ps.listAll().toArray());
        if (ps.listAll().isEmpty()) {
            map.put("data", new Object[]{});
        }
        return Response.ok(map).build();		
    }

@Path("/save")
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response save(HashMap<String, Object> map) {
    HashMap<String, Object> res = new HashMap<>();

    HotelesServices ps = new HotelesServices();
    try {
        ps.getHoteles().setNombre(map.get("nombre").toString());
        ps.getHoteles().setHabitaciones(map.get("habitaciones").toString());
        ps.getHoteles().setCiudad((map.get("ciudad").toString()));
        ps.getHoteles().setLatitud(Double.parseDouble(map.get("latitud").toString()));
        ps.getHoteles().setLongitud(Double.parseDouble(map.get("longitud").toString()));
        
        ps.save(); 
        res.put("msg", "ok");
        res.put("data", "Guardado con éxito");
        return Response.ok(res).build();
        } catch (Exception ex) {
            res.put("msg", "ERROR");
            res.put("data", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
         }
}

@Path("/creargrafo")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response createGraph() {
    HashMap<String, Object> res = new HashMap<>();
    try {
        HotelesDao hoteldao = new HotelesDao();
        LinkedList<Hoteles> list = hoteldao.getListAll();
        hoteldao.createGraph();
        hoteldao.saveGraph();
        res.put("msg", "ok");
        res.put("data", "Grafo creado con éxito");
        res.put("list", list.toArray());
        return Response.ok(res).build();
    } catch (Exception ex) {
        res.put("msg", "ERROR");
        res.put("data", ex.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
    }
}

@Path("/adyacencias")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response adyacenciass() {
    HashMap<String, Object> res = new HashMap<>();
    try {
        HotelesDao hoteldao = new HotelesDao();
        GraphLabelNoDirect<String> graph = hoteldao.adyacencias();
        hoteldao.saveGraph();
        res.put("msg", "ok");
        res.put("data", "Adyacencias creadas con éxito");
        res.put("graph", graph.toString());
        return Response.ok(res).build();
    } catch (Exception ex) {
        res.put("msg", "ERROR");
        res.put("data", ex.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
    }


}

@Path("mapearlosgrafos")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response mapearlosgrafos() {
    try {
        HotelesDao hoteldao = new HotelesDao();
        JsonObject graph = hoteldao.getGraphData();
        return Response.ok(graph.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
        // Manejo de errores
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }

}

@Path("/misgrafos")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getGraph() {
    HashMap<String, Object> res = new HashMap<>();
    try {
        HotelesDao hoteldao = new HotelesDao();
        JsonArray graph = hoteldao.obtainWeights();
        res.put("msg", "Grafo obtenido exitosamente");
        return Response.ok(graph.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
        res.put("msg", "Error");
        res.put("data", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
    }
}


@Path("/caminocorto/{origen}/{destino}/{algoritmo}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response calcularCaminoCorto(@PathParam("origen") int origen, 
                                     @PathParam("destino") int destino, 
                                     @PathParam("algoritmo") int algoritmo) {
    HashMap<String, Object> res = new HashMap<>();
    try {
        HotelesDao hoteldao = new HotelesDao();    
        JsonArray graph = hoteldao.obtainWeights();   
        String resultado = hoteldao.caminoCorto(origen, destino, algoritmo);     
        res.put("msg", "Camino corto calculado exitosamente");
        res.put("resultado", resultado);
        return Response.ok(res).build();
    } catch (Exception e) {
        res.put("msg", "Error");
        res.put("data", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
    }
}

@Path("/dfs/{origen}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response verificarConectividad(@PathParam("origen") int origen) throws Exception {   
    HashMap<String, Object> res = new HashMap<>();
    HotelesDao hoteldao = new HotelesDao();  // Asegúrate de inicializar bien el DAO
    JsonArray graph = hoteldao.obtainWeights(); // Asegúrate de obtener los pesos del grafo
    String respuesta = hoteldao.dFS(origen);
    try {
        res.put("respuesta", respuesta);
        return Response.ok(res).build();          
        } catch (Exception e) {
                res.put("msg", "Error");
                res.put("data", e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
}

}