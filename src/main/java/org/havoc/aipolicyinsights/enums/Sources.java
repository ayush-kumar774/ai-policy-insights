package org.havoc.aipolicyinsights.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sources {
    PDF("pdf"),
    IMAGE("image"),
    TEXT("text");

    private final String name;

}
