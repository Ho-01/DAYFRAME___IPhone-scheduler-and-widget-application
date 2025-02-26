package com.BUS.DayFrame;

import com.BUS.DayFrame.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DayFrameApplicationTests {

	@Test
	void register() {
		User user1 = new User();
		user1.setEmail("user1@gmail.com");
		user1.setPassword("0000");
		user1.setName("사용자");
	}

	@Test
	void contextLoads() {
	}

}
