package io.summarizeit.backend.entity.specification;

import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class GroupFilterSpecification extends GenericFilterSpecification implements Specification<Group>{
    private final static String[] searchColumns = new String[]{"name"};

    private final GenericCriteria genericCriteria;

    private final UUID organizationId;
    
    @Override
    public Predicate toPredicate(@NonNull final Root<Group> root,
            @NonNull final CriteriaQuery<?> query,
            @NonNull final CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!genericCriteria.isEmpty())
                    predicates.add(this.toGenericPredicate(root, query, builder, genericCriteria, searchColumns)); 
                predicates.add(builder.equal(root.get("organization").get("id"), organizationId)); 
                return query.where(predicates.toArray(new Predicate[0])).getRestriction();
    }
}
