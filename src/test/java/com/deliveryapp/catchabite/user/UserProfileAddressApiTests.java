package com.deliveryapp.catchabite.user;

import com.deliveryapp.catchabite.entity.Address;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AddressRepository;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserProfileAddressApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void meProfileReturnsNicknameAndLoginId() throws Exception {
        String email = uniqueEmail("user");
        String password = "Passw0rd!";
        AppUser user = createUser(email, uniqueMobile(), "nickA", "Test User", password);
        MockHttpSession session = loginUserSession(email, password);

        mockMvc.perform(get("/api/v1/auth/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(user.getAppUserId()))
            .andExpect(jsonPath("$.loginId").value(email))
            .andExpect(jsonPath("$.nickname").value("nickA"))
            .andExpect(jsonPath("$.roleName").value("ROLE_USER"));
    }

    @Test
    void updateNicknameSuccess() throws Exception {
        String email = uniqueEmail("user");
        String password = "Passw0rd!";
        AppUser user = createUser(email, uniqueMobile(), "nickA", "Test User", password);
        MockHttpSession session = loginUserSession(email, password);

        Map<String, Object> payload = Map.of("nickname", "nickB");

        mockMvc.perform(patch("/api/v1/users/me/profile")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.userId").value(user.getAppUserId()))
            .andExpect(jsonPath("$.data.loginId").value(email))
            .andExpect(jsonPath("$.data.nickname").value("nickB"));

        AppUser updated = appUserRepository.findById(user.getAppUserId()).orElseThrow();
        assertThat(updated.getAppUserNickname()).isEqualTo("nickB");
    }

    @Test
    void updateNicknameDuplicateReturnsConflict() throws Exception {
        String emailA = uniqueEmail("userA");
        String emailB = uniqueEmail("userB");
        createUser(emailA, uniqueMobile(), "dupNick", "User A", "Passw0rd!");
        createUser(emailB, uniqueMobile(), "otherNick", "User B", "Passw0rd!");

        MockHttpSession session = loginUserSession(emailB, "Passw0rd!");

        Map<String, Object> payload = Map.of("nickname", "dupNick");

        mockMvc.perform(patch("/api/v1/users/me/profile")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("DUPLICATE_NICKNAME"));
    }

    @Test
    void getMyAddressesReturnsList() throws Exception {
        String email = uniqueEmail("user");
        String password = "Passw0rd!";
        AppUser user = createUser(email, uniqueMobile(), "nickA", "Test User", password);
        createAddress(user, "123 Main St", "Home", true);
        createAddress(user, "456 Side St", "Office", false);

        MockHttpSession session = loginUserSession(email, password);

        mockMvc.perform(get("/api/v1/addresses/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void updateMyAddressSuccess() throws Exception {
        String email = uniqueEmail("user");
        String password = "Passw0rd!";
        AppUser user = createUser(email, uniqueMobile(), "nickA", "Test User", password);
        Address address = createAddress(user, "123 Main St", "Home", false);

        MockHttpSession session = loginUserSession(email, password);

        Map<String, Object> payload = new HashMap<>();
        payload.put("addressDetail", "789 New St");
        payload.put("addressNickname", "New Home");
        payload.put("isDefault", true);

        mockMvc.perform(patch("/api/v1/addresses/" + address.getAddressId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.addressDetail").value("789 New St"))
            .andExpect(jsonPath("$.data.addressNickname").value("New Home"))
            .andExpect(jsonPath("$.data.isDefault").value(true));
    }

    @Test
    void otherUserAddressUpdateAndDeleteForbidden() throws Exception {
        String ownerEmail = uniqueEmail("owner");
        String otherEmail = uniqueEmail("other");
        AppUser owner = createUser(ownerEmail, uniqueMobile(), "ownerNick", "Owner", "Passw0rd!");
        Address address = createAddress(owner, "123 Main St", "Home", false);

        createUser(otherEmail, uniqueMobile(), "otherNick", "Other", "Passw0rd!");
        MockHttpSession session = loginUserSession(otherEmail, "Passw0rd!");

        Map<String, Object> payload = Map.of("addressDetail", "Hack St");

        mockMvc.perform(patch("/api/v1/addresses/" + address.getAddressId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(delete("/api/v1/addresses/" + address.getAddressId()).session(session))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    private AppUser createUser(String email, String mobile, String nickname, String name, String rawPassword) {
        AppUser user = AppUser.builder()
            .appUserEmail(email)
            .appUserPassword(passwordEncoder.encode(rawPassword))
            .appUserNickname(nickname)
            .appUserMobile(mobile)
            .appUserName(name)
            .appUserCreatedDate(LocalDateTime.now())
            .build();
        return appUserRepository.save(user);
    }

    private Address createAddress(AppUser user, String detail, String nickname, boolean isDefault) {
        Address address = Address.builder()
            .appUser(user)
            .addressDetail(detail)
            .addressNickname(nickname)
            .addressEntranceMethod(null)
            .addressIsDefault(isDefault ? "Y" : "N")
            .addressCreatedDate(LocalDate.now())
            .addressVisible("Y")
            .build();
        return addressRepository.save(address);
    }

    private MockHttpSession loginUserSession(String loginKey, String password) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("loginKey", loginKey);
        payload.put("password", password);
        payload.put("accountType", "USER");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        return session;
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String uniqueEmail(String prefix) {
        return prefix + uniqueSuffix() + "@test.com";
    }

    private String uniqueMobile() {
        long value = Math.abs(System.nanoTime() % 100000000L);
        return "010" + String.format("%08d", value);
    }
}
