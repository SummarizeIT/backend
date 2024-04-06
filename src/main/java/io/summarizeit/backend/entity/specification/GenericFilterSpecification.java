package io.summarizeit.backend.entity.specification;

import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class GenericFilterSpecification {
    public Predicate toGenericPredicate(@NonNull final Root<?> root,
            @NonNull final CriteriaQuery<?> query,
            @NonNull final CriteriaBuilder builder,
            @NotNull final GenericCriteria criteria,
            @NotNull final String[] searchColumns) {
        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getIds() != null) {
            predicates.add(root.get("id").in((Object[]) criteria.getIds()));
        }

        if (criteria.getSearch() != null) {
            String q = String.format("%%%s%%", criteria.getSearch().toLowerCase());
            ArrayList<Predicate> searchPredicates = new ArrayList<>();
            for (String attributeName : searchColumns) {
                searchPredicates.add(builder.like(builder.lower(root.get(attributeName).as(String.class)), q));
            }
            predicates.add(builder.or(searchPredicates.toArray(new Predicate[0])));
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return query.distinct(true).getRestriction();
    }
}
