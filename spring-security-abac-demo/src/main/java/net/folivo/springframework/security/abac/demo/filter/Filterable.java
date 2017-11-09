package net.folivo.springframework.security.abac.demo.filter;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("dynamicFilter")
public interface Filterable {

}
