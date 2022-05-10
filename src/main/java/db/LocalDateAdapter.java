package db;

import java.lang.reflect.Type;
import java.time.LocalDate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateAdapter implements JsonDeserializer<LocalDate>,JsonSerializer<LocalDate> {
	
	@Override
	public LocalDate deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		int year = jsonObject.get("year").getAsInt();
		int month = jsonObject.get("month").getAsInt();
		int day = jsonObject.get("day").getAsInt();
		return LocalDate.of(year,month,day);
	}

	@Override
	public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jobj = new JsonObject();
		jobj.addProperty("year", src.getYear());
		jobj.addProperty("month", src.getMonthValue());
		jobj.addProperty("day", src.getDayOfMonth());
		return jobj;
	}
}
