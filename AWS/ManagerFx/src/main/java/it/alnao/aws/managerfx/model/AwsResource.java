package it.alnao.aws.managerfx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modello per rappresentare le informazioni di una risorsa AWS generica
 * 
 * @author AlNao
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsResource {
    private String id;
    private String name;
    private String type;
    private String status;
    private String region;
}
