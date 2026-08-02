package com.i2iacademy.voltwise.Home_Management.Home;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i2iacademy.voltwise.AI_Notification.DTO.AiAlertResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.ApplianceLiveResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.ConsumptionSnapshotResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeRegistrationRequest;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeStatusResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeSummaryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/homes")
@RequiredArgsConstructor
@Tag(name = "Home Management")
public class HomeController {
    private final HomeServices homeServices;
    /* private final IgniteClient igniteClient; */

    @PostMapping
    @Operation(summary = "Register a new home with its appliances")
    public ResponseEntity<HomeResponse> registerHome(@Valid @RequestBody HomeRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(homeServices.registerHome(request));
    }

    @GetMapping
    @Operation(summary = "Get all homes with their summary information")
    public List<HomeSummaryResponse> getAllHomes() {
        return homeServices.getAllHomesSummary();
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get live status from Ignite (never touches Postgres)")
    public HomeStatusResponse getStatus(@PathVariable UUID id) {
        return homeServices.getLiveStatus(id);
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get historical consumption trend from Postgres")
    public List<ConsumptionSnapshotResponse> getHistory(@PathVariable UUID id) {
        return homeServices.getConsumptionHistory(id);
    }
    @GetMapping("/{homeId}/appliances")
    public List<ApplianceLiveResponse> getLiveAppliances(@PathVariable UUID homeId) {
        return homeServices.getLiveAppliances(homeId);
    }
    @GetMapping("/{homeId}/ai_advice")
    public List<AiAlertResponse> getAiAlerts(@PathVariable UUID homeId) {
        return homeServices.getAiAlerts(homeId);
    }
}
