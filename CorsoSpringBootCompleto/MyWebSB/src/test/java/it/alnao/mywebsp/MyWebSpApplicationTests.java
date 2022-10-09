package it.alnao.mywebsp;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.alnao.mywebsb.repository.PasswordRepository;


@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = Application.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyWebSpApplicationTests {

	@Autowired
	private PasswordRepository PasswordRepository;
	
	@Test
	void contextLoads() {
	}

}
