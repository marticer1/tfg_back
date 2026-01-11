package com.tfg.backend.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.Color;
import java.io.IOException;

public class ColorSerializer extends JsonSerializer<Color> {
    @Override
    public void serialize(Color color, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (color == null) {
            gen.writeNull();
        } else {
            String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            gen.writeString(hex);
        }
    }
}
