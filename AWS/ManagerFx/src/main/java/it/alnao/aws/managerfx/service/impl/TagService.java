package it.alnao.aws.managerfx.service.impl;

import it.alnao.aws.managerfx.model.DefaultTags;
import it.alnao.aws.managerfx.model.TagIndex;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;

/**
 * Servizio per leggere in una sola chiamata (paginata) i tag di tutte le risorse di una regione
 * tramite la Resource Groups Tagging API (permesso IAM richiesto: tag:GetResources).
 */
public class TagService {
    private final ResourceGroupsTaggingApiClient taggingClient;

    public TagService(Region region, String profile) {
        this.taggingClient = ResourceGroupsTaggingApiClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create(profile))
            .build();
    }

    /**
     * Costruisce l'indice dei tag della regione; in caso di errore restituisce un indice "non disponibile"
     * in modo che le tabelle mostrino l'errore invece di segnalare tag mancanti.
     */
    public TagIndex loadTagIndex() {
        try {
            TagIndex index = new TagIndex();
            GetResourcesRequest request = GetResourcesRequest.builder().resourcesPerPage(100).build();
            for (ResourceTagMapping mapping : taggingClient.getResourcesPaginator(request).resourceTagMappingList()) {
                index.put(mapping.resourceARN(), DefaultTags.toMap(mapping.tags(), Tag::key, Tag::value));
            }
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return TagIndex.unavailable(e.getMessage());
        }
    }

    public void close() {
        if (taggingClient != null) {
            taggingClient.close();
        }
    }
}
