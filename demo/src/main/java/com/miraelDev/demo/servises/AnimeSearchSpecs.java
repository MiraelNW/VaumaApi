package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.dbModels.GenreDbModel;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kotlin.Pair;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AnimeSearchSpecs {

    public static Specification<AnimeDbModel> isNameStartingWith(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), name.toUpperCase() + "%");
        };
    }

    public static Specification<AnimeDbModel> isRussianStartingWith(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.upper(root.get("russian")), name.toUpperCase() + "%");
        };
    }

    public static Specification<AnimeDbModel> isNameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + name.toUpperCase() + "%");
        };
    }

    public static Specification<AnimeDbModel> isRussianContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.upper(root.get("russian")), "%" + name.toUpperCase() + "%");
        };
    }

    public static Specification<AnimeDbModel> isGenreContains(Set<String> genres) {
        return (root, query, criteriaBuilder) -> {
            if (genres == null) return criteriaBuilder.conjunction();
            System.out.println(genres);
            query.distinct(true);
            Root<AnimeDbModel> animeDbModel = root;
            Subquery<GenreDbModel> genreSubQuery = query.subquery(GenreDbModel.class);
            Root<GenreDbModel> owner = genreSubQuery.from(GenreDbModel.class);
            Expression<Collection<AnimeDbModel>> animeGenres = owner.get("animeSet");
            genreSubQuery.select(owner);
            genreSubQuery.where(owner.get("genreRussian").in(genres), criteriaBuilder.isMember(animeDbModel, animeGenres));
            return criteriaBuilder.exists(genreSubQuery);
        };
    }

    public static Specification<AnimeDbModel> betweenYears(Map<String, Date> years) {
        return (root, query, criteriaBuilder) -> {
            if (years == null) return criteriaBuilder.conjunction();
            System.out.println(years.get("from"));
            System.out.println(years.get("to"));
            return criteriaBuilder.between(root.get("releasedOn"), years.get("from"), years.get("to"));
        };
    }

}
