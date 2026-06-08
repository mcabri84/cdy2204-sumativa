package cl.duoc.cdy2204.service;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3GuiaService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3GuiaService(
            @Value("${aws.region}") String awsRegion,
            @Value("${aws.s3.bucket}") String bucketName
    ) {
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public void subirArchivo(Path archivoLocal, String rutaS3) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(rutaS3)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromFile(archivoLocal));
    }

    public byte[] descargarArchivo(String rutaS3) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(rutaS3)
                .build();

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
        return response.asByteArray();
    }

    public void eliminarArchivo(String rutaS3) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(rutaS3)
                .build();

        s3Client.deleteObject(request);
    }

    public boolean existeArchivo(String rutaS3) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(rutaS3)
                    .build();

            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    public String generarRutaS3(String fecha, String transportista, String archivoNombre) {
        return fecha + "/" + limpiarNombre(transportista) + "/" + archivoNombre;
    }

    private String limpiarNombre(String valor) {
        return valor.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
