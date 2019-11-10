package com.github.jokar.multilanguages

class PluginExtension {
    boolean enable = true
    List<String> overwriteClass = new ArrayList<>()


    @Override
    String toString() {
        return "PluginExtension{" +
                "enable=" + enable +
                ", overwriteClass=" + overwriteClass.toString() +
                '}'
    }
}