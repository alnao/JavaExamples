package it.alnao.javaexamples.awssdk.utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.StringUtils;

public class RegionUtils {
    public static Region getRegionOrDefault(String region){
        if (StringUtils.isBlank(region)) {
            return Region.EU_CENTRAL_1;
        }
        return Region.of(region);
    }
}
