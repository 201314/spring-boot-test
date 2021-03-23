package com.gitee.linzl.commons.tools;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.SpringDataMongoDB;

/**
 * @see org.springframework.data.util.Version
 */
@Getter
@Setter
public class VersionUtil {
    /**
     * 与基准版本比较，小于则提示升级，大于等于则不需要
     *
     * @param baseVersion  基准版本
     * @param frontVersion 前端传递的版本
     * @return true提示升级，false不需要
     */
    public static boolean compareNewVersion(String baseVersion, String frontVersion) {
        String[] versionArray1 = baseVersion.split("[.]");
        String[] versionArray2 = frontVersion.split("[.]");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);
        int diff = 0;

        while (idx < minLength
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff > 0;
    }

    /**
     * 获取当前工具类的版本,只有打包后包名带有版本号才会有值
     *
     * @return
     */
    public static String version() {
        Package pkg = VersionUtil.class.getPackage();
        String versionString = (pkg != null ? pkg.getImplementationVersion() : null);
        return versionString;
    }

    public static void main(String[] args) {
        String baseVersion = "5.2.2.3";
        String frontVersion = "5.2.2";
        System.out.println("compareNewVersion:" + compareNewVersion(baseVersion, frontVersion));
        System.out.println("version() :" + SpringDataMongoDB.version());
    }
}
