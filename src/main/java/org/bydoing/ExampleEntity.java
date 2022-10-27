package org.bydoing;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cacheable
@Table(name = "exampletable")
public class ExampleEntity extends PanacheEntityBase {

  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

//  	@JsonbDateFormat(value="yyyy-MM-dd'T'HH:mm:ss")
//	@JsonFormat(shape = JsonFormat.Shape.STRING,
//		pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+1")
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "exampledate")
	public OffsetDateTime exampledate;

	public static Uni<ExampleEntity> createExampleEntity(ExampleEntity exampleEntity) {

		Log.debug("******* ******* ExampleEntity.createExampleEntity entered w/ consenting: " + exampleEntity.toString());
		return Panache
			.<ExampleEntity>withTransaction(exampleEntity::persist)
			.replaceWith(exampleEntity)
			.ifNoItem()
			.after(Duration.ofMillis(100000))
			.fail()
			.onFailure()
			.transform(t -> new IllegalStateException(t));
	}

	public static Uni<List<ExampleEntity>> getAllExampleEntities() {
		return ExampleEntity
			.listAll(Sort.by("exampledate"))
			.ifNoItem()
			.after(Duration.ofMillis(10000))
			.fail()
			.onFailure()
			.recoverWithUni(Uni.createFrom().<List<PanacheEntityBase>>item(Collections.EMPTY_LIST));
	}



}