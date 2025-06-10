package org.havoc.aipolicyinsights.repository;

import org.havoc.aipolicyinsights.model.PolicyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyRepository extends MongoRepository<PolicyDocument, String> {
}
