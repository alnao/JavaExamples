package it.alnao.aws.s3console.utils;

// S3EventType.java
public enum S3EventType {
    PROFILE_CHANGED,      // Quando viene cambiato il profilo AWS
    BUCKETS_LISTED,      // Quando viene completata la lista dei bucket dopo un cambio profilo
    BUCKET_CHANGED,       // Quando viene selezionato un nuovo bucket
    FILES_REFRESHED,      // Quando la lista dei file viene aggiornata
    FILE_UPLOADED,        // Quando un file viene caricato con successo
    FILE_DOWNLOADED,      // Quando un file viene scaricato con successo
    FILE_DELETED,         // Quando un file viene eliminato
    FILE_RENAMED,         // Quando un file viene rinominato
    FOLDER_CREATED,       // Quando viene creata una nuova cartella
    ERROR_OCCURRED,       // Quando si verifica un errore
    TRANSFER_PROGRESS     // Per monitorare il progresso dei trasferimenti
}