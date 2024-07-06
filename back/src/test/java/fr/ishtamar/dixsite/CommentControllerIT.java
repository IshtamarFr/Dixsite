package fr.ishtamar.dixsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ishtamar.TestContent;
import fr.ishtamar.dixsite.model.album.AlbumRepository;
import fr.ishtamar.dixsite.model.comment.CommentRepository;
import fr.ishtamar.dixsite.model.comment.CreateCommentRequest;
import fr.ishtamar.dixsite.model.picvid.PicvidRepository;
import fr.ishtamar.starter.model.user.UserInfoRepository;
import fr.ishtamar.starter.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    CommentRepository repository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    PicvidRepository picvidRepository;
    @Autowired
    JwtService jwtService;

    final ObjectMapper mapper=new ObjectMapper();

    @BeforeEach
    @AfterEach
    void clean() {
        repository.deleteAll();
        picvidRepository.deleteAll();
        albumRepository.deleteAll();
        userInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("When I ask to get all comments for legit picvid, it works")
    @WithMockUser(roles="USER")
    void testGetAllCommentsForValidPicvid() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();

        repository.save(tc.initialComment1);
        repository.save(tc.initialComment2);

        //When
        mockMvc.perform(get("/album/"+albumId+"/picvid/"+picvidId+"/comment"))

        //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().string(containsString("Super")))
                .andExpect(content().string(containsString("n'aime pas")));
    }

    @Test
    @DisplayName("When I try to add a valid comment, it works")
    @WithMockUser(roles="USER")
    void testAddCommentForValidPicvid() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        CreateCommentRequest mockRequest=CreateCommentRequest.builder()
                .content("Trop belle photo !!!!")
                .build();

        //When
        mockMvc.perform(post("/album/"+albumId+"/picvid/"+picvidId+"/comment")
                .header("Authorization","Bearer "+jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mockRequest)))
        //Then
                .andExpect(status().isOk());

        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("When I try to answer to a non answer, all is OK")
    @WithMockUser(roles="USER")
    void testAddAnswerToNonAnswer() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();
        Long motherId=repository.save(tc.initialComment1).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        CreateCommentRequest mockRequest=CreateCommentRequest.builder()
                .content("Trop belle photo !!!!")
                .mother_id(motherId)
                .build();

        //When
        mockMvc.perform(post("/album/"+albumId+"/picvid/"+picvidId+"/comment")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockRequest)))
                //Then
                .andExpect(status().isOk());

        assertThat(repository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("When I try to answer to an answer, an error is thrown")
    @WithMockUser(roles="USER")
    void testAddAnswerToAnswer() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();
        repository.save(tc.initialComment2);
        Long answerId=repository.save(tc.initialAnswer).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        CreateCommentRequest mockRequest=CreateCommentRequest.builder()
                .content("Trop belle photo !!!!")
                .mother_id(answerId)
                .build();

        //When
        mockMvc.perform(post("/album/"+albumId+"/picvid/"+picvidId+"/comment")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockRequest)))

                        //Then
                        .andExpect(status().isBadRequest());

        assertThat(repository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("When owner tries to moderate a comment, it works")
    @WithMockUser(roles="USER")
    void testModerateOwner() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();
        Long commentId=repository.save(tc.initialComment2).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        //When
        mockMvc.perform(put("/album/"+albumId+"/picvid/"+picvidId+"/comment/"+commentId)
                        .header("Authorization","Bearer "+jwt)
                        .param("action","moderate"))

                //Then
                .andExpect(status().isOk());

        assertThat(repository.findById(commentId).get().getStatus()).isEqualTo("MODERATED");
    }

    @Test
    @DisplayName("When user tries to moderate a comment, it is bad request")
    @WithMockUser(roles="USER")
    void testModerateUser() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();
        Long commentId=repository.save(tc.initialComment2).getId();

        String jwt=jwtService.generateToken(tc.initialUser2.getEmail());

        //When
        mockMvc.perform(put("/album/"+albumId+"/picvid/"+picvidId+"/comment/"+commentId)
                        .header("Authorization","Bearer "+jwt)
                        .param("action","moderate"))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When owner tries to unmoderate a comment, it works")
    @WithMockUser(roles="USER")
    void testUnmoderateOwner() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();

        tc.initialComment2.setStatus("MODERATED");

        Long commentId=repository.save(tc.initialComment2).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        //When
        mockMvc.perform(put("/album/"+albumId+"/picvid/"+picvidId+"/comment/"+commentId)
                        .header("Authorization","Bearer "+jwt)
                        .param("action","unmoderate"))

                //Then
                .andExpect(status().isOk());

        assertThat(repository.findById(commentId).get().getStatus()).isEqualTo("ONLINE");
    }

    @Test
    @DisplayName("When owner tries to unmoderate a superadmined comment, it is bad request")
    @WithMockUser(roles="ADMIN")
    void testUnmoderateOwnerAdmin() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();

        tc.initialComment2.setStatus("ADMIN");

        Long commentId=repository.save(tc.initialComment2).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        //When
        mockMvc.perform(put("/album/"+albumId+"/picvid/"+picvidId+"/comment/"+commentId)
                        .header("Authorization","Bearer "+jwt)
                        .param("action","unmoderate"))

                //Then
                .andExpect(status().isBadRequest());

        assertThat(repository.findById(commentId).get().getStatus()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("When owner tries to moderate a supermoderated comment, it is bad request")
    @WithMockUser(roles="USER")
    void testModerateOwnerAdmin() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();

        tc.initialComment2.setStatus("ADMIN");

        Long commentId=repository.save(tc.initialComment2).getId();

        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        //When
        mockMvc.perform(put("/album/"+albumId+"/picvid/"+picvidId+"/comment/"+commentId)
                        .header("Authorization","Bearer "+jwt)
                        .param("action","moderate"))

                //Then
                .andExpect(status().isBadRequest());

        assertThat(repository.findById(commentId).get().getStatus()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("When admin tries to supermoderate a moderate comment, it is bad request")
    @WithMockUser(roles="USER")
    void testSuperModerateAdmin() throws Exception {
        //Given
        TestContent tc=new TestContent();

        userInfoRepository.save(tc.initialUser);
        userInfoRepository.save(tc.initialUser2);
        userInfoRepository.save(tc.initialAdmin);
        Long albumId=albumRepository.save(tc.initialAlbum).getId();
        Long picvidId=picvidRepository.save(tc.initialPicvid).getId();

        tc.initialComment2.setStatus("MODERATED");

        Long commentId=repository.save(tc.initialComment2).getId();

        String jwt=jwtService.generateToken(tc.initialAdmin.getEmail());

        //When
        mockMvc.perform(put("/album/"+albumId+"/picvid/"+picvidId+"/comment/"+commentId)
                        .header("Authorization","Bearer "+jwt)
                        .param("action","supermoderate"))

                //Then
                .andExpect(status().isOk());

        assertThat(repository.findById(commentId).get().getStatus()).isEqualTo("ADMIN");
    }
}
