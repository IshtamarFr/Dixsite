package fr.ishtamar.starter.filetransfer;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
	@Value("${fr.ishtamar.starter.files-upload}")
	private String filesUpload;

	@Value("${fr.ishtamar.starter.cropped-size}")
	private int croppedSize;

	public FileUploadResponse saveFile(MultipartFile multipartFile) throws Exception {
		String filename = multipartFile.getOriginalFilename();
		String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf(".") + 1);

		if (extension.equalsIgnoreCase("jpg")
				|| extension.equalsIgnoreCase("jpeg")
				|| extension.equalsIgnoreCase("png")
				|| extension.equalsIgnoreCase("gif")
		) {
			return saveImage(multipartFile);
		} else if (extension.equalsIgnoreCase("mp4")
				|| extension.equalsIgnoreCase("avi")
				|| extension.equalsIgnoreCase("mov")
		) {
			return saveVideo(multipartFile);
		} else {
			throw new GenericException("This file is neither a picture nor a video");
		}
	}

	private FileUploadResponse saveImage(MultipartFile multipartFile) throws Exception {
		FileUploadResponse response=new FileUploadResponse();
		Optional<Date> dateExif= getExif(multipartFile.getInputStream());
        dateExif.ifPresent(response::setDateTimeExif);

		int orientation=getOrientation(multipartFile.getInputStream());
		response.setFileCodeExt(saveJpgFile(multipartFile.getInputStream(),orientation));
		return response;
	}

	private Optional<Date> getExif(InputStream inputStream){
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			Date dateExif = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

			return Optional.of(dateExif);
		}catch(Exception e){
			log.warn(e.toString());
			return Optional.empty();
		}
	}

	private int getOrientation(InputStream inputStream){
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
			Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

			return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		}catch(Exception e){
			log.warn(e.toString());
			return 1;
		}
	}

	private String saveJpgFile(InputStream inputStream,int orientation) throws IOException {
		try {
			//Prepare folder and fileCode
			String fileCode = RandomStringUtils.randomAlphanumeric(15);
			Path uploadPath = Paths.get(filesUpload);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			//Copy image
			Path filePath = uploadPath.resolve(fileCode + ".jpg");
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

			//Get image and orientation
			File file = new File(filesUpload + "/" + fileCode + ".jpg");
			FileInputStream fis = new FileInputStream(file);
			BufferedImage originalImage = ImageIO.read(fis);
			fis.close();

			//Cropped Image
			int height = originalImage.getHeight();
			int width = originalImage.getWidth();
			int target=Math.min(height, width);
			int center=croppedSize/2;

			BufferedImage croppedImage = originalImage.getSubimage((width-target)/2,(height-target)/2,target,target);

			//Resized image
			BufferedImage resizedImage = new BufferedImage(croppedSize, croppedSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = resizedImage.createGraphics();
			if (orientation==3) g.rotate(Math.toRadians(180),center,center);
			if (orientation==6) g.rotate(Math.toRadians(90),center,center);
			if (orientation==8) g.rotate(Math.toRadians(270),center,center);
			g.drawImage(croppedImage, 0, 0, croppedSize, croppedSize, null);
			g.dispose();
			ImageIO.write(resizedImage,"jpg",new File(filesUpload + "/crop-" + fileCode + ".jpg"));

			//Conclusion
			return fileCode + ".jpg";

		} catch (IOException ioe) {
			throw new IOException("Could not save file", ioe);
		}
	}

	private FileUploadResponse saveVideo(MultipartFile multipartFile) throws Exception {
		//Prepare folder and fileCode
		String filename = multipartFile.getOriginalFilename();
		String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));

		String fileCode = RandomStringUtils.randomAlphanumeric(15);
		Path uploadPath = Paths.get(filesUpload);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		Path filePath = uploadPath.resolve(fileCode + extension);
		Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		return FileUploadResponse.builder()
				.fileCodeExt(fileCode + extension)
				.build();
	}

	public void deletePicvidFromFS(String fileCode) {
		if (FileUtils.deleteQuietly(new File(filesUpload + "/" + fileCode)))
            log.info("crop-{} has been successfully deleted from user action", fileCode);
		if (FileUtils.deleteQuietly(new File(filesUpload + "/crop-" + fileCode)))
            log.info("{} has been successfully deleted from user action", fileCode);
	}
}
