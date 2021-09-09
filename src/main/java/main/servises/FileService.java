package main.servises;

import lombok.Data;
import main.Constants;
import main.exceptions.UnableToUploadFileException;
import main.repositories.PostRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

    @Value("${upload.dir}")
    private String resourcesPathName;
    @Value("${max.file.size}")
    private int maxFileSize;
    @Value("${store.photo.width}")
    private int photoWidth;

    @Autowired
    private PostRepository postRepository;

    private String uploadPathName;
    private MultipartFile file;
    private String newFileName;

    public void uploadFile() {

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
                    Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
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
            Files.createDirectories(Paths.get(resourcesPathName + uploadPathName + newDirName));
            newFileName = uploadPathName + newDirName + "/" + fileName;
            return true;

        } catch(IOException ex) {
            throw new UnableToUploadFileException(Constants.FILE_UPLOAD_ERROR);
        }
    }

    public void resizeImageFile() {

        String extension = newFileName.substring(newFileName.lastIndexOf(".") + 1);

        try{
            BufferedImage image = ImageIO.read(new File(resourcesPathName + newFileName));

            int height = image.getHeight();
            int width = image.getWidth();
            int x, y, size;

            if(height > width) {
                System.out.println(1);
                x = 0;
                y = (height - width) / 2;
                size = width;
            } else {
                System.out.println(2);
                y = 0;
                x = (width - height) / 2;
                size = height;
            }

            BufferedImage croppedImage = Scalr.crop(image, x, y, size, size);
            BufferedImage resizedImage = Scalr.resize(croppedImage, photoWidth);
            ImageIO.write(resizedImage, extension, new File(resourcesPathName + newFileName));

        } catch (Exception ex) {
            throw new UnableToUploadFileException(Constants.RESIZE_FILE_ERROR);
        }
    }

}
