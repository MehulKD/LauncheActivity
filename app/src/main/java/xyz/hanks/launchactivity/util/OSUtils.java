package xyz.hanks.launchactivity.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class OSUtils {

    private static String TAG = OSUtils.class.getSimpleName();

    public enum ROM_TYPE {
        MIUI_ROM,
        FLYME_ROM,
        EMUI_ROM,
        OTHER_ROM
    }

    /**
     *
     * MIUI ROM标识
     * "ro.miui.ui.version.code" -> "5"
     * "ro.miui.ui.version.name" -> "V7"
     * "ro.miui.has_handy_mode_sf" -> "a"
     * "ro.miui.has_real_blur" -> "a"
     *
     * Flyme ROM标识
     * "ro.build.user" -> "flyme"
     * "persist.sys.use.flyme.icon" -> "true"
     * "ro.flyme.published" -> "true"
     * "ro.build.display.id" -> "Flyme OS 5.a.2.0U"
     * "ro.meizu.setupwizard.flyme" -> "true"
     *
     * EMUI ROM标识
     * "ro.build.version.emui" -> "EmotionUI_1.6"
     */

    //MIUI标识
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String ROM_MI = "Xiaomi";
    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String ROM_HUAWEI = "HUAWEI";
    //Flyme标识
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

    /**
     * @param
     * @return ROM_TYPE ROM类型的枚举
     * @datecreate at 2016/5/11 0011 9:46
     * @mehtodgetRomType
     * @description获取ROM类型，MIUI_ROM,    *FLYME_ROM,    * EMUI_ROM,    * OTHER_ROM
     */

    public static ROM_TYPE getRomType() {
        ROM_TYPE rom_type = ROM_TYPE.OTHER_ROM;
        Logs.i("getRomType: "+getRom());
        try {
            BuildProperties buildProperties = BuildProperties.getInstance();


            if (getRom().equalsIgnoreCase(ROM_HUAWEI)){
                 Logs.i("getRomType: EMUI_ROM");
                return ROM_TYPE.EMUI_ROM;
            }
            if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE)) {
                 Logs.i("getRomType: EMUI_ROM");
                return ROM_TYPE.EMUI_ROM;
            }


            if (getRom().equalsIgnoreCase(ROM_MI)){
                 Logs.i("getRomType: MIUI_ROM");
                return ROM_TYPE.MIUI_ROM;
            }
            if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME)) {
                 Logs.i("getRomType: MIUI_ROM");
                return ROM_TYPE.MIUI_ROM;
            }


            if (buildProperties.containsKey(KEY_FLYME_ICON_FALG) || buildProperties.containsKey(KEY_FLYME_SETUP_FALG) || buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
                 Logs.i("getRomType: FLYME_ROM");
                return ROM_TYPE.FLYME_ROM;
            }
            if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {
                String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);
                if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                     Logs.i("getRomType: FLYME_ROM");
                    return ROM_TYPE.FLYME_ROM;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
         Logs.i("getRomType: other");
        return rom_type;
    }


    public static class BuildProperties {

        private static BuildProperties ourInstance;

        public static BuildProperties getInstance() throws IOException {
            if (ourInstance == null) ourInstance = new BuildProperties();
            return ourInstance;
        }

        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public boolean isEmpty() {
            return properties.isEmpty();

        }

        public Enumeration keys() {
            return properties.keys();

        }

        public Set keySet() {
            return properties.keySet();

        }

        public int size() {
            return properties.size();

        }

        public Collection values() {
            return properties.values();
        }

    }


    /**
     *
     */
    public static String getRom(){
        String romType = android.os.Build.MANUFACTURER;
        return romType;//rom定制商的名称
    }

}
