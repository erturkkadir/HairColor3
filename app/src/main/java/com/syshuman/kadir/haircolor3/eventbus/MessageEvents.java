package com.syshuman.kadir.haircolor3.eventbus;

import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;

/**
 * Created by kadir on 2017-08-26.
 */

public class MessageEvents {

    private MessageEvents() {
        throw new UnsupportedOperationException("This class is non-instantiable");
    }

    public static class onGetColor {
        public String color;
        public String zone;

        public onGetColor(String color, String zone) {
            this.color = color;
            this.zone = zone;
        }
    }

    public static class onGetRecipe {
        public String recipe;

        public onGetRecipe(String recipe) {
            this.recipe = recipe;
        }
    }

    public static class onTrainData {
        public float[][] data;

        public onTrainData(float[][] data) {
            this.data = data;
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
