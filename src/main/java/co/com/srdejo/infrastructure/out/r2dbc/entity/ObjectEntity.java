package co.com.srdejo.infrastructure.out.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("object_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ObjectEntity {
    @Id
    @Column("object_id")
    private Long id;

    private String name;
}
