package it.alnao.aws.managerfx.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Tag standard che ogni risorsa AWS degli esempi deve avere
 * (vedi AWS/README.md, sezione "Convenzione dei tag").
 *
 * @author AlNao
 */
public final class DefaultTags {

    /** Chiavi dei tag standard, nell'ordine in cui vengono mostrate nelle tabelle */
    public static final List<String> KEYS = List.of("Name", "Environment", "Project", "Owner", "CostCenter", "ManagedBy");

    /** Marcatore: non è stato possibile leggere i tag della risorsa (permessi, errore di rete, ...) */
    public static final Map<String, String> ERROR = Collections.unmodifiableMap(new HashMap<>());

    /** Marcatore: il tipo di risorsa non supporta i tag (es. gruppi IAM) */
    public static final Map<String, String> UNSUPPORTED = Collections.unmodifiableMap(new HashMap<>());

    private DefaultTags() {
    }

    /**
     * Converte una lista di tag dell'SDK (ogni servizio ha la sua classe Tag) in una mappa chiave/valore.
     */
    public static <T> Map<String, String> toMap(Collection<T> tags, Function<T, String> key, Function<T, String> value) {
        Map<String, String> map = new HashMap<>();
        if (tags != null) {
            for (T tag : tags) {
                String k = key.apply(tag);
                if (k != null) {
                    map.put(k, value.apply(tag));
                }
            }
        }
        return map;
    }
}
