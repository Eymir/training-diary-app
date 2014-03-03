package myApp.trainingdiary.service;

/**
 * Created by Lenovo on 17.02.14.
 */
public class ResponseData {

    private Long status;
    private MetadataType metadata;
    private TransferData entity;


    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public MetadataType getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataType metadata) {
        this.metadata = metadata;
    }

    public TransferData getEntity() {
        return entity;
    }

    public void setEntity(TransferData entity) {
        this.entity = entity;
    }
}
