package org.havoc.aipolicyinsights.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "policies")
public class PolicyDocument {
    @Id
    private String uuid;

    private String title;
    private String text;
    private String source; // pdf, images or text

    private Instant createdAt;

    private Long ttlInSeconds; // Mongo will auto-delete after this many seconds

    private List<String> tags;
}
