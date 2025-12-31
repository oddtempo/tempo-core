package com.tempo.core.shared.infrastructure.error;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.ProblemDetail;

import java.io.IOException;
import java.util.Map;

/**
 * Custom serializer for {@link ProblemDetail} to provide a minimal and
 * consistent
 * JSON format for the frontend.
 * <p>
 * Format:
 * 
 * <pre>
 * {
 *   "status": 400,
 *   "error": "Validation Error",
 *   "message": "Validation failed",
 *   "errors": { "field": "message" },
 *   "errorCode": "BUSINESS_CODE"
 * }
 * </pre>
 * </p>
 */
public class ProblemDetailMinimalSerializer extends JsonSerializer<ProblemDetail> {

    @Override
    public void serialize(ProblemDetail pd, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        // 1. Core status code
        gen.writeNumberField("status", pd.getStatus());

        // 2. Error title (shorthand "error")
        gen.writeStringField("error", pd.getTitle());

        // 3. Detailed message
        if (pd.getDetail() != null) {
            gen.writeStringField("message", pd.getDetail());
        }

        // 4. Dynamic properties (errors, errorCode, etc.)
        Map<String, Object> properties = pd.getProperties();
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        gen.writeEndObject();
    }
}
