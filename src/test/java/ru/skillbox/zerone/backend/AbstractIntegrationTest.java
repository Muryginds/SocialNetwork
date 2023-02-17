package ru.skillbox.zerone.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
abstract public class AbstractIntegrationTest {
   @MockBean
   JavaMailSenderImpl javaMailSender;
}