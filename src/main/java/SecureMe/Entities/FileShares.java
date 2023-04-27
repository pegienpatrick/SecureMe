package SecureMe.Entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class FileShares {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "SecureMe.utils.UUIDGenerator")
    private String Id;

    @Column(nullable = false)
    private Long owner;

    @ElementCollection()
    private List<FileUploads> filesShared;

    @Column(nullable = true)
    private String authHash;

}
