package com.payper.domain.category.domain;

import java.util.Date;
import java.util.List;

import com.payper.domain.partner.domain.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private Integer categoryId;
    private String categoryName;
    private String categoryImageUrl; // nullable

    private List<Partner> partnerList;

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}