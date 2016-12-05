package net.dongliu.apk.parser.model;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;

public class IntentFilter {

    public final List<String> actions = new ArrayList<>();
    public final List<String> categories = new ArrayList<>();
    public final List<IntentData> dataList = new ArrayList<>();
    public NamedNodeMap attributes;

    public static class IntentData {

        public final String scheme;
        public final String host;
        public final String port;
        public final String path;
        public final String pathPattern;
        public final String pathPrefix;
        public final String mimeType;
        public final String type;

        public IntentData(String scheme, String host, String port, String path, String pathPattern, String pathPrefix,
                          String mimeType, String type) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
            this.path = path;
            this.pathPattern = pathPattern;
            this.pathPrefix = pathPrefix;
            this.mimeType = mimeType;
            this.type = type;
        }

        @Override public String toString() {
            return "IntentData{" +
                    "scheme='" + scheme + '\'' +
                    ", host='" + host + '\'' +
                    ", port='" + port + '\'' +
                    ", path='" + path + '\'' +
                    ", pathPattern='" + pathPattern + '\'' +
                    ", pathPrefix='" + pathPrefix + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

    }
}