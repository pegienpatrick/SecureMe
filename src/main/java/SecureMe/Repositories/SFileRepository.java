package SecureMe.Repositories;

import SecureMe.Entities.SFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SFileRepository extends JpaRepository<SFile,Long> {
    @Query("SELECT CASE WHEN COUNT(sf) > 0 THEN true ELSE false END FROM FileUploads fu JOIN SFile sf ON fu.FileRef = sf.Id WHERE sf.fileHash = ?1")
    boolean contains(String fileHash);

    @Query("SELECT sf FROM FileUploads fu JOIN SFile sf ON fu.FileRef = sf.Id WHERE sf.fileHash = ?1")
    Optional<SFile> findByFileHash(String fileHash);

    @Query("SELECT CASE WHEN COUNT(sf) > 0 THEN true ELSE false END FROM FileUploads fu JOIN SFile sf ON fu.FileRef = sf.Id WHERE sf.fileHash = ?1 AND fu.uploadOwner = ?2")
    boolean containsByFileHashAndUploadOwner(String fileHash, Long uploadOwner);

    @Query("SELECT sf FROM FileUploads fu JOIN SFile sf ON fu.FileRef = sf.Id WHERE sf.fileHash = ?1 AND fu.uploadOwner = ?2")
    Optional<SFile> findByFileHashAndUploadOwner(String fileHash, Long uploadOwner);


    public Optional<SFile> findById(Long fileRef);
}
