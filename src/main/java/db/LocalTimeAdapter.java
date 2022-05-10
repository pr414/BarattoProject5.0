package db;

import java.lang.reflect.Type;
import java.time.LocalTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Classe adattatrice per la serializzazione/deserializzazione
 * avanzata della classe LocalTime.
 */
public class LocalTimeAdapter implements JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {
	
	
	@Override
	public LocalTime deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		int hours = jsonObject.get("hours").getAsInt();
		int minutes = jsonObject.get("minutes").getAsInt();
		int seconds = jsonObject.get("seconds").getAsInt();
		return LocalTime.of(hours,minutes,seconds);
	}

	@Override
	public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jobj = new JsonObject();
		jobj.addProperty("hours", src.getHour());
		jobj.addProperty("minutes", src.getMinute());
		jobj.addProperty("seconds", src.getSecond());
		return jobj;
	}
}
