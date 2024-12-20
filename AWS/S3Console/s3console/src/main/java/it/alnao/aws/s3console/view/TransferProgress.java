package it.alnao.aws.s3console.view;

/**
 * Classe che rappresenta il progresso di un trasferimento (upload o download)
 */
public class TransferProgress {
    private final long bytesTransferred;
    private final long totalBytes;
    private final String fileName;

    public TransferProgress(long bytesTransferred, long totalBytes, String fileName) {
        this.bytesTransferred = bytesTransferred;
        this.totalBytes = totalBytes;
        this.fileName = fileName;
    }

    /**
     * @return il numero di byte trasferiti
     */
    public long getBytesTransferred() {
        return bytesTransferred;
    }

    /**
     * @return il numero totale di byte da trasferire
     */
    public long getTotalBytes() {
        return totalBytes;
    }

    /**
     * @return il nome del file in trasferimento
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return il progresso come percentuale (da 0 a 1)
     */
    public double getProgress() {
        return totalBytes > 0 ? (double) bytesTransferred / totalBytes : 0;
    }

    /**
     * @return il progresso formattato come percentuale (es: "45.2%")
     */
    public String getFormattedProgress() {
        return String.format("%.1f%%", getProgress() * 100);
    }

    /**
     * @return i byte trasferiti formattati in formato leggibile (es: "1.5 MB")
     */
    public String getFormattedBytesTransferred() {
        return formatBytes(bytesTransferred);
    }

    /**
     * @return il totale dei byte formattato in formato leggibile (es: "10.2 GB")
     */
    public String getFormattedTotalBytes() {
        return formatBytes(totalBytes);
    }

    /**
     * @return una stringa che rappresenta lo stato completo del trasferimento
     */
    @Override
    public String toString() {
        return String.format("%s - %s / %s (%s)", 
            fileName,
            getFormattedBytesTransferred(),
            getFormattedTotalBytes(),
            getFormattedProgress());
    }

    /**
     * Formatta un numero di byte in formato leggibile
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}