buat contoh Project java maven reshapi/api MVC  
perintah get/push/delete
menggunakan gson data plugin tomcat7
dengan koneksi oracle 
db.properties 
db.url=jdbc:oracle:thin:@localhost:1521/ADISXDB.ICONPLN.CO.ID
db.username=USERADISMONLAP
db.password=adis123
dan ada menu untuk ke menampilkan 
table select unitup, nama_unit from  VER_MASTER_UNIT 
dan select kode_bank, nama_bank from  VER_MASTER_BANK 

ChatGPT said:
ChatGPT
Berikut adalah contoh struktur proyek Java Maven REST API sederhana dengan pola MVC menggunakan Gson, plugin Tomcat7, dan koneksi ke Oracle Database.
------------------------------------------------------------------------
Berikut adalah langkah untuk membuat proyek Maven REST API:

1. Buat Proyek Maven Baru
bash
Copy code
mvn archetype:generate -DgroupId=com.example -DartifactId=maven-restapi-mvc -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false

2. Pindah ke Direktori Proyek
bash
Copy code
cd maven-restapi-mvc
3. Tambahkan Dependensi dan Plugin di pom.xml
Tambahkan dependensi seperti gson, ojdbc8, dan Jersey serta konfigurasi plugin Tomcat seperti pada kode sebelumnya.

4. Buat Paket dan Kelas
Buat struktur direktori berikut:

bash
Copy code
mkdir -p src/main/java/com/example/{controller,model,service,util}
Lalu tambahkan file Java untuk model, service, dan controller sesuai dengan kode yang diberikan sebelumnya.

5. Tambahkan Konfigurasi di web.xml
Letakkan file web.xml di src/main/webapp/WEB-INF/.

6. Tambahkan Properti Database
Buat file db.properties di src/main/resources/ dengan konfigurasi database.

7. Jalankan Aplikasi
Build dan deploy menggunakan Tomcat:

bash
Copy code
mvn clean install tomcat7:run
8. Uji Endpoint
Gunakan tools seperti Postman atau browser untuk mengakses:

http://localhost:8080/maven-restapi-mvc/api/units
http://localhost:8080/maven-restapi-mvc/api/banks

--------------------------------------------
1. Struktur Proyek
css
Copy code
maven-restapi-mvc
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com.example.controller
│   │   │   │   ├── BankController.java
│   │   │   │   ├── UnitController.java
│   │   │   ├── com.example.model
│   │   │   │   ├── Bank.java
│   │   │   │   ├── Unit.java
│   │   │   ├── com.example.service
│   │   │   │   ├── BankService.java
│   │   │   │   ├── UnitService.java
│   │   │   ├── com.example.util
│   │   │   │   ├── DatabaseUtil.java
│   ├── resources
│   │   ├── db.properties
│   │   ├── META-INF
│   │   │   ├── context.xml
│   │   ├── webapp
│   │   │   ├── WEB-INF
│   │   │   │   ├── web.xml
├── pom.xml

2. Kode Sumber
--2a) db.properties
	db.url=jdbc:oracle:thin:@localhost:1521/ADISXDB.ICONPLN.CO.ID
	db.username=USERADISMONLAP
	db.password=adis123

--2b) DatabaseUtil.java
	package com.example.util;

	import java.io.InputStream;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.util.Properties;

	public class DatabaseUtil {

		private static final String PROPERTIES_FILE = "db.properties";

		public static Connection getConnection() {
			Connection connection = null;
			try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
				if (input == null) {
					throw new RuntimeException("File " + PROPERTIES_FILE + " tidak ditemukan di classpath!");
				}

				// Load properties file
				Properties props = new Properties();
				props.load(input);

				// Baca konfigurasi database
				String url = props.getProperty("db.url");
				String username = props.getProperty("db.username");
				String password = props.getProperty("db.password");

				// Load Oracle JDBC driver
				Class.forName("oracle.jdbc.driver.OracleDriver");

				// Buat koneksi baru ke database
				connection = DriverManager.getConnection(url, username, password);
				System.out.println("Koneksi ke database berhasil!");

			} catch (Exception e) {
				System.err.println("Gagal membuat koneksi ke database!");
				e.printStackTrace();
				throw new RuntimeException("Error: " + e.getMessage(), e);
			}
			return connection;
		}

		// Metode main untuk testing koneksi
		public static void main(String[] args) {
			try (Connection testConn = DatabaseUtil.getConnection()) {
				if (testConn != null) {
					System.out.println("Tes koneksi berhasil!");
				} else {
					System.out.println("Tes koneksi gagal!");
				}
			} catch (Exception e) {
				System.err.println("Terjadi kesalahan saat menguji koneksi:");
				e.printStackTrace();
			}
		}
	}

Model Classes
--2c) Unit.java
	package com.example.model;

	public class Unit {
		private String unitup;
		private String nama_unit;

		// Getters and Setters
		public String getUnitup() { return unitup; }
		public void setUnitup(String unitup) { this.unitup = unitup; }

		public String getNama_unit() { return nama_unit; }
		public void setNama_unit(String nama_unit) { this.nama_unit = nama_unit; }
	}

--2c) Bank.java
	package com.example.model;

	import com.google.gson.annotations.SerializedName;

	public class Bank {
		@SerializedName("kode_bank")
		private String kodeBank;

		@SerializedName("nama_bank")
		private String namaBank;

		public String getKodeBank() {
			return kodeBank;
		}

		public void setKodeBank(String kodeBank) {
			this.kodeBank = kodeBank;
		}

		public String getNamaBank() {
			return namaBank;
		}

		public void setNamaBank(String namaBank) {
			this.namaBank = namaBank;
		}
	}


Service Classes
--2d) UnitService.java
	package com.example.service;

	import com.example.model.Unit;
	import com.example.util.DatabaseUtil;

	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.util.ArrayList;
	import java.util.List;

	public class UnitService {
		public List<Unit> getAllUnits() {
			List<Unit> units = new ArrayList<>();
			String query = "SELECT unitup, nama_unit FROM VER_MASTER_UNIT";

			try (Connection connection = DatabaseUtil.getConnection();
				 PreparedStatement ps = connection.prepareStatement(query);
				 ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					Unit unit = new Unit();
					unit.setUnitup(rs.getString("unitup"));
					unit.setNama_unit(rs.getString("nama_unit"));
					units.add(unit);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return units;
		}
	}



--2e) BankService.java
	package com.example.service;

	import com.example.model.Bank;
	import com.example.util.DatabaseUtil;
	// import com.google.gson.Gson;

	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Optional;

	// import javax.ws.rs.Consumes;
	// import javax.ws.rs.POST;
	// import javax.ws.rs.Path;
	// import javax.ws.rs.Produces;
	// import javax.ws.rs.core.MediaType;

	public class BankService {

		// Mendapatkan semua bank
		public List<Bank> getAllBanks() {
			List<Bank> banks = new ArrayList<>();
			String query = "SELECT kode_bank, nama_bank FROM VER_MASTER_BANK";

			try (Connection connection = DatabaseUtil.getConnection();
				 PreparedStatement ps = connection.prepareStatement(query);
				 ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					Bank bank = new Bank();
					bank.setKodeBank(rs.getString("kode_bank")); // Disesuaikan ke camelCase
					bank.setNamaBank(rs.getString("nama_bank")); // Disesuaikan ke camelCase
					banks.add(bank);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return banks;
		}

		// Mendapatkan bank berdasarkan kode_bank
		public Optional<Bank> getBankByKode(String kodeBank) {
			String query = "SELECT kode_bank, nama_bank FROM VER_MASTER_BANK WHERE kode_bank = ?";
			try (Connection connection = DatabaseUtil.getConnection();
				 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

				preparedStatement.setString(1, kodeBank);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					Bank bank = new Bank();
					bank.setKodeBank(resultSet.getString("kode_bank")); // Disesuaikan ke camelCase
					bank.setNamaBank(resultSet.getString("nama_bank")); // Disesuaikan ke camelCase
					return Optional.of(bank);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Optional.empty();
		}
		
	   // insert data bank metode post
	   public static String addBank(Bank bank) {
			String query = "INSERT INTO VER_MASTER_BANK (kode_bank, nama_bank) VALUES (?, ?)";
			Connection connection = null;
			PreparedStatement preparedStatement = null;

			try {
				connection = DatabaseUtil.getConnection();
				connection.setAutoCommit(false); // Nonaktifkan auto-commit untuk transaksi manual

				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, bank.getKodeBank());
				preparedStatement.setString(2, bank.getNamaBank());

				int rowsInserted = preparedStatement.executeUpdate();
				connection.commit(); // Commit transaksi

				if (rowsInserted > 0) {
					return "{\"message\":\"Bank added successfully!\"}";
				} else {
					return "{\"error\":\"Failed to insert bank record.\"}";
				}
			} catch (Exception e) {
				e.printStackTrace();

				// Tangkap error spesifik
				if (e.getMessage().contains("ORA-00001: unique constraint")) {
					return "{\"error\":\"Bank with the same kode_bank already exists.\"}";
				} else {
					return "{\"error\":\"An unexpected error occurred: " + e.getMessage() + "\"}";
				}
			} finally {
				// Tutup koneksi
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
					if (connection != null) {
						connection.setAutoCommit(true);
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}


		public static void main(String[] args) {
			BankService bankService = new BankService();
		
			// Test getAllBanks
			List<Bank> allBanks = bankService.getAllBanks();
			System.out.println("All Banks: " + allBanks);
		
			// Test getBankByKode
			String kodeBank = "123";
			Optional<Bank> bank = bankService.getBankByKode(kodeBank);
			bank.ifPresentOrElse(
				b -> System.out.println("Bank found: " + b.getNamaBank()),
				() -> System.out.println("No bank found with kode_bank: " + kodeBank)
			);
		}

	}


Controllers
--2f)UnitController.java

	package com.example.controller;

	import com.example.model.Unit;
	import com.example.service.UnitService;
	import com.google.gson.Gson;

	import javax.ws.rs.GET;
	import javax.ws.rs.Path;
	import javax.ws.rs.Produces;
	import javax.ws.rs.core.MediaType;
	import java.util.List;

	@Path("/units")
	public class UnitController {

		private UnitService unitService = new UnitService();

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public String getAllUnits() {
			System.out.println("Fetching units..."); // Log untuk melacak eksekusi
			List<Unit> units = null;

			try {
				// Memanggil service untuk mendapatkan data
				units = unitService.getAllUnits();
				System.out.println("Units fetched: " + (units != null ? units.size() : 0));
			} catch (Exception e) {
				// Log error jika terjadi masalah
				System.err.println("Error fetching units: " + e.getMessage());
				e.printStackTrace();
				return "{\"error\":\"Failed to fetch units.\"}";
			}

			// Mengembalikan data dalam format JSON
			return new Gson().toJson(units);
		}
	}



--2g) BankController.java
	package com.example.controller;

	import com.example.model.Bank;
	import com.example.service.BankService;
	import com.google.gson.Gson;

	import javax.ws.rs.Consumes;
	import javax.ws.rs.GET;
	import javax.ws.rs.POST;
	import javax.ws.rs.Path;
	import javax.ws.rs.Produces;
	import javax.ws.rs.QueryParam;
	import javax.ws.rs.core.MediaType;
	import java.util.List;

	@Path("/banks")
	public class BankController {

		private BankService bankService = new BankService();

		// Endpoint untuk mendapatkan semua bank
		@GET
		@Path("/getBankAll")
		@Produces(MediaType.APPLICATION_JSON)
		public String getAllBanks() {
			try {
				List<Bank> banks = bankService.getAllBanks(); // Pastikan metode ini ada di BankService
				return new Gson().toJson(banks);
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"error\":\"An error occurred while fetching all banks.\"}";
			}
		}

		// Endpoint untuk mendapatkan bank berdasarkan kode_bank
		@GET
		@Path("/getBankByKode")
		@Produces(MediaType.APPLICATION_JSON)
		public String getBankByKode(@QueryParam("kode_bank") String kodeBank) {
			try {
				if (kodeBank == null || kodeBank.isEmpty()) {
					return "{\"error\":\"Parameter kode_bank is required.\"}";
				}

				return bankService.getBankByKode(kodeBank) // Pastikan metode ini ada di BankService
						.map(bank -> new Gson().toJson(bank))
						.orElse("{\"error\":\"Bank not found for kode_bank: " + kodeBank + "\"}");
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"error\":\"An error occurred while fetching the bank.\"}";
			}
		}

		// Endpoint untuk menambahkan data bank
		// insert data bank metode post
		// insert data bank metode post
		@POST
		@Path("/postBankadd")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String addBank(String jsonBody) {
			try {
				// Parse JSON body ke objek Bank
				Bank bank = new Gson().fromJson(jsonBody, Bank.class);

				// Log data bank yang diterima
				System.out.println("Received Bank Data: " + new Gson().toJson(bank));

				if (bank.getKodeBank() == null || bank.getNamaBank() == null) {
					return "{\"error\":\"kode_bank and nama_bank are required.\"}";
				}

				// Panggil service untuk menambahkan bank
				String resultMessage = BankService.addBank(bank);
				return resultMessage; // Kirimkan pesan yang dikembalikan oleh service
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"error\":\"An error occurred while adding the bank.\"}";
			}
		}



	}


--2h) web.xml
	<web-app xmlns="http://java.sun.com/xml/ns/javaee"
			 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			 xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
								 http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
			 version="3.0">
		<display-name>Maven REST API</display-name>

		<!-- Jersey Servlet Mapping -->
		<servlet>
			<servlet-name>jersey-serlvet</servlet-name>
			<servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
			<init-param>
				<param-name>jersey.config.server.provider.packages</param-name>
				<param-value>com.example.controller</param-value>
			</init-param>
			<load-on-startup>1</load-on-startup>
		</servlet>

		<servlet-mapping>
			<servlet-name>jersey-serlvet</servlet-name>
			<url-pattern>/api/*</url-pattern>
		</servlet-mapping>
	</web-app>

--2i) web.xmlpom.xml
	<project>
		<modelVersion>4.0.0</modelVersion>
		<groupId>com.example</groupId>
		<artifactId>maven-restapi-mvc</artifactId>
		<version>1.0-SNAPSHOT</version>
		<packaging>war</packaging>

		<properties>
			<!-- Encoding dan versi Java -->
			<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
			<maven.compiler.source>17</maven.compiler.source>
			<maven.compiler.target>17</maven.compiler.target>
		</properties>
		
		<dependencies>
			<!-- Jersey -->
			<dependency>
				<groupId>org.glassfish.jersey.containers</groupId>
				<artifactId>jersey-container-servlet</artifactId>
				<version>2.35</version>
			</dependency>
			<dependency>
				<groupId>org.glassfish.jersey.inject</groupId>
				<artifactId>jersey-hk2</artifactId>
				<version>2.35</version>
			</dependency>

			<!-- Gson -->
			<dependency>
				<groupId>com.google.code.gson</groupId>
				<artifactId>gson</artifactId>
				<version>2.8.9</version>
			</dependency>

			<!-- OJDBC -->
			<dependency>
				<groupId>com.oracle.database.jdbc</groupId>
				<artifactId>ojdbc8</artifactId>
				<version>19.3.0.0</version>
			</dependency>
		</dependencies>


		<build>
			<plugins>
				<plugin>
					<groupId>org.apache.tomcat.maven</groupId>
					<artifactId>tomcat7-maven-plugin</artifactId>
					<version>2.2</version>
					<configuration>
						<url>http://localhost:8080/manager/text</url>
						<username>admin</username>
						<password>admin</password>
					</configuration>
				</plugin>
			</plugins>
		</build>
	</project>


Setelah dijalankan, endpoint akan tersedia di:

GET /api/units untuk data unit.
GET /api/banks untuk data bank.





