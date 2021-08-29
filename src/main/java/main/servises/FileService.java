package main.servises;

import lombok.Data;
import main.Constants;
import main.exceptions.UnableToUploadFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Data
@Service
public class FileService {

    @Value("${resources.path}")
    private String resourcesPathName;
    @Value("${max.file.size}")
    private int maxFileSize;

    private String uploadPathName;
    private MultipartFile file;
    private String newFileName;

    public void uploadFile() throws Exception{

        if (!file.isEmpty()) {
            if(file.getSize() > maxFileSize) {
                throw new UnableToUploadFileException(Constants.FILE_SIZE_ERROR);
            }
            if(!(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))) {
                throw new UnableToUploadFileException(Constants.WRONG_IMAGE_TYPE);
            }

            if(makeNewFilePath()) {
                Path uploadFilePath = Paths.get(resourcesPathName + newFileName);

                try (InputStream inputStream = file.getInputStream()) {
                    System.out.println(uploadFilePath);

                    Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
                    System.out.println("exception line 48 uploadFile");
                    throw new UnableToUploadFileException(Constants.FILE_UPLOAD_ERROR);
                }
            }
        } else {
            throw new UnableToUploadFileException(Constants.FILE_MISSING_ERROR);
        }
    }

    public boolean makeNewFilePath() {

        String originName = file.getOriginalFilename();
        String extension = originName.substring(originName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newDirName = "/" + uuid.substring(0, 3) + "/" + uuid.substring(3, 6) + "/" + uuid.substring(6, 9);
        String fileName = uuid.substring(9, 18) + extension;

        try {
            System.out.println(resourcesPathName + uploadPathName + newDirName);
            Files.createDirectories(Paths.get(resourcesPathName + uploadPathName + newDirName));
            newFileName = uploadPathName + newDirName + "/" + fileName;
            return true;

        } catch(IOException ex) {
            System.out.println("exception line 71 makeNewFilePath");
            throw new UnableToUploadFileException(Constants.FILE_UPLOAD_ERROR);
        }
    }

}
