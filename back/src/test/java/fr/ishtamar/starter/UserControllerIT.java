package fr.ishtamar.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserInfoRepository repository;

    ObjectMapper mapper=new ObjectMapper();
    final UserInfo initialUser=UserInfo.builder()
            .name("TickleMonster")
            .email("test999@test.com")
            .password(passwordEncoder().encode("Aa1234567!"))
            .roles("ROLE_USER")
            .build();

    @BeforeEach
    @AfterEach
    void init() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("When I query data from user, I get its Dto")
    @WithMockUser(roles="USER")
    void testGetUserDto() throws Exception {
        //Given
        Long id=repository.save(initialUser).getId();

        //When
        mockMvc.perform(get("/user/"+id))

        //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TickleMonster")))
                .andExpect(content().string(CoreMatchers.not(containsString("word"))));
    }

    @Test
    @DisplayName("When I query data from inexistant user, it is not found")
    @WithMockUser(roles="USER")
    void testGetInvalidUserDto() throws Exception {
        //Given
        long id=repository.save(initialUser).getId()+1;

        //When
        mockMvc.perform(get("/user/"+id))

        //Then
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When I query data from user when unauthenticated, it is forbidden")
    void testGetUserDtoAsInvalid() throws Exception {
        //Given
        long id=repository.save(initialUser).getId();

        //When
        mockMvc.perform(get("/user/"+id))

        //Then
                .andExpect(status().isForbidden());
    }
}
