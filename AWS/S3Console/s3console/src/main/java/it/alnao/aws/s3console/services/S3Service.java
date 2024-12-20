package it.alnao.aws.s3console.services;


import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import it.alnao.aws.s3console.utils.*;
import it.alnao.aws.s3console.view.ProgressCallback;
import it.alnao.aws.s3console.view.TransferProgress;

public class S3Service {
    private S3Client s3Client;
    private String currentProfile;
    private String currentBucket;
    private String currentPrefix = "";
    private final ExecutorService executorService;
    private final List<S3EventListener> listeners;
    private Region currentRegion;

    public S3Service() {
        this.executorService = Executors.newCachedThreadPool();
        this.listeners = new ArrayList<>();
        this.currentRegion = Region.EU_WEST_1; // Default region
    }

    public void initialize(String profileName, Region region) {
        changeProfile(profileName, region);
    }

    public void changeProfile(String profileName) {
        changeProfile(profileName, this.currentRegion);
    }

    public void changeProfile(String profileName, Region region) {
        CompletableFuture.runAsync(() -> {
            try {
                // Chiudi il client esistente se presente
                if (s3Client != null) {
                    s3Client.close();
                }

                ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
                    .profileName(profileName)
                    .build();

                this.s3Client = S3Client.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .build();

                this.currentProfile = profileName;
                this.currentRegion = region;
                this.currentBucket = null;
                this.currentPrefix = "";
                
                // Notifica il cambio di profilo
                notifyListeners(new S3Event(S3EventType.PROFILE_CHANGED));

                // Ottieni la lista dei bucket direttamente
                try {
                    ListBucketsResponse response = s3Client.listBuckets();
                    List<String> buckets = response.buckets().stream()
                        .map(Bucket::name)
                        .collect(Collectors.toList());
                    
                    // Notifica i bucket trovati
                    SwingUtilities.invokeLater(() -> 
                        notifyListeners(new S3Event(S3EventType.BUCKETS_LISTED, buckets)));
                } catch (Exception e) {
                    handleError("Failed to list buckets", e);
                }

            } catch (Exception e) {
                handleError("Failed to change AWS profile", e);
                e.printStackTrace();
            }
        }, executorService);
    }

    public List<String> getAvailableProfiles() {
        try {
            return software.amazon.awssdk.profiles.ProfileFile
                .defaultProfileFile()
                .profiles()
                .keySet()
                .stream()
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new S3CommanderException("Failed to load AWS profiles", e);
        }
    }

    public CompletableFuture<List<String>> listBuckets() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ListBucketsResponse response = s3Client.listBuckets();
                return response.buckets().stream()
                    .map(Bucket::name)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                throw new S3CommanderException("Failed to list buckets", e);
            }
        }, executorService);
    }


    public void changeBucket(String bucketName) {
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Inizio cambio bucket a: " + bucketName);
                
                // Verifica che il bucket esista
                HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
                s3Client.headBucket(headBucketRequest);

                // Aggiorna lo stato
                this.currentBucket = bucketName;
                this.currentPrefix = "";
                
                // Notifica il cambio bucket
                notifyListeners(new S3Event(S3EventType.BUCKET_CHANGED));
                
                // Lista i contenuti del nuovo bucket
                listObjects();
                
                System.out.println("Completato cambio bucket a: " + bucketName);
            } catch (NoSuchBucketException e) {
                handleError("Bucket not found: " + bucketName, e);
            } catch (Exception e) {
                handleError("Failed to change bucket", e);
            }
        }, executorService);
    }

    // Rimuoviamo selectBucket dato che ora abbiamo changeBucket
    public void selectBucket(String bucketName) {
        changeBucket(bucketName);
    }

    public void listObjects() {
        listObjects(currentPrefix);
    }

    public void listObjects(String prefix) {
        CompletableFuture.runAsync(() -> {
            try {
                ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(currentBucket)
                    .prefix(prefix)
                    .delimiter("/")
                    .build();

                ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(request);
                List<S3Object> objects = new ArrayList<>();

                for (ListObjectsV2Response response : responses) {
                    // Aggiungi le cartelle (CommonPrefixes)
                    response.commonPrefixes().forEach(commonPrefix -> {
                        objects.add(convertCommonPrefixToS3Object(commonPrefix));
                    });

                    // Aggiungi i file
                    objects.addAll(response.contents());
                }

                currentPrefix = prefix;
                SwingUtilities.invokeLater(() -> 
                    notifyListeners(new S3Event(S3EventType.FILES_REFRESHED, objects)));

            } catch (Exception e) {
                handleError("Failed to list objects", e);
            }
        }, executorService);
    }

    private S3Object convertCommonPrefixToS3Object(CommonPrefix commonPrefix) {
        return S3Object.builder()
            .key(commonPrefix.prefix())
            .size(0L)
            .lastModified(java.time.Instant.now())
            .build();
    }

    public CompletableFuture<Void> uploadObject(File file, ProgressCallback progressCallback) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = currentPrefix + file.getName();
                
                // Creiamo la richiesta di upload
                PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(currentBucket)
                    .key(key)
                    .build();

                // Creiamo un RequestBody con monitoraggio del progresso
                RequestBody requestBody = RequestBody.fromFile(file);
                
                // Eseguiamo l'upload
                s3Client.putObject(request, requestBody);
                
                // Notifichiamo il completamento
                notifyListeners(new S3Event(S3EventType.FILE_UPLOADED));
                listObjects();
                
            } catch (Exception e) {
                handleError("Failed to upload file", e);
            }
        }, executorService);
    }

    public CompletableFuture<Void> uploadObject(File file) {
        return uploadObject(file, progress -> {});
    }

    public CompletableFuture<Void> downloadFile(String key, File destination, ProgressCallback progressCallback) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Prima otteniamo i metadati dell'oggetto per conoscere la dimensione totale
                HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(currentBucket)
                    .key(key)
                    .build();

                HeadObjectResponse headResponse = s3Client.headObject(headRequest);
                long totalSize = headResponse.contentLength();

                // Creiamo la richiesta di download
                GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(currentBucket)
                    .key(key)
                    .build();

                // Configuriamo un ResponseTransformer personalizzato per monitorare il progresso
                ResponseTransformer<Object, Object> responseTransformer = ResponseTransformer.toFile(destination.toPath());

                // Eseguiamo il download con monitoraggio del progresso
                s3Client.getObject(request);
                
                // Notifichiamo il completamento del download
                notifyListeners(new S3Event(S3EventType.FILE_DOWNLOADED));
                
            } catch (Exception e) {
                handleError("Failed to download file", e);
            }
        }, executorService);
    }

    public CompletableFuture<Void> downloadObject(S3Object s3Object , File destination) {
        return CompletableFuture.runAsync(() -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(s3Object.key()));
                
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    
                    downloadFile(s3Object.key(), destination, progress -> {
                        notifyListeners(new S3Event(S3EventType.TRANSFER_PROGRESS, 
                            new TransferProgress(progress, s3Object.size(), s3Object.key() )));
                        //TODO 100=s3Object.size()
                    }).get();
                }
            } catch (Exception e) {
                handleError("Failed to download file", e);
            }
        }, executorService);
    }

    public CompletableFuture<Void> deleteObject(S3Object s3Object) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (s3Object.key().endsWith("/")) {
                    deletePrefix(s3Object.key());
                } else {
                    DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(currentBucket)
                        .key(s3Object.key())
                        .build();

                    s3Client.deleteObject(request);
                }
                
                notifyListeners(new S3Event(S3EventType.FILE_DELETED));
                listObjects();
            } catch (Exception e) {
                handleError("Failed to delete object", e);
            }
        }, executorService);
    }

    private void deletePrefix(String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
            .bucket(currentBucket)
            .prefix(prefix)
            .build();

        ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(listRequest);
        responses.forEach(response -> {
            response.contents().forEach(object -> {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(currentBucket)
                    .key(object.key())
                    .build();
                s3Client.deleteObject(deleteRequest);
            });
        });
    }

    public void createFolder(String folderName) {
        CompletableFuture.runAsync(() -> {
            try {
                String key = currentPrefix + folderName + "/";
                PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(currentBucket)
                    .key(key)
                    .build();

                s3Client.putObject(request, RequestBody.empty());
                
                notifyListeners(new S3Event(S3EventType.FOLDER_CREATED));
                listObjects();
            } catch (Exception e) {
                handleError("Failed to create folder", e);
            }
        }, executorService);
    }

    public void addListener(S3EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(S3EventListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(S3Event event) {
        for (S3EventListener listener : listeners) {
            listener.onS3Event(event);
        }
    }

    private void handleError(String message, Exception e) {
        S3CommanderException exception = new S3CommanderException(message, e);
        SwingUtilities.invokeLater(() -> 
            notifyListeners(new S3Event(S3EventType.ERROR_OCCURRED, exception)));
    }

    public String getCurrentBucket() {
        return currentBucket;
    }

    public String getCurrentPrefix() {
        return currentPrefix;
    }

    public void shutdown() {
        executorService.shutdown();
        if (s3Client != null) {
            s3Client.close();
        }
    }
}