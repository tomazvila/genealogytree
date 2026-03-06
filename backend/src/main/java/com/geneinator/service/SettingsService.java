package com.geneinator.service;

import com.geneinator.dto.settings.SystemSettingsDto;

public interface SettingsService {

    SystemSettingsDto getSettings();

    SystemSettingsDto updateSettings(SystemSettingsDto settings);
}
