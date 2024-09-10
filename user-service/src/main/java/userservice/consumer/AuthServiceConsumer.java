package userservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import userservice.entities.UserInfoDto;
import userservice.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthServiceConsumer {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @KafkaListener(topics = "some-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserInfoDto eventData) {
        try{
            // Todo: Make it transactional, to handle idempotency and validate email, phoneNumber etc
            userService.createOrUpdateUser(eventData);
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("AuthServiceConsumer: Exception is thrown while consuming kafka event");
        }
    }
}
