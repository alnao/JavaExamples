package it.alnao.aws.managerfx.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Indice dei tag delle risorse di una regione, costruito con la Resource Groups Tagging API.
 * Le risorse sono indicizzate per "servizio|risorsa" ricavati dall'ARN
 * (arn:partition:servizio:regione:account:risorsa), così si possono cercare anche
 * risorse di cui l'SDK restituisce solo nome o id (tabelle DynamoDB, code SQS, parametri SSM, API Gateway).
 * Le risorse senza tag non compaiono nell'indice: per loro viene restituita una mappa vuota.
 *
 * @author AlNao
 */
public class TagIndex {

    private final Map<String, Map<String, String>> byKey = new HashMap<>();
    private final boolean available;
    private final String error;

    public TagIndex() {
        this.available = true;
        this.error = null;
    }

    private TagIndex(String error) {
        this.available = false;
        this.error = error;
    }

    /** Indice non disponibile (es. permesso tag:GetResources mancante): tutte le ricerche restituiscono {@link DefaultTags#ERROR} */
    public static TagIndex unavailable(String error) {
        return new TagIndex(error);
    }

    public boolean isAvailable() {
        return available;
    }

    public String getError() {
        return error;
    }

    public void put(String arn, Map<String, String> tags) {
        String key = key(arn);
        if (key != null) {
            byKey.put(key, tags);
        }
    }

    /** Tag della risorsa con l'ARN indicato */
    public Map<String, String> byArn(String arn) {
        if (!available) {
            return DefaultTags.ERROR;
        }
        String key = key(arn);
        return key == null ? Map.of() : byKey.getOrDefault(key, Map.of());
    }

    /**
     * Tag della risorsa indicata da servizio e parte "risorsa" dell'ARN,
     * es. ("dynamodb", "table/clienti") oppure ("apigateway", "/restapis/abc123").
     */
    public Map<String, String> byResource(String service, String resource) {
        if (!available) {
            return DefaultTags.ERROR;
        }
        return byKey.getOrDefault(service + "|" + resource, Map.of());
    }

    private static String key(String arn) {
        if (arn == null) {
            return null;
        }
        String[] parts = arn.split(":", 6);
        return parts.length == 6 ? parts[2] + "|" + parts[5] : null;
    }
}
