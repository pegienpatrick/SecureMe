package SecureMe.Entities;



import org.springframework.core.io.Resource;
import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.ClassPathResource;


@Entity
public class SFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(nullable=false)
    private String filename;

    @Column(nullable=false)
    private String fileHash;


    public void setResource(File file) throws IOException {
        this.filename = "file"+Id;
        Path filePath = Paths.get("uploads", filename);
        Files.copy(file.toPath(), filePath);
    }

    public void setResource(byte[] bytes) throws IOException {
        this.filename = "file" + Id;
        Path filePath = Paths.get("uploads", filename);

        // Create the parent directories if they don't exist
        Files.createDirectories(filePath.getParent());

        // Create the file if it doesn't exist
        if (!Files.exists(filePath)) {
            Files.write(filePath, bytes);
            System.out.println("File created successfully.");
        } else {
            System.out.println("File already exists.");
        }
    }

    public File getFile() throws IOException {
        this.filename = "file"+Id;
        Path filePath = Paths.get("uploads", filename);
        // Load the resource from the classpath (assuming it's in the uploads folder)
        //Resource resource = new ClassPathResource("uploads/" + filename);
        // Write the contents of the resource to a file
        //Files.copy(resource.getInputStream(), filePath);
        return filePath.toFile();
    }

    public Long getSize()  {
        this.filename = "file"+Id;
        Path filePath = Paths.get("uploads", filename);
        return filePath.toFile().length();
    }




    /**
     * get field @Id
     @GeneratedValue(strategy = GenerationType.AUTO)

      *
      * @return Id @Id
     @GeneratedValue(strategy = GenerationType.AUTO)

     */
    public Long getId() {
        return this.Id;
    }

    /**
     * set field @Id
     @GeneratedValue(strategy = GenerationType.AUTO)

      *
      * @param Id @Id
     @GeneratedValue(strategy = GenerationType.AUTO)

     */
    public void setId(Long Id) {
        this.Id = Id;
    }

    /**
     * get field @Column(nullable=false)
     *
     * @return filename @Column(nullable=false)

     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * set field @Column(nullable=false)
     *
     * @param filename @Column(nullable=false)

     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * get field @Column(nullable=false)
     *
     * @return fileHash @Column(nullable=false)

     */
    public String getFileHash() {
        return this.fileHash;
    }

    /**
     * set field @Column(nullable=false)
     *
     * @param fileHash @Column(nullable=false)

     */
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
