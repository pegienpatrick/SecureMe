package SecureMe.Entities;

import SecureMe.Repositories.SFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

@Entity
public class FileUploads {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long Id;


//    @Autowired
//    private SFileRepository filesRepository;




    @Column(nullable = false)
    private Long uploadOwner;

    @Column(nullable = false)
    private String uploadName;

    @Column(nullable = false)
    private Date uploadDate;

    @Column(nullable = false)
    private Long FileRef;

    public Optional<SFile> getSFile()
    {
        //return filesRepository.findById(FileRef);
        return Optional.empty();
    }


    /**
     * get field @Id
     @GeneratedValue(strategy= GenerationType.AUTO)

      *
      * @return Id @Id
     @GeneratedValue(strategy= GenerationType.AUTO)

     */
    public Long getId() {
        return this.Id;
    }

    /**
     * set field @Id
     @GeneratedValue(strategy= GenerationType.AUTO)

      *
      * @param Id @Id
     @GeneratedValue(strategy= GenerationType.AUTO)

     */
    public void setId(Long Id) {
        this.Id = Id;
    }




    /**
     * get field @Column(nullable = false)
     *
     * @return uploadOwner @Column(nullable = false)

     */
    public Long getUploadOwner() {
        return this.uploadOwner;
    }

    /**
     * set field @Column(nullable = false)
     *
     * @param uploadOwner @Column(nullable = false)

     */
    public void setUploadOwner(Long uploadOwner) {
        this.uploadOwner = uploadOwner;
    }

    /**
     * get field @Column(nullable = false)
     *
     * @return uploadName @Column(nullable = false)

     */
    public String getUploadName() {
        return this.uploadName;
    }

    /**
     * set field @Column(nullable = false)
     *
     * @param uploadName @Column(nullable = false)

     */
    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    /**
     * get field @Column(nullable = false)
     *
     * @return uploadDate @Column(nullable = false)

     */
    public Date getUploadDate() {
        return this.uploadDate;
    }

    /**
     * set field @Column(nullable = false)
     *
     * @param uploadDate @Column(nullable = false)

     */
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * get field @Column(nullable = false)
     *
     * @return FileRef @Column(nullable = false)

     */
    public Long getFileRef() {
        return this.FileRef;
    }

    /**
     * set field @Column(nullable = false)
     *
     * @param FileRef @Column(nullable = false)

     */
    public void setFileRef(Long FileRef) {
        this.FileRef = FileRef;
    }
}
