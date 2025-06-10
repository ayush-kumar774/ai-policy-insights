package org.havoc.aipolicyinsights.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.aipolicyinsights.exception.BadRequestException;
import org.havoc.aipolicyinsights.model.PolicyDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyQueryService {

    private final MongoTemplate mongoTemplate;

    public List<PolicyDocument> searchPolicies(String keyword, String source, List<String> tags, Instant fromDate, Instant toDate) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(keyword)) {
            String cleanKeyword = keyword.trim();
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(Pattern.quote(cleanKeyword), "i"),
                    Criteria.where("text").regex(Pattern.quote(cleanKeyword), "i")
            );
            criteriaList.add(keywordCriteria);
        }

        if (StringUtils.hasText(source)) {
            criteriaList.add(Criteria.where("source").is(source));
        }


        if (tags != null) {
            log.info("🧪 Raw tags param: {}", tags);

            List<String> nonEmptyTags = tags.stream()
                    .map(tag -> Objects.isNull(tag) ? null : tag.replace("\"", "").trim())
                    .filter(StringUtils::hasText)
                    .toList();

            log.info("✅ Filtered non-empty tags: {}", nonEmptyTags);

            if (nonEmptyTags.isEmpty()) {
                log.warn("❌ 'tags' param was present but empty or invalid. Rejecting request.");
                throw new BadRequestException("'tags' parameter is provided but contains no valid entries. Please pass at least one non-empty tag.");
            }

            criteriaList.add(Criteria.where("tags").in(nonEmptyTags));
        }




        if (Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            criteriaList.add(Criteria.where("createdAt").gte(fromDate).lte(toDate));
        }
        else if (Objects.nonNull(fromDate)) {
            criteriaList.add(Criteria.where("createdAt").gte(fromDate));
        }
        else if (Objects.nonNull(toDate)) {
            criteriaList.add(Criteria.where("createdAt").lte(toDate));
        }


        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));


        log.info("🔍 Running search query: {}", query);

        return mongoTemplate.find(query, PolicyDocument.class);
    }
}
