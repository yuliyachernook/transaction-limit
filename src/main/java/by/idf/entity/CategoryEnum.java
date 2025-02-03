package by.idf.entity;

import lombok.Getter;

@Getter
public enum CategoryEnum {
    PRODUCT("PRODUCT") ,
    SERVICE("SERVICE");

    private final String name;

    CategoryEnum(String name) {
        this.name = name;
    }
}