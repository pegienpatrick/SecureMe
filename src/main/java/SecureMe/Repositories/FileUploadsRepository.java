package SecureMe.Repositories;

import SecureMe.Entities.FileUploads;
import SecureMe.Entities.SFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileUploadsRepository extends JpaRepository<FileUploads,Long> {
    @Query("SELECT fu FROM FileUploads fu JOIN SFile sf ON fu.FileRef = sf.Id WHERE sf.fileHash = ?1 AND fu.uploadOwner = ?2")
    Optional<FileUploads> findByFileHashAndUploadOwner(String fileHash, Long uploadOwner);

    @Query("select fu from FileUploads fu where fu.uploadOwner=?1 order by fu.uploadDate desc")
    Optional<List<FileUploads>> findByOwner(Long owner);

    @Query("SELECT fu FROM FileUploads fu JOIN SFile sf ON fu.FileRef = sf.Id WHERE fu.id = ?1 AND fu.uploadOwner = ?2")
    Optional<FileUploads> findByFileOwner(Long uploadid, Long uploadOwner);


}
