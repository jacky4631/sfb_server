/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.github.xiaoymin.knife4j.spring.util;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.refs.GenericRef;
import io.swagger.models.refs.RefType;

/**
 * @author ：LionCity
 * @date ：2019/9/24
 * @description： 同时拥有ArrayProperty和RefProperty的特性
 */
public class ArrayRefProperty extends ArrayProperty {
    private GenericRef genericRef;

    public String get$ref() {
        return genericRef.getRef();
    }

    public void set$ref(String ref) {
        this.genericRef = new GenericRef(RefType.DEFINITION, ref);

        // $ref
        RefProperty items = new RefProperty();
        items.setType(ref);
        items.set$ref(ref);
        this.items(items);
    }
}
