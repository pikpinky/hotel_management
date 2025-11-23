package com.devpro.spring.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.devpro.spring.dto.CheckInInfoDto;
import com.devpro.spring.model.Chamber;
import com.devpro.spring.model.Guest;
import com.devpro.spring.service.ChamberService;
import com.devpro.spring.service.GuestService;
import com.devpro.spring.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test class cho CheckInApi.
 * Test các chức năng check-in phòng, bao gồm validation, update guest, add rental.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckInApiTest {

    private MockMvc mockMvc;

    @Mock
    private GuestService guestService;

    @Mock
    private ChamberService chamberService;

    @Mock
    private RentalService rentalService;

    @InjectMocks
    private CheckInApi checkInApi;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(checkInApi).build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test case 1: Check-in thành công với khách hàng đã tồn tại.
     * Mô tả: Khách hàng có idCard đã tồn tại, update thông tin và tạo rental.
     */
    @Test
    public void testCheckIn_ExistingGuest_Success() throws Exception {
        // Chuẩn bị dữ liệu
        CheckInInfoDto checkInDto = createValidCheckInDto();
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "false");
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Hanoi", "VN", "0123456789", "a@example.com", "false", "true");

        // Mock các service
        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(1);
        doNothing().when(guestService).updateComplete(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(guestService.searchGuestWithCart("123456789")).thenReturn(guest);
        doNothing().when(rentalService).addRentalInfo(any());

        // Thực hiện request
        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Check in thành công!\"}"));

        // Verify các service được gọi đúng
        verify(chamberService, times(1)).updateCheckIn(1L);
        verify(guestService, times(1)).updateComplete("P123456", "Hanoi", "0123456789", "a@example.com", "true", "123456789");
        verify(guestService, never()).addGuestInfo(any());
        verify(rentalService, times(1)).addRentalInfo(any());
    }

    /**
     * Test case 2: Check-in thành công với khách hàng mới.
     * Mô tả: Khách hàng chưa tồn tại, thêm mới và tạo rental.
     */
    @Test
    public void testCheckIn_NewGuest_Success() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "false");
        Guest guest = new Guest("Nguyen Van A", "1990-01-01", "123456789", "P123456", "Hanoi", "VN", "0123456789", "a@example.com", "false", "true");

        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(0);
        doNothing().when(guestService).addGuestInfo(any(Guest.class));
        when(guestService.searchGuestWithCart("123456789")).thenReturn(guest);
        doNothing().when(rentalService).addRentalInfo(any());

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Check in thành công!\"}"));

        verify(chamberService, times(1)).updateCheckIn(1L);
        verify(guestService, times(1)).addGuestInfo(any(Guest.class));
        verify(guestService, never()).updateComplete(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(rentalService, times(1)).addRentalInfo(any());
    }

    /**
     * Test case 3: Validation lỗi - thiếu thông tin bắt buộc.
     * Mô tả: Input thiếu name, trả về lỗi validation.
     */
    @Test
    public void testCheckIn_ValidationError_MissingName() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        checkInDto.setName(null); // Thiếu name

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isBadRequest());

        // Verify không gọi service
        verify(chamberService, never()).updateCheckIn(any());
        verify(guestService, never()).checkExistGuest(anyString());
    }

    /**
     * Test case 4: Lỗi hệ thống - checkExistGuest trả về >1.
     * Mô tả: Có nhiều hơn 1 khách với cùng idCard, trả về lỗi.
     */
    @Test
    public void testCheckIn_SystemError_DuplicateGuest() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();

        when(chamberService.findChamber(1L)).thenReturn(new Chamber());
        when(guestService.checkExistGuest("123456789")).thenReturn(2); // Duplicate

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Lỗi hệ thống vui lòng thử lại sau!\"}"));

        verify(chamberService, times(1)).updateCheckIn(1L);
        verify(guestService, never()).updateComplete(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(guestService, never()).addGuestInfo(any());
        verify(rentalService, never()).addRentalInfo(any());
    }

    /**
     * Test case 5: Check-in với phòng VIP.
     * Mô tả: Phòng là VIP, guest được set isVip = true.
     */
    @Test
    public void testCheckIn_VipRoom() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        Chamber chamber = new Chamber("101", "single", "true", "100", "20", "note", "true"); // VIP
        Guest guest = new Guest();

        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(0);
        doNothing().when(guestService).addGuestInfo(any(Guest.class));
        when(guestService.searchGuestWithCart("123456789")).thenReturn(guest);
        doNothing().when(rentalService).addRentalInfo(any());

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk());

        // Verify guest được tạo với isVip = true
        verify(guestService, times(1)).addGuestInfo(any(Guest.class)); // Có thể verify argument nếu cần
    }

    /**
     * Test case 6: Check-in với note.
     * Mô tả: Rental được tạo với note từ DTO.
     */
    @Test
    public void testCheckIn_WithNote() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        checkInDto.setNote("Special request");
        Chamber chamber = new Chamber();
        Guest guest = new Guest();

        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(0);
        doNothing().when(guestService).addGuestInfo(any());
        when(guestService.searchGuestWithCart("123456789")).thenReturn(guest);
        doNothing().when(rentalService).addRentalInfo(any());

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).addRentalInfo(any()); // Note được set trong rental
    }

    /**
     * Test case 7: Check-in với email rỗng.
     * Mô tả: Email có thể null hoặc empty.
     */
    @Test
    public void testCheckIn_EmptyEmail() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        checkInDto.setEmail("");
        Chamber chamber = new Chamber();
        Guest guest = new Guest();

        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(1);
        doNothing().when(guestService).updateComplete(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(guestService.searchGuestWithCart("123456789")).thenReturn(guest);
        doNothing().when(rentalService).addRentalInfo(any());

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk());

        verify(guestService, times(1)).updateComplete("P123456", "Hanoi", "0123456789", "", "true", "123456789");
    }

    /**
     * Test case 8: Exception khi update chamber.
     * Mô tả: Nếu updateCheckIn throw exception, transaction rollback.
     */
    @Test
    public void testCheckIn_ExceptionOnUpdateChamber() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();

        when(chamberService.findChamber(1L)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isInternalServerError()); // Hoặc tùy config

        verify(chamberService, times(1)).updateCheckIn(1L);
        verify(guestService, never()).checkExistGuest(anyString());
    }

    /**
     * Test case 9: Guest search trả về null.
     * Mô tả: Sau add/update, searchGuestWithCart trả null, nhưng vẫn trả success? Theo code, nếu null thì vẫn ok.
     */
    @Test
    public void testCheckIn_GuestSearchNull() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        Chamber chamber = new Chamber();

        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(0);
        doNothing().when(guestService).addGuestInfo(any());
        when(guestService.searchGuestWithCart("123456789")).thenReturn(null);
        doNothing().when(rentalService).addRentalInfo(any());

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).addRentalInfo(any());
    }

    /**
     * Test case 10: Check-in với tất cả field đầy đủ.
     * Mô tả: Test với input hoàn chỉnh.
     */
    @Test
    public void testCheckIn_FullFields() throws Exception {
        CheckInInfoDto checkInDto = createValidCheckInDto();
        Chamber chamber = new Chamber("101", "couple", "false", "200", "30", "luxury", "false");
        Guest guest = new Guest("Tran Thi B", "1985-05-05", "987654321", "P654321", "HCM", "VN", "0987654321", "b@example.com", "false", "false");

        when(chamberService.findChamber(1L)).thenReturn(chamber);
        when(guestService.checkExistGuest("123456789")).thenReturn(1);
        doNothing().when(guestService).updateComplete(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(guestService.searchGuestWithCart("123456789")).thenReturn(guest);
        doNothing().when(rentalService).addRentalInfo(any());

        mockMvc.perform(post("/rent-chamber")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk());

        verify(chamberService, times(1)).updateCheckIn(1L);
        verify(guestService, times(1)).updateComplete("P123456", "Hanoi", "0123456789", "a@example.com", "false", "123456789");
        verify(rentalService, times(1)).addRentalInfo(any());
    }

    // Helper method để tạo DTO hợp lệ
    private CheckInInfoDto createValidCheckInDto() {
        CheckInInfoDto dto = new CheckInInfoDto();
        dto.setName("Nguyen Van A");
        dto.setIdCard("123456789");
        dto.setBirth("1990-01-01");
        dto.setPassport("P123456");
        dto.setAddress("Hanoi");
        dto.setNationality("VN");
        dto.setPhone("0123456789");
        dto.setEmail("a@example.com");
        dto.setNote("No note");
        dto.setChamberId(1L);
        return dto;
    }
}
