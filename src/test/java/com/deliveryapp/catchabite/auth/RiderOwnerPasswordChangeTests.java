package com.deliveryapp.catchabite.auth;

import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RiderOwnerPasswordChangeTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DelivererRepository delivererRepository;

    @Autowired
    private StoreOwnerRepository storeOwnerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void riderPasswordChangeSuccessAndRelogin() throws Exception {
        String email = uniqueEmail("rider");
        String currentPassword = "Passw0rd!";
        String newPassword = "NewPassw0rd!";
        createRider(email, currentPassword);

        MockHttpSession session = loginRiderSession(email, currentPassword);
        String payload = buildPasswordPayload(currentPassword, newPassword, newPassword);

        mockMvc.perform(patch("/api/v1/riders/me/password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.changed").value(true));

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", email);
        loginPayload.put("password", newPassword);

        mockMvc.perform(post("/api/v1/deliverer/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
            .andExpect(status().isOk());
    }

    @Test
    void riderPasswordChangeConfirmMismatchReturnsBadRequest() throws Exception {
        String email = uniqueEmail("rider");
        String currentPassword = "Passw0rd!";
        createRider(email, currentPassword);

        MockHttpSession session = loginRiderSession(email, currentPassword);
        String payload = buildPasswordPayload(currentPassword, "NewPassw0rd!", "Mismatch1!");

        mockMvc.perform(patch("/api/v1/riders/me/password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void riderPasswordChangeWrongCurrentPasswordReturnsBadRequest() throws Exception {
        String email = uniqueEmail("rider");
        String currentPassword = "Passw0rd!";
        createRider(email, currentPassword);

        MockHttpSession session = loginRiderSession(email, currentPassword);
        String payload = buildPasswordPayload("WrongPass1!", "NewPassw0rd!", "NewPassw0rd!");

        mockMvc.perform(patch("/api/v1/riders/me/password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void ownerPasswordChangeSuccessAndRelogin() throws Exception {
        String email = uniqueEmail("owner");
        String currentPassword = "Passw0rd!";
        String newPassword = "NewPassw0rd!";
        createOwner(email, "Owner", "01012345678", currentPassword);

        MockHttpSession session = loginOwnerSession(email, currentPassword);
        String payload = buildPasswordPayload(currentPassword, newPassword, newPassword);

        mockMvc.perform(patch("/api/v1/owners/me/password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.changed").value(true));

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", email);
        loginPayload.put("password", newPassword);

        mockMvc.perform(post("/api/v1/store-owner/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
            .andExpect(status().isOk());
    }

    @Test
    void ownerPasswordChangeConfirmMismatchReturnsBadRequest() throws Exception {
        String email = uniqueEmail("owner");
        String currentPassword = "Passw0rd!";
        createOwner(email, "Owner", "01098765432", currentPassword);

        MockHttpSession session = loginOwnerSession(email, currentPassword);
        String payload = buildPasswordPayload(currentPassword, "NewPassw0rd!", "Mismatch1!");

        mockMvc.perform(patch("/api/v1/owners/me/password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void ownerPasswordChangeWrongCurrentPasswordReturnsBadRequest() throws Exception {
        String email = uniqueEmail("owner");
        String currentPassword = "Passw0rd!";
        createOwner(email, "Owner", "01055556666", currentPassword);

        MockHttpSession session = loginOwnerSession(email, currentPassword);
        String payload = buildPasswordPayload("WrongPass1!", "NewPassw0rd!", "NewPassw0rd!");

        mockMvc.perform(patch("/api/v1/owners/me/password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    private Deliverer createRider(String email, String rawPassword) {
        Deliverer rider = Deliverer.builder()
            .delivererEmail(email)
            .delivererPassword(passwordEncoder.encode(rawPassword))
            .delivererVehicleType(DelivererVehicleType.WALKING)
            .delivererVerified(YesNo.N)
            .delivererCreatedDate(LocalDateTime.now())
            .build();
        return delivererRepository.save(rider);
    }

    private StoreOwner createOwner(String email, String name, String mobile, String rawPassword) {
        StoreOwner owner = StoreOwner.builder()
            .storeOwnerEmail(email)
            .storeOwnerPassword(passwordEncoder.encode(rawPassword))
            .storeOwnerName(name)
            .storeOwnerMobile(mobile)
            .storeOwnerBusinessRegistrationNo("BRN-" + uniqueSuffix())
            .createdAt(LocalDateTime.now())
            .build();
        return storeOwnerRepository.save(owner);
    }

    private MockHttpSession loginRiderSession(String email, String password) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);

        MvcResult result = mockMvc.perform(post("/api/v1/deliverer/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        return session;
    }

    private MockHttpSession loginOwnerSession(String email, String password) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);

        MvcResult result = mockMvc.perform(post("/api/v1/store-owner/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        return session;
    }

    private String buildPasswordPayload(String current, String next, String confirm) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("currentPassword", current);
        payload.put("newPassword", next);
        payload.put("confirmNewPassword", confirm);
        return objectMapper.writeValueAsString(payload);
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String uniqueEmail(String prefix) {
        return prefix + uniqueSuffix() + "@test.com";
    }
}
