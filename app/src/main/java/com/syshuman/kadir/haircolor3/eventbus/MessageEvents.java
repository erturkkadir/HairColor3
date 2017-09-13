package com.syshuman.kadir.haircolor3.eventbus;

import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by kadir on 2017-08-26.
 */

public class MessageEvents {

    private MessageEvents() {
        throw new UnsupportedOperationException("This class is non-instantiable");
    }

    public static class onGetColor {
        public JSONObject data;
        public int zone;
        public onGetColor(JSONObject data, int zone) {
            this.data = data;
            this.zone = zone;
        }
    }

    public static class onGetRecipe {
        public String recipe;

        public onGetRecipe(String recipe) {
            this.recipe = recipe;
        }
    }

    public static class onTrainedData {
        public JSONArray jsonArray;
        public onTrainedData(JSONArray jsonArrays) {
            this.jsonArray = jsonArrays ;
        }
    }


    public static class onConnected {
        public BluetoothLeUart uart;
        public onConnected(BluetoothLeUart uart) {
            this.uart = uart;
        }
    }

    public static class onDeviceFound {
        public onDeviceFound() {

        }
    }

    public static class onDisconnect {
        public onDisconnect() {
        }
    }

    public static class onTrainingComplete {
        public String message;
        public onTrainingComplete(String message) {
            this.message = message;
        }
    }
}
