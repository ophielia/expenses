package meg.swapout.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {


    /**
     * Write a file, containing the given text, to the filesystem.
     * <br>
     * filename must represent the complete path together with the files name.<br>
     * <b>Example:</b> "C:\projects\test.txt" would be a valid filename if working on a
     * Windows environment.<br>
     *
     * @param filename Name of the file to write
     * @param text     String to write into the file
     * @return True if succesful, false if not.
     */
    static public boolean writeStringToFile(String filename, String text) throws IOException {
        FileWriter fWriter;

            fWriter = new FileWriter(filename);
        try {
            fWriter.write(text);
            fWriter.close();
        } catch (IOException e) {
            return false;
        } finally {
            fWriter.close();
        }

        return true;
    }

    static public boolean writeMultipartToTempFile(MultipartFile file, File tempFile) throws IOException {
        FileOutputStream fos= new FileOutputStream(tempFile);
        try {
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            fos.close();
        }
        return true;
    }

}
