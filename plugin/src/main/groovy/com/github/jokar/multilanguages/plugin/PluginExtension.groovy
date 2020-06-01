package com.github.jokar.multilanguages

class PluginExtension {
    boolean enable = true
    List<String> excludePackage = new ArrayList<>()


    @Override
    public String toString() {
        return "PluginExtension{" +
                "enable=" + enable +
                ", excludePackage=" + excludePackage +
                '}';
    }
}