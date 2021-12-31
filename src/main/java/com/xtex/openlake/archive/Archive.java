package com.xtex.openlake.archive;

import com.xtex.openlake.archive.block.ArchiveWorldGateBlock;
import com.xtex.openlake.archive.dimension.ArchiveDimension;
import com.xtex.openlake.archive.entity.large_lake.LargeLakeEntity;

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
