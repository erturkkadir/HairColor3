package com.syshuman.kadir.haircolor3.eventbus;

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

}
