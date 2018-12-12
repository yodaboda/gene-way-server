package com.nutrinfomics.geneway.server.domain.specification;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public abstract class AbstractFoodSpecification extends EntityBase implements FoodSpecification {}
