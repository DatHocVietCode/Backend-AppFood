package vn.dk.BackendFoodApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

////Disable Security
//@SpringBootApplication(exclude = {
//		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})


@SpringBootApplication
public class BackendFoodappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendFoodappApplication.class, args);
	}
}
