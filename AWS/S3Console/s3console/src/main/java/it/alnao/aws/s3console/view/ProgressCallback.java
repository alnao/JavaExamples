package it.alnao.aws.s3console.view;

/**
 * Interfaccia funzionale per il monitoraggio del progresso delle operazioni di trasferimento
 */
@FunctionalInterface
public interface ProgressCallback {
    /**
     * Metodo chiamato durante il trasferimento per aggiornare il progresso
     * 
     * @param bytesTransferred il numero di byte trasferiti finora
     */
    void onProgress(long bytesTransferred);
}