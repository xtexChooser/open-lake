package com.xtex.openlakejason.archive;

import com.xtex.openlakejason.archive.block.ArchiveWorldGateBlock;
import com.xtex.openlakejason.archive.dimension.ArchiveDimension;
import com.xtex.openlakejason.archive.entity.large_lake.LargeLakeEntity;

public class Archive {

    public static void init() {
        ArchiveDimension.init();
        ArchiveWorldGateBlock.init();
        LargeLakeEntity.init();
    }

    public static void initClient() {
        LargeLakeEntity.initClient();
    }

}
