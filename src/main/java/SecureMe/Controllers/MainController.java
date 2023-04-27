package SecureMe.Controllers;

import SecureMe.Entities.FileUploads;
import SecureMe.Entities.SFile;
import SecureMe.Entities.User;
import SecureMe.Repositories.FileUploadsRepository;
import SecureMe.Repositories.SFileRepository;
import SecureMe.Repositories.UserRepository;
import SecureMe.utils.Blake2b;
import SecureMe.utils.FileUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.core.io.Resource;



@RestController

public class MainController{

	private Blake2b bl=new Blake2b(64);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SFileRepository fileRepository;

	@Autowired
	FileUploadsRepository fileuploads;


	@GetMapping({"","/"})
	public RedirectView home(HttpSession session) {
		if(session.isNew())
			return login();
		else {
			return dashboard();
		}
	}


	@GetMapping("/login")
	public RedirectView login() {
		return new RedirectView("login.html");
	}


	@GetMapping("/Dashboard")
	public RedirectView dashboard() {
		return new RedirectView("Dashboard.html");
	}

	@GetMapping("/test")
	public String test() {
		return "am testing";
	}


	@GetMapping("/example")
    public String example() {
        return "redirect:profile.html";
    }

	@GetMapping("/getUser")
	@ResponseBody
	public String getUser(HttpSession session) {
		JSONObject responseJson = new JSONObject();

		// Check if the user is authorized
		if (session.getAttribute("user") != null) {
			// Get the user object from the session
			User user = (User) session.getAttribute("user");

			// Create a JSON object representing the user
			JSONObject userJson = new JSONObject();
			userJson.put("id", user.getId());
			userJson.put("first_name", user.getFname());
			userJson.put("last_name", user.getLname());
			userJson.put("email", user.getEmail());

			// Add any other properties you want to include in the JSON object

			// Add the user JSON object to the response JSON object
			responseJson.put("status", "success");
			responseJson.put("user", userJson);
		} else {
			// Add an error message to the response JSON object
			responseJson.put("status", "error");
			responseJson.put("message", "User is not authorized");
		}

		// Return the JSON object as a string
		return responseJson.toString();
	}


	@PostMapping("/register")
	public ResponseEntity<JSONObject> register(@RequestParam(value = "authenticationMethod", required = true) String authenticationMethod,
											   @RequestParam(value = "first_name", required = true) String firstName,
											   @RequestParam(value = "last_name", required = true) String lastName,
											   @RequestParam(value = "email", required = true) String email,
											   @RequestParam(value = "password", required = false) String password,
											   @RequestParam(value = "password_repeat", required = false) String passwordRepeat,
											   @RequestParam(value = "file_authentication", required = false) MultipartFile fileAuthentication) {

		//System.out.println("we are here");
		// Check if authentication method is password and passwords don't match
		if (authenticationMethod.equals("password") && !password.equals(passwordRepeat)) {
			// Return error response
			//return "Passwords do not match";
			JSONObject response = new JSONObject();
			response.put("status", "error");
			response.put("message", "Passwords dont match");
			return ResponseEntity.badRequest().body(response);

		}

		User user=new User();

		user.setAuthType(authenticationMethod);
		user.setFname(firstName);
		user.setLname(lastName);
		user.setEmail(email);


		// Handle the registration logic based on authentication method
		if (authenticationMethod.equals("password")) {
			// Handle password authentication
			user.setPasswordHash(bytesToHex(bl.hash(null,password.getBytes())));
			// ...
		} else if (authenticationMethod.equals("file")) {
			// Handle file authentication
			// ...
			try {
				//System.out.println(new String(fileAuthentication.getBytes()));
				user.setPasswordHash(bytesToHex(bl.hash(null,fileAuthentication.getBytes())));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			// Save the user

			userRepository.save(user);

			// Return a success response
			JSONObject response = new JSONObject();
			response.put("status", "success");
			return ResponseEntity.ok(response);

		} catch (DataIntegrityViolationException e) {
			// Handle duplicate email error
			JSONObject response = new JSONObject();
			response.put("status", "error");
			response.put("message", "Email address is already Registered.");
			return ResponseEntity.badRequest().body(response);

		} catch (Exception e) {
			// Handle other errors
			JSONObject response = new JSONObject();
			response.put("status", "error");
			response.put("message", "An error occurred while processing your request.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}


		// Return success response
		// return "Registration successful";
	}

	@PostMapping("/login")
	public ResponseEntity<JSONObject> login(@RequestParam(value = "authenticationMethod", required = true) String authenticationMethod,
											@RequestParam(value = "email", required = true) String email,
											@RequestParam(value = "password", required = false) String password,
											@RequestParam(value = "file_authentication", required = false) MultipartFile fileAuthentication,HttpSession session) {
		User user=userRepository.findByEmail(email);
		if(user==null) {
			JSONObject response = new JSONObject();
			response.put("status", "error");
			response.put("message", "Email address not Found");
			return ResponseEntity.badRequest().body(response);

		} else {
			Blake2b bl=new Blake2b(64);
			if(authenticationMethod.equals("password")) {

				if(bytesToHex(bl.hash(null,password.getBytes())).equals(user.getPasswordHash())) {
					session.setAttribute("user",user);
					JSONObject response = new JSONObject();
					response.put("status", "success");
					return ResponseEntity.ok(response);

				} else{

					JSONObject response = new JSONObject();
					response.put("status", "error");
					response.put("message", "password incorrect");
					return ResponseEntity.badRequest().body(response);

				}
			} else{
				{

					try {
						//System.out.println(new String(fileAuthentication.getBytes()));
						if(bytesToHex(bl.hash(null,fileAuthentication.getBytes())).equals(user.getPasswordHash())) {
							session.setAttribute("user",user);
							JSONObject response = new JSONObject();
							response.put("status", "success");
							return ResponseEntity.ok(response);

						} else{

							JSONObject response = new JSONObject();
							response.put("status", "error");
							response.put("message", "File incorrect");
							return ResponseEntity.badRequest().body(response);

						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}


	}


	@PostMapping("/updateUser")
	public ResponseEntity<JSONObject> updateUser(@RequestParam("first_name") String firstName,
												 @RequestParam("last_name") String lastName,
												 @RequestParam("email") String email,
												 HttpSession session) {

		// Check if user is logged in and has a session attribute of "user"
		if (session.getAttribute("user") == null) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("error", "User not logged in");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseJson);
		}

		// Get the user record from the session
		User user = (User) session.getAttribute("user");

		// Check if email already exists for another user
		User existingUser = userRepository.findByEmail(email);
		if (existingUser != null && !existingUser.getId().equals(user.getId())) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("error", "Email already exists for another user");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
		}

		// Update the user record with the new data
		user.setFname(firstName);
		user.setLname(lastName);
		user.setEmail(email);

		try {
			// Save the updated user record to the database
			User updatedUser = userRepository.save(user);

			// Update the session attribute with the updated user record
			session.setAttribute("user", updatedUser);

			JSONObject responseJson = new JSONObject();
			responseJson.put("success", "User record updated successfully");
			return ResponseEntity.ok(responseJson);
		} catch (Exception e) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("error", "Error updating user record");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseJson);
		}
	}


	@PostMapping("/resetPassword")
	public ResponseEntity<JSONObject> resetPassword(@RequestParam(name = "authentication_method") String authenticationMethod,
                                                @RequestParam(name = "new_password",required=false) String newPassword,
                                                @RequestParam(name = "confirm_password",required=false) String confirmPassword,
                                                @RequestParam(name = "file_authentication", required = false) MultipartFile fileAuthentication,HttpSession session) {

		if (session.getAttribute("user") == null) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("error", "User not logged in");
			responseJson.put("message", "User not logged in");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseJson);
		}
		User user = (User) session.getAttribute("user");
		Blake2b bl=new Blake2b(64);
		if (authenticationMethod.equals("password")) {
			if (!newPassword.equals(confirmPassword)) {
				JSONObject response = new JSONObject();
				response.put("error", "Passwords do not match");
				response.put("message", "Passwords do not match");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			user.setPasswordHash(bytesToHex(bl.hash(null,newPassword.getBytes())));
			user.setAuthType(authenticationMethod);


		} else if (authenticationMethod.equals("file")) {
			// File authentication code here
			// ...
			try {
				user.setPasswordHash(bytesToHex(bl.hash(null,fileAuthentication.getBytes())));
			} catch (IOException e) {

				throw new RuntimeException(e);
			}
			user.setAuthType(authenticationMethod);


		} else {
			JSONObject response = new JSONObject();
			response.put("error", "Invalid authentication method");
			response.put("message", "Invalid authentication method");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			// Save the updated user record to the database
			User updatedUser = userRepository.save(user);

			// Update the session attribute with the updated user record
			session.setAttribute("user", updatedUser);

			JSONObject responseJson = new JSONObject();
			responseJson.put("success", "User Authentication Reset successfully");
			//responseJson.put("message", "User Authentication Reset successfully");
			return ResponseEntity.ok(responseJson);
		} catch (Exception e) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("error", "Error updating user Authentication");
			responseJson.put("message", "Error updating user Authentication");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseJson);
		}


	}


	@PostMapping("/uploadFile")
	public ResponseEntity<JSONObject> uploadFile(@RequestPart("file") MultipartFile file,HttpSession session) {
		// Add your file processing logic here
		// For example, you can save the file to a specific location
		// or perform any other required operations
		if (session.getAttribute("user") == null) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("error", "User not logged in");
			responseJson.put("message", "User not logged in");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseJson);
		}
		User user = (User) session.getAttribute("user");
		Blake2b bl=new Blake2b(64);

		try {
			// Process the file
			// e.g., save it to a specific location
			// file.transferTo(new File("/path/to/save/" + file.getOriginalFilename()));
			String hash=bytesToHex(bl.hash(null, file.getBytes()));
			if(fileRepository.containsByFileHashAndUploadOwner(hash,user.getId())) {
				SFile avail=null;
				if(fileRepository.findByFileHash(hash).isPresent())
					avail=fileRepository.findByFileHash(hash).get();

				assert avail != null;
				File fl=avail.getFile();
				FileUploads fu=fileuploads.findByFileHashAndUploadOwner(hash,user.getId()).get();

				if(FileUtils.filesEqual(file.getBytes(),(new FileInputStream(fl).readAllBytes()))) {
					JSONObject response = new JSONObject();
					response.put("error", "");
					response.put("message", "File is already Uploaded by you as "+fu.getUploadName());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
				}

			} else if(fileRepository.contains(hash)){

				SFile avail=null;
				if(fileRepository.findByFileHash(hash).isPresent())
					avail=fileRepository.findByFileHash(hash).get();

				assert avail != null;

				File fl=avail.getFile();

				FileUploads fu=new FileUploads();
				fu.setUploadDate(new Date());
				fu.setUploadName(file.getOriginalFilename());
				fu.setUploadOwner(user.getId());


				if(FileUtils.filesEqual(file.getBytes(),(new FileInputStream(fl).readAllBytes()))) {

					fu.setFileRef(avail.getId());
					fileuploads.save(fu);

					JSONObject responseJson = new JSONObject();
					responseJson.put("success", "Another Similar File Found and Linked with");
					responseJson.put("message", "Another Similar File Found and Linked with");

					return ResponseEntity.ok(responseJson);

				}
			} else{

				SFile avail=new SFile();
				avail.setFileHash(hash);
				avail.setFilename(file.getOriginalFilename());
				//avail.setResource(file.getBytes());
				SFile tmp=fileRepository.save(avail);
				tmp.setResource(file.getBytes());


				FileUploads fu=new FileUploads();
				fu.setUploadDate(new Date());
				fu.setUploadName(file.getOriginalFilename());
				fu.setUploadOwner(user.getId());


				fu.setFileRef(tmp.getId());
				fileuploads.save(fu);

				JSONObject responseJson = new JSONObject();
				responseJson.put("success", "New File uploaded SuccessFully");
				responseJson.put("message", "New File uploaded SuccessFully");
				return ResponseEntity.ok(responseJson);
			}

			// Return a success message
			//return ResponseEntity.ok("File uploaded successfully");
		} catch (Exception e) {
			// Return an error message
			//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file");
			JSONObject response = new JSONObject();
			response.put("error", "");
			response.put("message", "Error:"+e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		JSONObject response = new JSONObject();
		response.put("error", "");
		response.put("message", "File not received");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}


	@GetMapping("/listFiles")
	public JSONArray listFiles(HttpSession session) {
		if (session.getAttribute("user") == null)
			return null;

		User user=(User) session.getAttribute("user");
		List<FileUploads> fileUploadsList = (List<FileUploads>) fileuploads.findByOwner(user.getId()).get();
		JSONArray jsonArray = new JSONArray();

		for (FileUploads fileUploads : fileUploadsList) {
			Optional<SFile> optionalSFile = fileRepository.findById(fileUploads.getFileRef());
			optionalSFile.ifPresent(sFile -> {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", fileUploads.getId());

				jsonObject.put("uploadName", fileUploads.getUploadName());
				jsonObject.put("uploadDate", fileUploads.getUploadDate());

				//jsonObject.put("filename", sFile.getFilename());
				jsonObject.put("fileHash", sFile.getFileHash());
				jsonObject.put("size", sFile.getSize());


				jsonArray.add(jsonObject);
			});
		}

		return jsonArray;
	}




	@GetMapping("/download")
	public ResponseEntity<Object> downloadFile(@RequestParam("file") Long uploadId, HttpSession session) throws IOException {

		if (session.getAttribute("user") == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		User user = (User) session.getAttribute("user");

		Optional<FileUploads> fileUploadsOptional = fileuploads.findByFileOwner(uploadId, user.getId());
		if (fileUploadsOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		FileUploads fu = fileUploadsOptional.get();
		Optional<SFile> sFileOptional = fileRepository.findById(fu.getFileRef());
		if (sFileOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		SFile sfile = sFileOptional.get();

		// Set the content type and attachment disposition headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fu.getUploadName());

		// Return the file as a ResponseEntity
		File fileResource;
		try {
			fileResource=sfile.getFile();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.ok()
				.headers(headers)
				.body(new FileSystemResource(fileResource));
	}


	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		//System.out.println(sb);
		return sb.toString();
	}


	@GetMapping("/dologout")
	public RedirectView dologout(HttpSession session) {
		
		session.invalidate();
		return new RedirectView("login.html");
	}


	@PostMapping("/my-endpoint")
    public String myEndpoint(@RequestParam String myField) {
        return "Received field value: " + myField;
    }

    
    @RequestMapping({"/error","/404"})
    public RedirectView error()
	{
		return new RedirectView("error404.html");
	}


	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RedirectView handleException() {
        return new RedirectView("error404.html");
    }












}

