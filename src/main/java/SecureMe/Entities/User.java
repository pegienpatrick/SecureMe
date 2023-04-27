package SecureMe.Entities;

import javax.persistence.*;
import java.util.List;


@Entity
public class User{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;

	@Column(nullable=false,unique=true)
	private String email;

	@Column(nullable=true)
	private String fname;

	@Column (nullable=true)
	private String lname;

	@Column(nullable=false)
	private String authType;


	@Column(nullable=false)
	private String passwordHash;


	public List<SFile> getFiles(){
		return  null;
	}


	


	/**
	 * get field
	 *
	 * @return Id
	 */
	public Long getId() {
		return this.Id;
	}

	/**
	 * set field
	 *
	 * @param Id
	 */
	public void setId(Long Id) {
		this.Id = Id;
	}

	/**
	 * get field @column(nullable=false,unique=true)
	 *
	 * @return email @column(nullable=false,unique=true)

	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * set field @column(nullable=false,unique=true)
	 *
	 * @param email @column(nullable=false,unique=true)

	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * get field @column(nullable=true)
	 *
	 * @return fname @column(nullable=true)

	 */
	public String getFname() {
		return this.fname;
	}

	/**
	 * set field @column(nullable=true)
	 *
	 * @param fname @column(nullable=true)

	 */
	public void setFname(String fname) {
		this.fname = fname;
	}

	/**
	 * get field @column(nullable=true)
	 *
	 * @return lname @column(nullable=true)

	 */
	public String getLname() {
		return this.lname;
	}

	/**
	 * set field @column(nullable=true)
	 *
	 * @param lname @column(nullable=true)

	 */
	public void setLname(String lname) {
		this.lname = lname;
	}

	/**
	 * get field @column(nullable=false)
	 *
	 * @return authType @column(nullable=false)

	 */
	public String getAuthType() {
		return this.authType;
	}

	/**
	 * set field @column(nullable=false)
	 *
	 * @param authType @column(nullable=false)

	 */
	public void setAuthType(String authType) {
		this.authType = authType;
	}

	/**
	 * get field @column(nullable=false)
	 *
	 * @return passwordHash @column(nullable=false)

	 */
	public String getPasswordHash() {
		return this.passwordHash;
	}

	/**
	 * set field @column(nullable=false)
	 *
	 * @param passwordHash @column(nullable=false)

	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}

