package com.nutrinfomics.geneway.server.domain.specification;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
abstract public class AbstractFoodSpecification extends EntityBase implements FoodSpecification{
}
