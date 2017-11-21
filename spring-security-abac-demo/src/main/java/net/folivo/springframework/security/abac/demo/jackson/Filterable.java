package net.folivo.springframework.security.abac.demo.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("dynamicFilter")
public interface Filterable {

}
