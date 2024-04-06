package io.summarizeit.backend.entity.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserFilterSpecification extends GenericFilterSpecification implements Specification<User>{
    private final static String[] searchColumns = new String[]{"first_name", "last_name", "email"};

    private final UUID organizationId;

    private final GenericCriteria genericCriteria;
    
    @Override
    public Predicate toPredicate(@NonNull final Root<User> root,
            @NonNull final CriteriaQuery<?> query,
            @NonNull final CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!genericCriteria.isEmpty())
                    predicates.add(this.toGenericPredicate(root, query, builder, genericCriteria, searchColumns));

                Join<User, Group> roleJoin = root.join("roles", JoinType.LEFT);
                predicates.add(builder.equal(roleJoin.get("organization").get("id"), organizationId));
                return query.where(predicates.toArray(new Predicate[0])).getRestriction();
    }
}
