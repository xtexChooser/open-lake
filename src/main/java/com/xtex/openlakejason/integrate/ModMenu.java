package com.xtex.openlakejason.integrate;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.xtex.openlakejason.config.OpenLakeConfig;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OpenLakeConfig::createConfigScreen;
    }

}
