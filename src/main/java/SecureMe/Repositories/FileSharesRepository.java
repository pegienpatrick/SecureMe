package SecureMe.Repositories;

import SecureMe.Entities.FileShares;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileSharesRepository extends JpaRepository<FileShares,Long> {
}
