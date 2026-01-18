package dev.folomkin.reservationsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    public void getReservationById() throws Exception {
        mockMvc.perform(get("/reservation/{0}", "0"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAllReservations() throws Exception {
        mockMvc.perform(get("/reservation"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createReservation() throws Exception {
        String reservationToCreate = """
                {
                    "id": 0,
                    "userId": 0,
                    "roomId": 0,
                    "startDate": "2026-01-16",
                    "endDate": "2026-01-16",
                    "status": "PENDING"
                }""";

        mockMvc.perform(post("/reservation")
                        .content(reservationToCreate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updateReservation() throws Exception {
        String reservationToUpdate = """
                {
                    "id": 0,
                    "userId": 0,
                    "roomId": 0,
                    "startDate": "2026-01-16",
                    "endDate": "2026-01-16",
                    "status": "PENDING"
                }""";

        mockMvc.perform(put("/reservation/{0}", "0")
                        .content(reservationToUpdate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteReservation() throws Exception {
        mockMvc.perform(delete("/reservation/{0}", "0"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void approveReservation() throws Exception {
        mockMvc.perform(post("/reservation/{0}/approve", "0"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
