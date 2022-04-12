# Base Microservice

Latest version: 3.2

## ReadME Overview
1. ### [Installation](#installation)
2. ### [Library overview](#library-overview)
3. ### [Usage](#usage)
    1. [Creating models](#creating-models)
    2. [Mapping DTOs and DAOs](#mapping-dtos-and-daos)
    3. [Repositories](#repositories)
    4. [Controllers](#controllers)

- ## Installation

Copy and paste this inside your `pom.xml` `dependencies` block.

		<dependency>
			<groupId>com.guru</groupId>
			<artifactId>base-microservice</artifactId>
			<version>${base.version}</version>
		</dependency>

### Maven command
```
mvn dependency:get -Dartifact=com.guru:base-microservice:version
```

- ## Registry setup
If you haven't already done so, you will need to add the below to your pom.xml file.

        <repositories>
            <repository>
                <id>gitlab-maven</id>
                <url>https://gitlab.com/api/v4/projects/19215364/packages/maven</url>
            </repository>
        </repositories>

        <distributionManagement>
            <repository>
                <id>gitlab-maven</id>
                <url>https://gitlab.com/api/v4/projects/19215364/packages/maven</url>
            </repository>

            <snapshotRepository>
                <id>gitlab-maven</id>
                <url>https://gitlab.com/api/v4/projects/19215364/packages/maven</url>
            </snapshoptRepository>
        </distributionManagement>

- ## API Configuration
### JsonBaseEntity

You need to create a `JsonBaseEntity` `class` in your project.
This class define the `jsonb` type.

```java
@TypeDefs({ @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
@MappedSuperclass
public class JsonBaseEntity {
    
}
```

### RepositoryConfiguration

To enable the base repository you need to create a `@Configuration` `class`.

```java
@Configuration
@EnableJpaRepositories(basePackages = { "com.guru.base_microservice.repository.impl",
		"com.guru.notifications_service.repository" }, repositoryBaseClass = CustomizedBaseRepositoryImpl.class)
@EnableTransactionManagement
public class RepositoryConfiguration {
    
}
```

### RestClientFilter

Add the custom filter to intercept the request and validate its headers.

```java
@Component
public class RestClientFilter extends AbstractRestClientFilter {
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    super.doFilter(request, response, chain);
  }
}

```

### TomcatConfiguration

Enable Tomcat to receive some symbols. TODO: actualize the deprecated method.

```java

@Component
public class TomcatConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(
                new TomcatConnectorCustomizer() {
                    @Override
                    public void customize(Connector connector) {
                        connector.setAttribute("relaxedPathChars", "<>[\\]^`{|}");
                        connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}");
                    }
                }
        );
    }
}
```

- ## Library overview
### BaseEntity

The `BaseEntity` `abstract class` defines the basic attributes of every model that implements it.  
It also has the annotations needed for auto-generation of its attributes.

```java
public abstract class BaseEntity {
	private UUID uuid;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Timestamp deactivatedAt;
}
```

### CustomizedBaseRepository

The `CustomizedBaseRepository` is an interface for generic CRUD operations on a repository for a specific type.  
Here is defined all the business database logic and its standards. 

```java
@NoRepositoryBean
public interface CustomizedBaseRepository<T extends BaseEntity, I> {
    T maximum();
	
    T minimum();
	
    Object count();
	
    Object sum();
	
    Object average();
	
    T maximumDeactivated();
	
    T minimumDeactivated();
	
    T findById();
	
    T findByDeactivatedId();
	
    Page<T> findAll();
	
    List<T> findAll();
	
    void saveUuid();
}

```

### BaseRepository

The `BaseRepository` is an extension of `CustomizedBaseRepository` to provide the 
additional methods of `PagingAndSortingRepository`.

```java
public interface BaseRepository<T extends BaseEntity, I> extends 
        PagingAndSortingRepository<T, I>, CustomizedBaseRepository<T, I> {

}
```

### BaseController

The `BaseController` has the default implementations for the basic operations and its mappings.

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public abstract class BaseController<D extends BaseEntity, E extends BaseEntity, I> {

    protected BaseController(BaseRepository<E, I> repo, Class<E> entityType, EntityMapper<D, E> mapper) {
        this.repo = repo;
        this.entityClass = entityType;
        this.utilsService = new UtilServiceImpl();
        this.service = new BaseServiceImpl<>(entityType, mapper, utilsService);
    }

    protected BaseController(BaseRepository<E, I> repo, Class<E> entityType) {
        this.repo = repo;
        this.entityClass = entityType;
        this.utilsService = new UtilServiceImpl();
        this.service = new BaseServiceImpl<>(entityType, mapper, utilsService);
    }

    @GetMapping(path = "/health_check")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody ObjectResponse getObject();

    @PostMapping(headers = {"accept=application/json"})
    public @ResponseBody Optional<D> createObject(@RequestBody @Valid D object, HttpServletRequest request);

    @GetMapping(headers = {"accept=application/json"})
    public List<?> getPageableObject(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) @Valid int page,
            @RequestParam(value = "per_page", defaultValue = "10") @Max(100) @Valid int perPage,
            @RequestParam(required = false) Map<String, String> orderAndFiltering, ServletRequest request);

    @GetMapping(value = "/{id}", headers = {"accept=application/json"})
    public @ResponseBody Optional<D> findObject(@PathVariable I id, HttpServletRequest request);

    @PutMapping(value = "/{id}", headers = {"accept=application/json"})
    public @ResponseBody Optional<D> updateObject(@PathVariable I id, @RequestBody D object,
                                                  HttpServletRequest request);

    @DeleteMapping(value = "/{id}", headers = {"accept=application/json"})
    public @ResponseBody DeletedObjectResponse deleteObject(@PathVariable I id, HttpServletRequest request);

    public @ResponseBody Optional<D> findDeactivatedById(I id);
}
```
# Usage

- ## Creating models
### Models

To get started, you will need a model to use this library. This new model should extend `BaseEntity`.  
The attributes should utilize java standard naming, 
for database and external usage `jackson` and `javax` will format the names.  
  
For the `data` attribute we should add the `@TypeDef` annotation to the class declaring the type `jsonb`.  
Ignore nulls and unknown attributes to avoid unexpected errors.

```java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guru.base_microservice.configuration.jsonb.JsonBinaryType;
import com.guru.base_microservice.domain.BaseEntity;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;

@Table(name = "new_object")
// Declaring jsonb type
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
// Ignoring nulls and unknowns attributes
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewObject extends BaseEntity {

    @Column(name = "new_object_info")
    @JsonProperty("new_object_info")
    private String newObjectInfo;

    // Not needed to format the name.
    private NewObjectData data;
}
```
### ModelData

When declaring a `data` field it will be mapped to a `jsonb` type in the database, 
so `javax` name formatting is not needed.

```java
import com.fasterxml.jackson.annotation.JsonProperty;

// No formatting needed
public class NewObjectData {

    @JsonProperty("map_of_strings")
    private Map<String, String> mapOfStrings;
    
    // No formatting needed
    private String message;
}
```

### ModelDto

For the `MVC` pattern whe define a `DTO` of the model.  
This dto does not have `data` field. Instead, all the data fields are inside the actual DTO.  
We will map DTOs to DAOs later with `mapstruct`.  
  
Extending `BaseEntity` will add the basic attributes.

As indicated in the MVC pattern, DTOs does not interact with databases so `javax` name format is not needed.

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guru.base_microservice.domain.BaseEntity;

public class NewObjectDto extends BaseEntity {

    @JsonProperty("new_object_info")
    private String newObjectInfo;
    // Data attributes
    @JsonProperty("map_of_strings")
    private Map<String, String> mapOfStrings;
    // No formatting needed
    private String message;
}
```

- ## Mapping DTOs and DAOs

For mappings, we will use `mapstruct` extending the `EntityMapper` from the library.

```java
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewObjectMapper extends EntityMapper<NewObjectDto, NewObject> {
    NewObjectMapper INSTANCE = Mappers.getMapper(NewObjectMapper.class);

    // Here we declare all the attributes from the Data object.
    // If we don't declare an attribute it will be ignored by the DAO and may cause unexpected errors.
    @Mapping(source = "mapOfStrings", target = "data.mapOfStrings")
    @Mapping(source = "message", target = "data.message")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NewObject toEntity(NewObjectDto dto);

    @InheritInverseConfiguration
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    NewObjectDto toDto(NewObject entity);
}
```

- ## Repositories

Once model and mappings are finished we can create the repositories extending `BaseRepository`.

```java
import com.guru.base_microservice.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewObjectRepository extends BaseRepository<NewObject, UUID> {

}
```

- ## Controllers

Now we have all the needs to create a REST controller extending `BaseController`.
    
Inject the `repository` to the constructor, then pass it to the `super` constructor.  
Instantiate a `mapper` and pass it with the DAO class to the `super` constructor too.


```java
import com.guru.base_microservice.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class NewObjectController extends BaseController<NewObjectDto, NewObject, UUID> {

    @Autowired
    public NewObjectController(NewOjbectRepository repo) {
        super(repo, NewObject.class, NewObjectMapper.INSTANCE);
    }
}
```

Now we can test our application with the basic methods, `GET`, `POST`, `PUT`, `DELETE`.
