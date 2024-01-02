package tec.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;

public class Connect {
    private String myip;
    private String jsonFilePath = "config.json";
    
    public Connect(){  
        try (FileReader fileReader = new FileReader(jsonFilePath)) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(fileReader).getAsJsonObject();
            
            this.myip = jsonObject.get("ip").getAsString();

        } catch (IOException e) {
            this.myip = null;
        }
    }
    
    public String get_ip() {
        try (FileReader fileReader = new FileReader(jsonFilePath)) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(fileReader).getAsJsonObject();
            
            this.myip = jsonObject.get("ip").getAsString();
            return this.myip;
        } catch (IOException e) {
            this.myip = null;
            return this.myip;
        }
    }
}