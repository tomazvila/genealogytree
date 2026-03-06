package com.geneinator.service.impl;

import com.geneinator.dto.settings.SystemSettingsDto;
import com.geneinator.service.SettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class SettingsServiceImpl implements SettingsService {

    private final AtomicReference<SystemSettingsDto> settings;

    public SettingsServiceImpl(
            @Value("${app.visibility.spouse-family-visible:true}") Boolean spouseFamilyVisible,
            @Value("${app.visibility.default-max-relationship-hops:3}") Integer maxRelationshipHops,
            @Value("${app.visibility.include-marriage-connections:true}") Boolean includeMarriageConnections) {

        this.settings = new AtomicReference<>(SystemSettingsDto.builder()
                .spouseFamilyVisible(spouseFamilyVisible)
                .maxRelationshipHops(maxRelationshipHops)
                .includeMarriageConnections(includeMarriageConnections)
                .build());

        log.info("Initialized system settings: {}", this.settings.get());
    }

    @Override
    public SystemSettingsDto getSettings() {
        return settings.get();
    }

    @Override
    public SystemSettingsDto updateSettings(SystemSettingsDto newSettings) {
        log.info("Updating system settings: {}", newSettings);

        settings.updateAndGet(current -> {
            SystemSettingsDto.SystemSettingsDtoBuilder builder = SystemSettingsDto.builder();

            // Apply updates, keeping current values for null fields
            builder.spouseFamilyVisible(
                    newSettings.getSpouseFamilyVisible() != null
                            ? newSettings.getSpouseFamilyVisible()
                            : current.getSpouseFamilyVisible());

            builder.maxRelationshipHops(
                    newSettings.getMaxRelationshipHops() != null
                            ? newSettings.getMaxRelationshipHops()
                            : current.getMaxRelationshipHops());

            builder.includeMarriageConnections(
                    newSettings.getIncludeMarriageConnections() != null
                            ? newSettings.getIncludeMarriageConnections()
                            : current.getIncludeMarriageConnections());

            return builder.build();
        });

        log.info("System settings updated: {}", settings.get());
        return settings.get();
    }
}
