package clientes;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;




public class AuxiliarViajesWS {

	// URI del recurso que permite acceder al servicio casa de subastas
	final private String baseURI = "http://localhost:8080/ViajesWS-1.0-SNAPSHOT/servicios2/viajes";
	Client cliente = null;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de la clase
	 * Crea el cliente
	 */
	public AuxiliarViajesWS()  {
		this.cliente = ClientBuilder.newClient();
	}

	/**
	 * Cuando cada cliente cierra su sesion volcamos los datos en el fichero para mantenerlos actualizados
	 */
	public void guardaDatos() {
		Response response = cliente.target(baseURI).path("/guardaDatos")
				.request().put(Entity.text(""));
	}
	/**
	 * Devuelve los viajes disponibles con un origen dado
	 *
	 * @param origen
	 * @return JSONArray de viajes con un origen dado. Vacío si no hay viajes disponibles con ese origen
	 */
	public JSONArray consultaViajes(String origen) throws WebApplicationException {
		Response response = cliente.target(baseURI)
				.path("/consultaViajes")
				.queryParam("origen",origen)
				.request(MediaType.APPLICATION_JSON)
				.get();

		int estado = response.getStatus();
		if ( estado == 200) {
			JSONArray cadenaContacto =response.readEntity(JSONArray.class);
			response.close();
			return cadenaContacto;
		} else if (estado == 404) {
			response.close();
			return new JSONArray();
		} else {
			response.close();
			throw new WebApplicationException("Error detectado al hacer la consulta de Viaje");
		}

	}

	/**
	 * El cliente codcli reserva el viaje codviaje
	 *
	 * @param codviaje
	 * @param codcli
	 * @return JSONObject con la información del viaje. Vacío si no existe o no está disponible
	 * @throws	WebApplicationException
	 */
	public JSONObject reservaViaje(String codviaje, String codcli) {
		Response response = cliente.target(baseURI)
				.path("/reservaViaje/"+codviaje)
				.queryParam("codCli",codcli)
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.text(""));
		return getJsonObject(response);

	}

	/**
	 * El cliente codcli anula su reserva del viaje codviaje
	 *
	 * @param codviaje	codigo del viaje a anular
	 * @param codcli	codigo del cliente
	 * @return	JSON del viaje en que se ha anulado la reserva. JSON vacio si no se ha anulado
	 * @throws	WebApplicationException
	 */
	public JSONObject anulaReserva(String codviaje, String codcli) {
		Response response = cliente.target(baseURI)
				.path("/anulaReserva/"+codviaje)
				.queryParam("codCli",codcli)
				.request(MediaType.APPLICATION_JSON)
				.delete();
		return getJsonObject(response);
	}



	/**
	 * El cliente codcli oferta un Viaje
	 * @param codcli
	 * @param origen
	 * @param destino
	 * @param fecha
	 * @param precio
	 * @param numplazas
	 * @return	JSONObject con los datos del viaje ofertado. Vacio si no se oferta
	 * @throws	WebApplicationException
	 */
	public JSONObject ofertaViaje(String codcli, String origen, String destino, String fecha, long precio, long numplazas) {
		Response response = cliente.target(baseURI)
				.path("/ofertaViaje")
				.queryParam("codCli",	codcli)
				.queryParam("origen",	origen)
				.queryParam("destino",	destino)
				.queryParam("fecha",		fecha)
				.queryParam("precio",	precio)
				.queryParam("numPlazas",	numplazas)
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.text(""));
		return getJsonObject(response);
	}


	/**
	 * El cliente codcli borra un viaje que ha ofertado
	 *
	 * @param codviaje	codigo del viaje a borrar
	 * @param codcli	codigo del cliente
	 * @return	JSONObject del viaje borrado. JSON vacio si no se ha borrado
	 * @throws	WebApplicationException
	 */
	public JSONObject borraViaje(String codviaje, String codcli) {
		Response response = cliente.target(baseURI).path("/borraViaje/"+codviaje)
				.queryParam("codCli",codcli)
				.request(MediaType.APPLICATION_JSON).delete();
		return getJsonObject(response);
	}

	private JSONObject getJsonObject(Response response) {
		int estado = response.getStatus();
		if ( estado == 200) {
			JSONObject cadenaContacto = response.readEntity(JSONObject.class);
			response.close();
			return cadenaContacto;
		} else if (estado == 404) {
			response.close();
			return new JSONObject();
		} else {
			response.close();
			throw new WebApplicationException("Error detectado, estado diferente a 404 ");
		}
	}

} // fin clase
